package com.jadaptive.plugins.debrep;

import static com.jadaptive.plugins.debrep.OSCommand.runAndCheckExit;
import static com.jadaptive.plugins.debrep.OSCommand.runCommandAndCaptureOutput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.template.SortOrder;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantAware;

@Service
public class GPGKeyResourceServiceImpl extends AbstractUUIDObjectServceImpl<GPGKeyResource>
		implements GPGKeyService, TenantAware, StartupAware {

	@Autowired
	private SingletonObjectDatabase<DebrepConfiguration> config;
	
	@Autowired
	private EventService eventService; 
	
	final static Logger LOG = LoggerFactory.getLogger(GPGKeyResourceServiceImpl.class);

	private ThreadLocal<Boolean> deleted = new ThreadLocal<>();
	private ThreadLocal<Boolean> created = new ThreadLocal<>();

	@Override
	public void initializeTenant(Tenant tenant, boolean newSchema) {
		
		try {
			syncWithKeystore();		
		} catch (Exception e) {
			LOG.error("Failed to synchronized realms for GPG configuration.", e);
		}
	}
	
	protected Class<GPGKeyResource> getResourceClass() {
		return GPGKeyResource.class;
	}

	@Override
	public String getPublicContent(GPGKeyResource resource) {
		final File realmGPGHomeDir = getRealmGPGHomeDir();
		if (realmGPGHomeDir.exists()) {
				return String.join("\n", runCommandAndCaptureOutput("gpg", "--homedir",
						realmGPGHomeDir.getAbsolutePath(), "--armour", "--export", resource.getFingerprint())) + "\n\n";
		}
		throw new IllegalStateException("Publish of keys failed, no directory for GPG keys.");
	}

	@Override
	public void publishKey(GPGKeyResource resource) {

		final File realmGPGHomeDir = getRealmGPGHomeDir();
		if (realmGPGHomeDir.exists()) {
			var args = Arrays.asList("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--keyserver",
					config.getObject(DebrepConfiguration.class).getKeyServer(), "--send-keys",
					resource.getFingerprint());
			LOG.info(String.format("Executing command: %s", String.join(" ", args)));
			runAndCheckExit(args);
			return;
		}
		throw new IllegalStateException("Publish of keys failed, no directory for GPG keys.");
	}

	@Override
	public Iterable<GPGKeyResource> getKeys() {
		return objectDatabase.list(GPGKeyResource.class, SearchField.eq("recordType", GPGRecordType.pub.name()));
	}

	@Override
	public Iterable<GPGKeyResource> getSigningKeys() {
		return objectDatabase.list(GPGKeyResource.class, SearchField.in("recordType", GPGRecordType.pub.name(), GPGRecordType.sub.name()));
	}

	@Override
	public Iterable<GPGKeyResource> searchKeys(String searchColumn, String search, int start, int length) {
		return objectDatabase.searchTable(GPGKeyResource.class, start, length, SortOrder.ASC, "", 
				SearchField.eq(searchColumn, search),
				SearchField.eq("recordType", GPGRecordType.pub.name()));
	}

	@Override
	public long getKeyCount(String searchColumn, String search) {
		return objectDatabase.searchCount(GPGKeyResource.class,  
				SearchField.eq(searchColumn, search),
				SearchField.eq("recordType", GPGRecordType.pub.name()));
	}

	protected void syncWithKeystore() throws IOException {
		Set<String> exist = new HashSet<>();

		final File realmGPGHomeDir = getRealmGPGHomeDir();
		updateDir(realmGPGHomeDir);
		LOG.info(String.format("Realm GPG configuration for tenant %s from %s", getCurrentTenant().getName(), realmGPGHomeDir));
		if (realmGPGHomeDir.exists()) {
			syncKeyType(exist, realmGPGHomeDir, null, "--list-keys");
			syncKeyType(exist, realmGPGHomeDir, null, "--list-secret-keys");
		}

		/* Remove all that no longer exist */
		deleted.set(Boolean.TRUE);
		try {
			streamAll().filter(r -> !exist.contains(r.getFingerprint())).forEach(this::deleteObject);
		} finally {
			deleted.remove();
		}
	}

	protected void syncKeyType(Set<String> exist, final File realmGPGHomeDir, GPGKeyResource gpg,
			String keyTypeArg) throws IOException {
		for (String line : runCommandAndCaptureOutput("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(),
				keyTypeArg, "--with-colons").split("\n")) {
			String[] data = line.split(":");

			if (data[0].equals("gpg")) {
				/* Warning output */
				LOG.warn(line);
				continue;
			}

			GPGRecordType recordType = GPGRecordType.valueOf(data[0]);
			if (recordType.equals(GPGRecordType.sec) || recordType.equals(GPGRecordType.ssb)
					|| recordType.equals(GPGRecordType.pub) || recordType.equals(GPGRecordType.sub)) {
				/* Write out previous key */
				if (gpg != null) {
					createOrUpdate(gpg);
					exist.add(gpg.getFingerprint());
				}

				/* Field 5 is the 64bit key ID and the last 64 bit of the SHA-1 fingerprint */
				String fp = data[4];

				GPGKeyResource last = gpg;

				gpg = new GPGKeyResource();
				gpg.setRecordType(recordType);
				gpg.setValidity(data[1].length() > 0 ? GPGValidity.fromCode(data[1]) : GPGValidity.NONE);
				gpg.setKeyLength(Integer.parseInt(data[2]));
				gpg.setPublicKeyAlgo(GPGKeyAlgo.fromCode(Integer.parseInt(data[3])));
				gpg.setFingerprint(fp);
				if (data.length > 5 && StringUtils.isNotBlank(data[5])) {
					try {
						gpg.setCreationDate(new Date(Long.parseLong(data[5]) * 1000));
					} catch (NumberFormatException nfe) {
						try {
							gpg.setCreationDate(new SimpleDateFormat("yyyy-MM-dd").parse(data[5]));
						} catch (ParseException e) {
							LOG.warn(String.format("Could not parse creation date.", data[5]), e);
							gpg.setCreationDate(null);
						}
					}
				} else
					gpg.setCreated(null);
				if (data.length > 6 && StringUtils.isNotBlank(data[6])) {
					try {
						gpg.setExpirationDate(new Date(Long.parseLong(data[6]) * 1000));
					} catch (NumberFormatException nfe) {
						try {
							gpg.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse(data[6]));
						} catch (ParseException e) {
							LOG.warn(String.format("Could not parse expiration date.", data[6]), e);
							gpg.setCreationDate(null);
						}
					}
				} else
					gpg.setExpirationDate(null);
				if (data.length > 7)
					gpg.setInfo(data[7]);
				if (data.length > 8)
					gpg.setOwnerTrust(data[8]);
				if (data.length > 10) {
					gpg.setSignatureClass(data[10]);
					if (data.length > 11) {
						gpg.setKeyCapabilities(data[11]);
					}
				}

				if (last != null && (recordType == GPGRecordType.sub || recordType == GPGRecordType.ssb)
						&& (last.getRecordType() == GPGRecordType.pub || last.getRecordType() == GPGRecordType.sec)) {
					gpg.setComment(last.getComment());
					gpg.setParent(last);
					gpg.setFullName(last.getFullName());
					gpg.setEmail(last.getEmail());
				}
				else if(data.length > 9 && ( recordType == GPGRecordType.pub || recordType == GPGRecordType.sub)) {
					GPGComment cmt = new GPGComment(data[9]);
					gpg.setEmail(cmt.email);
					gpg.setFullName(cmt.fullName);
					gpg.setComment(cmt.comment);
				}
			} else if (recordType.equals(GPGRecordType.fpr)) {
				/* Contains full finger print (happens after pub) */
				if (gpg == null)
					throw new IllegalStateException("Unexpected ordering of key database.");
				else {
					gpg.setFingerprint(data[9]);
				}
			} else if (recordType.equals(GPGRecordType.uid)) {
				GPGComment cmt = new GPGComment(data[9]);
				gpg.setEmail(cmt.email);
				gpg.setFullName(cmt.fullName);
				gpg.setComment(cmt.comment);
			}
		}
		if (gpg != null) {
			createOrUpdate(gpg);
			exist.add(gpg.getFingerprint());
		}
	}

	protected void syncXXXKeyType(Set<String> exist, final File realmGPGHomeDir, 
			String keyTypeArg) throws IOException {
		GPGKeyResource gpg = null;
		GPGKeyResource last = null;
		List<GPGRecordType> supportedTypes = Arrays.asList(GPGRecordType.pub, GPGRecordType.uid, GPGRecordType.fpr);
		
		for (String line : runCommandAndCaptureOutput("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(),
				keyTypeArg, "--with-colons").split("\n")) {
			System.out.println("ZZZZZZ: " + line);
			if(line.equals(""))
				return;
			
			String[] data = line.split(":");

			if (data[0].equals("gpg")) {
				/* Warning output */
				LOG.warn(line);
				continue;
			}

			GPGRecordType recordType = GPGRecordType.valueOf(data[0]);

			if(!supportedTypes.contains(recordType)) {
				continue;
			}
			
			
			if (recordType.equals(GPGRecordType.pub)) {
				/* Write out previous key */
				if (gpg != null) {
					System.out.println("Writing: " + gpg);
					createOrUpdate(gpg);
					exist.add(gpg.getFingerprint());
				}

				/* Field 5 is the 64bit key ID and the last 64 bit of the SHA-1 fingerprint */
				String fp = data[4];

				gpg = new GPGKeyResource();
				gpg.setRecordType(recordType);
				gpg.setValidity(data[1].length() > 0 ? GPGValidity.fromCode(data[1]) : GPGValidity.NONE);
				gpg.setKeyLength(Integer.parseInt(data[2]));
				gpg.setPublicKeyAlgo(GPGKeyAlgo.fromCode(Integer.parseInt(data[3])));
				gpg.setFingerprint(fp);
				if (data.length > 5 && StringUtils.isNotBlank(data[5])) {
					try {
						gpg.setCreationDate(new Date(Long.parseLong(data[5]) * 1000));
					} catch (NumberFormatException nfe) {
						try {
							gpg.setCreationDate(new SimpleDateFormat("yyyy-MM-dd").parse(data[5]));
						} catch (ParseException e) {
							LOG.warn(String.format("Could not parse creation date.", data[5]), e);
							gpg.setCreationDate(null);
						}
					}
				} else
					gpg.setCreated(null);
				if (data.length > 6 && StringUtils.isNotBlank(data[6])) {
					try {
						gpg.setExpirationDate(new Date(Long.parseLong(data[6]) * 1000));
					} catch (NumberFormatException nfe) {
						try {
							gpg.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd").parse(data[6]));
						} catch (ParseException e) {
							LOG.warn(String.format("Could not parse expiration date.", data[6]), e);
							gpg.setCreationDate(null);
						}
					}
				} else
					gpg.setExpirationDate(null);
				if (data.length > 7)
					gpg.setInfo(data[7]);
				if (data.length > 8)
					gpg.setOwnerTrust(data[8]);
				if (data.length > 10) {
					gpg.setSignatureClass(data[10]);
					if (data.length > 11) {
						gpg.setKeyCapabilities(data[11]);
					}
				}

				if (last != null) {

					System.out.println("   last is " + last);
					
					gpg.setComment(last.getComment());
					gpg.setFullName(last.getFullName());
					gpg.setEmail(last.getEmail());
					gpg.setName(recordType + ":" + last.getFullName() + " <" + last.getEmail() + ">");
				}
				else if(data.length > 9 && ( recordType == GPGRecordType.pub)) {
					last = gpg;
					GPGComment cmt = new GPGComment(data[9]);
					last.setEmail(cmt.email);
					last.setFullName(cmt.fullName);
					last.setComment(cmt.comment);

					System.out.println("   comment "  +cmt);
				} 
				else {
					System.out.println("Not writing");
					gpg = null;
				}
					
			} else if (last != null && recordType.equals(GPGRecordType.fpr)) {
				/* Contains full finger print (happens after pub) */
				if (gpg == null)
					throw new IllegalStateException("Unexpected ordering of key database.");
				else {
					last.setFingerprint(data[9]);
					System.out.println("   FPR: " + data[9]);
				}
			} else if (last != null && recordType.equals(GPGRecordType.uid)) {
				GPGComment cmt = new GPGComment(data[9]);
				last.setEmail(cmt.email);
				last.setFullName(cmt.fullName);
				last.setComment(cmt.comment);
				System.out.println("   uid comment "  +cmt);
			} else {
				System.out.println("Unexpected GPG data: " + line);
			}
		}
		if (gpg != null) {
			createOrUpdate(gpg);
			exist.add(gpg.getFingerprint());
		}
	}
	
	class GPGComment {
		String email;
		String fullName;
		String comment;
		
		GPGComment(String userId) {

			/* The user ID consists of [name] ([comment]) <email> */
			int idx = userId.lastIndexOf('<');
			if (idx != -1) {
				final int eidx = userId.lastIndexOf('>');
				if (eidx != -1) {
					email = userId.substring(idx + 1, eidx);
					userId = userId.substring(0, idx).trim();
				}
			}
			idx = userId.lastIndexOf('(');
			if (idx != -1) {
				final int eidx = userId.lastIndexOf(')');
				if (eidx != -1) {
					comment = userId.substring(idx + 1, eidx);
					userId = userId.substring(0, idx).trim();
				}
			}
			fullName = userId;
		}

		@Override
		public String toString() {
			return "GPGComment [email=" + email + ", fullName=" + fullName + ", comment=" + comment + "]";
		}
		
	}

	protected void createOrUpdate(GPGKeyResource gpg)  {
		GPGKeyResource res ;
		try {
			res = objectDatabase.get(GPGKeyResource.class, SearchField.eq("name", gpg.getName()));
		}
		catch(ObjectNotFoundException onfe) {
			res = null;
		}
		GPGKeyResource parent = gpg.getParent();
		if (parent != null && parent.getUuid() == null) {
			parent = objectDatabase.get(GPGKeyResource.class, SearchField.eq("name", parent.getName()));
		}
		if (res == null) {
			created.set(Boolean.TRUE);
			try {
				gpg.setParent(parent);
				objectDatabase.saveOrUpdate(gpg);
				System.out.println("Created: " + gpg.getRecordType() + ":" + gpg.getUuid() + " : " + gpg.getName());
			} finally {
				created.remove();
			}
		} else {
			res.setEmail(gpg.getEmail());
			res.setFingerprint(gpg.getFingerprint());
			res.setExpirationDate(gpg.getExpirationDate());
			res.setFullName(gpg.getFullName());
			res.setInfo(gpg.getInfo());
			res.setIssuerCertificateFingerprint(gpg.getIssuerCertificateFingerprint());
			res.setKeyCapabilities(gpg.getKeyCapabilities());
			res.setKeyLength(gpg.getKeyLength());
			res.setComment(gpg.getComment());
			res.setParent(parent);
			res.setOwnerTrust(gpg.getOwnerTrust());
			res.setSignatureClass(gpg.getSignatureClass());
			res.setValidity(gpg.getValidity());
			objectDatabase.saveOrUpdate(res);
			System.out.println("Updated: " + gpg.getRecordType() + ":" + gpg.getUuid() + " : " + gpg.getName());
		}
	}

	public File getRealmGPGHomeDir() {
		return new File(new File("conf.d", "gpg"), getCurrentTenant().getUuid());
	}

	@Override
	public GPGKeyResource getKeyByFingerprint(String fp) {
		return objectDatabase.get(getResourceClass(), SearchField.eq("fingerprint", fp));
	}

	@Override
	public GPGKeyResource importKey(InputStream in) {
		final File realmGPGHomeDir = getRealmGPGHomeDir();
		updateDir(realmGPGHomeDir);
		try {
			var args = Arrays.asList("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--import");
			LOG.info(String.format("Executing command: %s", String.join(" ", args)));
			var fb = new ProcessBuilder(args);
			fb.redirectError(Redirect.INHERIT);
			fb.redirectInput(Redirect.PIPE);
			fb.redirectOutput(Redirect.INHERIT);
			var p = fb.start();
			
			try(var out = p.getOutputStream()) {
				in.transferTo(out);
			}
			
			if(p.waitFor() != 0)
				throw new IOException(String.format("GPG import exited with status code %d",p.exitValue()));
			
			syncWithKeystore();
			// TODO
			return allObjects().iterator().next();
		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Publish of keys failed.", e);
		}
	}

	private void updateDir(final File realmGPGHomeDir) {
		realmGPGHomeDir.mkdirs();
		realmGPGHomeDir.setReadable(true, true);
		realmGPGHomeDir.setWritable(true, true);
	}

	@Override
	public void onApplicationStartup() {
		
		eventService.deleting(GPGKeyResource.class, (e)-> {
			for(GPGKeyResource r : allObjects()) {
				if(e.getObject().equals(r.getParent())) {
					final File realmGPGHomeDir = getRealmGPGHomeDir();
					if (realmGPGHomeDir.exists()) {
						runAndCheckExit("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--batch",  "--delete-secret-keys",
								r.getFingerprint());
						runAndCheckExit("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--batch", "--delete-key",
								r.getFingerprint());
					}
					objectDatabase.delete(r);
				}
			}
			
			if (!Boolean.TRUE.equals(deleted.get())) {
				final File realmGPGHomeDir = getRealmGPGHomeDir();
				if (realmGPGHomeDir.exists()) {
					runAndCheckExit("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--batch",  "--delete-secret-keys",
							e.getObject().getFingerprint());
					runAndCheckExit("gpg", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--batch", "--delete-key",
							e.getObject().getFingerprint());
				}
			}
		});
		
		eventService.creating(GPGKeyResource.class, (e)->{
			
			if (Boolean.TRUE.equals(created.get()))
				return;

			GPGKeyResource resource = e.getObject();
			try {
				File tf = File.createTempFile("gpg", ".dat");
				try (PrintWriter pw = new PrintWriter(new FileWriter(tf), true)) {
					pw.println("%echo Generating PGP key");
					pw.println("%no-protection");
					switch (resource.getPublicKeyAlgo()) {
					case RSA_RSA:
						pw.println("Key-Type: RSA");
						pw.println("Key-Length: " + resource.getKeyLength());
						pw.println("Subkey-Type: RSA");
						pw.println("Subkey-Length: " + resource.getKeyLength());
						break;
					case RSA_SIGN_ONLY:
						pw.println("Key-Type: RSA");
						pw.println("Key-Length: " + resource.getKeyLength());
						break;
					case DSA_ELGAMAL:
						pw.println("Key-Type: DSA");
						pw.println("Key-Length: " + resource.getKeyLength());
						pw.println("Subkey-Type: Elgamal");
						pw.println("Subkey-Length: " + resource.getKeyLength());
						break;
					default:
						pw.println("Key-Type: DSA");
						pw.println("Key-Length: " + resource.getKeyLength());
						break;
					}
					pw.println("Name-Real: " + resource.getFullName());
					if(StringUtils.isNotBlank(resource.getComment()))
						pw.println("Name-Comment: " + resource.getComment());
					pw.println("Name-Email: " + resource.getEmail());
					pw.println("Expire-Date: "
							+ (resource.getExpirationDate() == null ? 0 : (resource.getExpirationDate().getTime() / 1000)));
					pw.println("%commit");
					pw.println("%echo done");
				}

				final File realmGPGHomeDir = getRealmGPGHomeDir();
				if (!realmGPGHomeDir.exists()) {
					if (realmGPGHomeDir.mkdirs())
						runAndCheckExit("chmod", "go-rwx", realmGPGHomeDir.getAbsolutePath());
					else
						throw new IllegalStateException(
								String.format("Failed to create GPG directory %s.", realmGPGHomeDir));
				}

				/*
				 * Start a background thread that interrupts if key generation seems to be
				 * taking a long time. This may be caused if enough entropy cannot be gained.
				 * This may be fixed on Linux by install rng-tools and starting rngd.
				 */
				final Thread thisThread = Thread.currentThread();
				Thread oThread = new Thread() {
					public void run() {
						try {
							Thread.sleep(60000);

							/* Timed out, interrupt main */
							thisThread.interrupt();
						} catch (InterruptedException ie) {
							/* Interrupted, it finished OK */
						}
					}
				};
				oThread.start();
				realmGPGHomeDir.mkdirs();
				try {
					runAndCheckExit("gpg", "--cert-digest-algo", "SHA512", "--default-preference-list",
							"SHA512 SHA384 SHA256 SHA224 AES256 AES192 AES CAST5 BZIP2 ZLIB ZIP Uncompressed", "--homedir",
							realmGPGHomeDir.getAbsolutePath(), "--no-tty", "--yes", "--passphrase", "", "--batch",
							"--gen-key", tf.getAbsolutePath());
				} finally {
					oThread.interrupt();
				}

				/* All OK, sync the realm to update and fill in missing key details */
				syncWithKeystore();
			} catch (Exception ex) {
				throw new IllegalStateException("Failed to generate GPG key.", ex);
			}

			
			
		});
	}
}
