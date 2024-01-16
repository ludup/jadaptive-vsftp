package com.jadaptive.plugins.debrep;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.scheduler.SchedulerService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantAware;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.direct.NioFileFactory;
import com.sshtools.common.files.direct.NioFileFactory.NioFileFactoryBuilder;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.forker.client.OSCommand;
import com.sshtools.synergy.s3.S3AbstractFileFactory;
import com.sshtools.synergy.s3.S3File;

@Service
public class DebianRepositoryServiceImpl extends AbstractUUIDObjectServceImpl<DebianRepository>
		implements DebianRepositoryService, StartupAware, TenantAware {

	final static Logger LOG = LoggerFactory.getLogger(DebianRepositoryServiceImpl.class);

	@Autowired
	private GPGKeyService gPGKeyResourceService;
	
	@Autowired
	private VirtualFileService virtualFileService;
	
	@Autowired
	private SchedulerService schedulerService;
	
	@Autowired
	private EventService eventService;
	
	private Set<DebianRepository> pushing = Collections.synchronizedSet(new HashSet<>());
	private List<Runnable> onPush = new LinkedList<>();


	@Override
	public void onApplicationStartup() {
		
		eventService.created(DebianRepository.class, (e)->{
			
			DebianRepository resource = e.getObject();
			final File realmRepoHomeDir = getRealmRepositoryHomeDir();
			if (!realmRepoHomeDir.exists()) {
				if (!realmRepoHomeDir.mkdirs())
					throw new IllegalStateException(
							String.format("Failed to create realm repository directory %s.", realmRepoHomeDir));
			}

			final VirtualFolder remoteRepository = resource.getRemoteRepository();
			if (remoteRepository == null) {
				new File(new File(realmRepoHomeDir, resource.getName()), "conf").mkdirs();
			} else {
				try {
					
					NioFileFactory localFileSystem = NioFileFactoryBuilder.create()
														.withHome(realmRepoHomeDir)
														.build();					
					LOG.info(String.format("Resolving remote location %s", remoteRepository));
					AbstractFile remote = virtualFileService.resolveMount(remoteRepository).getDefaultPath();
					if (remote == null)
						throw new FileNotFoundException(
								String.format("Cannot find remote repository %s", remoteRepository.getName()));
					LOG.info(String.format("Resolving local location %s", realmRepoHomeDir));
					AbstractFile src = localFileSystem.getDefaultPath();

					LOG.info(String.format("Started copying from %s to %s", remote, src));
					src.copyFrom(remote);
					LOG.info(String.format("Completed copying from %s to %s", remote, src));
				} catch (IOException | PermissionDeniedException fse) {
					throw new IllegalStateException(fse.getMessage(), fse);
				}
			}

			rebuildConfiguration(resource);
		});
		
		eventService.updated(DebianRepository.class, (e)->{
			rebuildConfiguration(e.getObject());
		});
		
		// TODO implement cascades
//		eventService.deleting(DebianRepository.class, (e)->{
//			for (DebianRelease r : e.getObject().getReleases()) {
//				r.getRepositories().remove(e.getObject());
//			}
//		});
	}
	
	

	@Override
	public void initializeSystem(boolean newSchema) {
		initializeTenant(getCurrentTenant(), newSchema);
	}

	@Override
	public void initializeTenant(Tenant tenant, boolean newSchema) {
		
		try {
			for (DebianRepository rep : allObjects()) {
				retryIncoming(rep);
			}
		} catch (Exception e) {
			LOG.error("Failed to check for orphaned uploads.", e);
		}		
	}

	public File getRealmRepositoryHomeDir() {
		return new File(new File("conf.d", "debrep"), getCurrentTenant().getUuid());
	}

	private File getRealmRepositoryIncomingDir() {
		return new File(new File("conf.d", "debrep-incoming"), getCurrentTenant().getUuid());
	}

	private File getReleaseIncomingDir(DebianRepository repo, DebianRelease release) {
		return new File(getRepositoryIncomingDir(repo), release.getName());
	}

	private File getRepositoryIncomingDir(DebianRepository repo) {
		return new File(getRealmRepositoryIncomingDir(), repo.getName());
	}

	@Override
	public void retryIncoming(DebianRepository rep) throws IOException {
		for (DebianRelease r : rep.getReleases()) {
			File inc = getReleaseIncomingDir(rep, r);
			List<File> debs = new ArrayList<>();
			if (inc.exists() && inc.isDirectory()) {
				debs.addAll(Arrays.asList(inc.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().toLowerCase().endsWith(".deb");
					}
				})));
				Collections.sort(debs, (a, b) -> Long.valueOf(a.lastModified()).compareTo(b.lastModified()));
				for (File f : debs) {
					LOG.info(String.format("Retrying %s", f));
					try {
						addPackage(rep, r, f, true);
					} catch (IOException ioe) {
						LOG.error(String.format("Failed retrying %s", f), ioe);
					}
				}
			}
		}
	}

	protected void deleteRepository(DebianRepository resource) {
		FileUtils.deleteQuietly(new File(getRealmRepositoryHomeDir(), resource.getName()));
		FileUtils.deleteQuietly(new File(getRealmRepositoryIncomingDir(), resource.getName()));
	}

	@Override
	protected Class<DebianRepository> getResourceClass() {
		return DebianRepository.class;
	}

	@Override
	public void rebuildConfiguration(DebianRepository resource) {
		if (!resource.getReleases().isEmpty()) {
			File dir = new File(getRealmRepositoryHomeDir(), resource.getName());
			File confDir = new File(dir, "conf");
			File distFile = new File(confDir, "distributions");
			try {
				PrintWriter pw = new PrintWriter(new FileWriter(distFile), true);
				for (DebianRelease r : resource.getReleases()) {
					r.write(pw, resource);
					pw.println();
				}
				File realmGPGHomeDir = gPGKeyResourceService.getRealmGPGHomeDir();
				/*
				 * TODO need to capture the output. It may contain errors, e.g. about the
				 * undefined targets that we ignore This happens when a repository synched from
				 * a remote contains older packages in a different distribution (e.g. when
				 * nervepoint used to be in 'lucid' foir example)
				 */
				OSCommand.run(dir, "reprepro", "--gnupghome", realmGPGHomeDir.getAbsolutePath(),
						"--ignore=undefinedtarget", "export");

				for (DebianRelease r : resource.getReleases()) {
					if (r.getSignWith() != null) {
						writeGPG(new File(confDir, resource.getName() + "-" + r.getName() + ".gpg.key"),
								realmGPGHomeDir, r.getSignWith());
					}
				}

				if (resource.getSignWith() != null) {
					writeGPG(new File(confDir, resource.getName() + ".gpg.key"), realmGPGHomeDir,
							resource.getSignWith());
				}
			} catch (IOException ioe) {
				throw new IllegalStateException("Failed to rebuild configuration.", ioe);
			}
		}

	}

	protected void writeGPG(File outFile, File realmGPGHomeDir, GPGKeyResource signWith) throws IOException {
		LOG.info(String.format("Writing GPG key %s", signWith));
		OSCommand.runCommand("gpg", "--yes", "--no-tty", "--homedir", realmGPGHomeDir.getAbsolutePath(), "--armor",
				"--output", outFile.getAbsolutePath(), "--export", signWith.getFingerprint());
	}

	@Override
	public void rebuildConfiguration(DebianRelease resource) {
		for (DebianRepository r : resource.getRepositories()) {
			rebuildConfiguration(r);
		}
	}

	@Override
	public void pushToRemote(DebianRepository repo) throws IOException {
		synchronized (pushing) {
			if (pushing.contains(repo))
				throw new IllegalStateException("Already pushing this repository.");
			pushing.add(repo);
		}
		try {
			
			if (repo.getRemoteRepository() == null)
				throw new IllegalArgumentException("Provided repository does not have a remote location.");
			LOG.info(String.format("Resolving remote location %s", repo.getRemoteRepository().getPath().generatePath()));
			AbstractFile remote = virtualFileService.resolveMount(repo.getRemoteRepository()).getDefaultPath();
			if (remote == null)
				throw new FileNotFoundException(
						String.format("Cannot find remote repository %s", repo.getRemoteRepository().getName()));
			LOG.info(String.format("Resolved VFS location as %s", remote.getAbsolutePath()));
			final File realmRepositoryHomeDir = new File(getRealmRepositoryHomeDir(), repo.getName());
			LOG.info(String.format("Resolving local location %s", realmRepositoryHomeDir));
			
			
			NioFileFactory localFileSystem = NioFileFactoryBuilder.create()
					.withHome(realmRepositoryHomeDir)
					.build();	
			AbstractFile src = localFileSystem.getDefaultPath();
			LOG.info(String.format("Resolved local location as %s", src.getAbsolutePath()));
			LOG.info(String.format("Started copying from local path %s to VFS path %s", src.getAbsolutePath(), remote.getAbsolutePath()));
			new Sync(remote, src).deleteRemoved(true).completor((s, d) -> {
				if(d instanceof S3File) {
					try {
						((S3AbstractFileFactory)d.getFileFactory()).makePublic((S3File)d);
					} catch (IOException | PermissionDeniedException e) {
						throw new IllegalStateException(e.getMessage(), e);
					}
				}
			}).sync();
			LOG.info(String.format("Completed copying from %s to %s", src.getAbsolutePath(), remote.getAbsolutePath()));
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			synchronized (pushing) {
				pushing.remove(repo);
				for (Runnable r : onPush)
					r.run();
				onPush.clear();
			}
		}
	}

	public static Iterable<String> runCommandAndIterateOutput(File cwd, String... args) throws IOException {
		final List<String> largs = new ArrayList<String>(Arrays.asList(args));
		ProcessBuilder pb = new ProcessBuilder(largs);
		if (cwd != null) {
			pb.directory(cwd);
		}
		pb.redirectErrorStream(true);
		LOG.info(String.format("Running command: %s (in %s)", largs, cwd));
		final Process p = pb.start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		return new Iterable<String>() {
			Iterator<String> iterator = null;

			@Override
			public Iterator<String> iterator() {
				if (iterator == null) {
					iterator = new Iterator<String>() {
						String next;

						void checkNext() {
							if (next == null) {
								try {
									next = reader.readLine();
									if (next == null) {
										throw new EOFException();
									}
								} catch (EOFException eofe) {
									try {
										int ret = p.waitFor();
										if (ret != 0) {
											throw new IllegalStateException("Command '" + StringUtils.join(largs, " ")
													+ "' returned non-zero status. Returned " + ret + ". ");
										}
									} catch (InterruptedException e) {
										throw new IllegalStateException(e.getMessage(), e);
									}
								} catch (IOException ioe) {
									throw new IllegalStateException("I/O iterator while iterating.");
								}
							}
						}

						@Override
						public boolean hasNext() {
							checkNext();
							return next != null;
						}

						@Override
						public String next() {
							try {
								checkNext();
								return next;
							} finally {
								next = null;
							}
						}
					};
					return iterator;
				} else
					throw new IllegalStateException("Can only create 1 iterator.");
			}
		};
	}

	@Override
	public void addPackage(DebianRepository repo, DebianRelease release, File file, boolean deleteOriginal) throws IOException {

		synchronized (pushing) {
			if (pushing.contains(repo)) {
				LOG.info(
						"Currently pushing this repository, cannot add a package whilst this is happening, so deferring the actual add until this is complete.");
				onPush.add(() -> {
					try {
						addPackage(repo, release, file, deleteOriginal);
					} catch (IOException e) {
						LOG.error("Failed to add package (deferred).", e);
					}
				});
				return;
			} else
				pushing.add(repo);
		}
		try {
			final File realmGPGHomeDir = gPGKeyResourceService.getRealmGPGHomeDir();
			final File realmRepositoryHomeDir = new File(getRealmRepositoryHomeDir(), repo.getName());
			List<String> args = new ArrayList<>(Arrays.asList("reprepro"));
			DebianPriority overridePriority = null;
			if (release.getOverridePriority() != null)
				overridePriority = release.getOverridePriority();
			else if (repo.getOverridePriority() != null)
				overridePriority = repo.getOverridePriority();
			String overrideSection = release.getOverrideSection();
			if (StringUtils.isBlank(overrideSection))
				overrideSection = repo.getOverrideSection();
			if (StringUtils.isNotBlank(overrideSection)) {
				args.add("--section");
				args.add(overrideSection);
			}
			if (overridePriority != null) {
				args.add("--priority");
				args.add(overridePriority.name());
			}
			args.add("--gnupghome");
			args.add(realmGPGHomeDir.getAbsolutePath());
			args.addAll(Arrays.asList("-Vb", ".", "includedeb", release.getName(), file.getAbsolutePath()));
			List<String> output = new ArrayList<>();
			File outputFile = new File(file.getAbsolutePath() + ".errors");
			try {
				for (String s : runCommandAndIterateOutput(realmRepositoryHomeDir, args.toArray(new String[0]))) {
					output.add(s);
					LOG.info("REPREPRO: " + s);
				}
				eventService.publishEvent(new DebianRepositoryChangeEvent(repo, file.getName()));
				if(deleteOriginal)
					file.delete();
				outputFile.delete();
			} catch (IllegalStateException | IOException ioe) {
				try (FileWriter fos = new FileWriter(outputFile)) {
					PrintWriter pw = new PrintWriter(fos);
					pw.println(String.format("Failed to import package %s to repository %s (release %s)",
							file.getName(), repo.getName(), release.getName()));
					pw.println("The output from reprepro was :-");
					pw.println();
					for (String s : output)
						pw.println(s);
					pw.println();
					pw.println("The error was reprepro was :-");
					pw.println();
					if (ioe instanceof IllegalStateException && ioe.getCause() instanceof IOException)
						ioe.getCause().printStackTrace(pw);
					else
						ioe.printStackTrace(pw);
					;
					pw.flush();
				}

				eventService
						.publishEvent(new DebianRepositoryChangeEvent(ioe, repo));
				return;
			}
		} finally {
			pushing.remove(repo);
		}

		if (repo.isUploadToRemoteOnUpdate() && repo.getRemoteRepository() != null) {
			var jid = "publishRepositoryJob-" + repo.getName();
			try {
				schedulerService.cancelTask(jid, true);
			}
			catch(Exception e) {
				LOG.error("Failed to remove existing scheduled publish job.", e);
			}

			// TODO implement runIn
			schedulerService.runNow(new PublishRepositoryJob(repo));
		}
	}

//	protected void checkShared() {
//		Set<Long> thisShared = new HashSet<>();
//		for (DebianRepository r : allObjects()) {
//			if (r.isShared()) {
//				if (!shared.containsKey(r.getId())) {
//					FileContentHandler fch = new FileContentHandler(r.getName(), 0,
//							new File(getRealmRepositoryHomeDir(r.getRealm()), r.getName())) {
//						@Override
//						public boolean getDisableCache() {
//							return false;
//						}
//
//						@Override
//						public String getBasePath() {
//							return r.getName();
//						}
//					};
//					shared.put(r.getId(), fch);
//					LOG.info(String.format("Sharing repository %d (%s)", r.getId(), r.getName()));
//					server.registerHttpHandler(fch);
//				}
//				thisShared.add(r.getId());
//			}
//		}
//		for (Iterator<Map.Entry<Long, FileContentHandler>> it = shared.entrySet().iterator(); it.hasNext();) {
//			Map.Entry<Long, FileContentHandler> en = it.next();
//			if (!thisShared.contains(en.getKey())) {
//				LOG.info(String.format("Unsharing repository %d", en.getKey()));
//				server.unregisterHttpHandler(en.getValue());
//				it.remove();
//			}
//		}
//	}

	@Override
	public void promote(DebianRelease resource, DebianRepository fromRepo,
			DebianRepository toRepo) throws IOException {
		if (!resource.getRepositories().contains(fromRepo) || !resource.getRepositories().contains(toRepo))
			throw new IOException("Relese must contain both repositories.");
		if (fromRepo.equals(toRepo))
			throw new IOException("Source and target repositories are the same.");

		final File realmRepositoryHomeDir = new File(getRealmRepositoryHomeDir(), fromRepo.getName());

		List<Exception> errors = new ArrayList<>();
		for (String s : runCommandAndIterateOutput(realmRepositoryHomeDir, "reprepro", "list", resource.getName())) {
			String[] parts = s.split("\\s+");
			String[] first = parts[0].split("\\|");
			String section = first[1];
			String arch = first[2].split("\\:")[0];
			String packageName = parts[1];
			String version = parts[2];

			File file = new File(realmRepositoryHomeDir,
					"pool" + File.separator + section + File.separator + packageName.substring(0, 1) + File.separator
							+ packageName + File.separator + packageName + "_" + version + "_" + arch + ".deb");

			LOG.info(String.format("Found package %s (%s) in section %s for arch %s", packageName, version, section,
					arch));
			if (!file.exists()) {
				LOG.debug(String.format("File %s does not exist, trying for 'all' package", file));
				file = new File(realmRepositoryHomeDir,
						"pool" + File.separator + section + File.separator + packageName.substring(0, 1) + File.separator
								+ packageName + File.separator + packageName + "_" + version + "_all.deb");
				if (!file.exists()) {
					LOG.warn(String.format("File %s does not exist, skipping", file));
					continue;
				}
			}
			
			LOG.info(String.format("Importing %s into %s", file, toRepo));
			try {
				addPackage(toRepo, resource, file, false);
			} catch (IOException | IllegalStateException ioe) {
				LOG.error(String.format("Failed to import %s into %s", file, toRepo), ioe);
				errors.add(ioe);
			}

		}
		if (errors.size() == 1) {
			Exception e = errors.get(0);
			if (e instanceof IOException)
				throw (IOException) e;
			if (e instanceof IllegalStateException)
				throw (IllegalStateException) e;
			else
				throw new IOException("Failed to promote.", e);
		} else if (!errors.isEmpty()) {
			StringBuilder bui = new StringBuilder(String.format("There were %d errors.\n\n", errors.size()));
			int i = 1;
			for (Exception e : errors) {
				bui.append(i++);
				bui.append(". ");
				bui.append(e.getMessage());
				bui.append("\n");
			}
			throw new IOException(bui.toString());
		}

	}

}
