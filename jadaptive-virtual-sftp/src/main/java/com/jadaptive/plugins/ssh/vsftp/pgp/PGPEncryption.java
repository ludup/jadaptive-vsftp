package com.jadaptive.plugins.ssh.vsftp.pgp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldOptions;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectExtension;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectViews({ 
	@ObjectViewDefinition(value = PGPEncryption.ENCRYPTION_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -75),
	@ObjectViewDefinition(value = PGPEncryption.KEYS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -70),
	
})
@ObjectExtension(resourceKey = PGPEncryption.RESOURCE_KEY, bundle = VirtualFolder.RESOURCE_KEY, extend = VirtualFolder.RESOURCE_KEY, extendingInterface = PGPEncryptionExtension.class)
@ObjectDefinition(resourceKey = PGPEncryption.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY)
public class PGPEncryption extends AbstractUUIDEntity {

	private static final long serialVersionUID = -4149278816758306048L;

	public static final String RESOURCE_KEY = "pGPEncryption";
	
	public static final String ENCRYPTION_VIEW = "encryptionView";
	public static final String KEYS_VIEW = "keysView";
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false",bundle = VirtualFolder.RESOURCE_KEY, view = ENCRYPTION_VIEW)
	Boolean encrypt;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false",bundle = VirtualFolder.RESOURCE_KEY, view = ENCRYPTION_VIEW)
	Boolean armour;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false",bundle = VirtualFolder.RESOURCE_KEY, view = ENCRYPTION_VIEW)
	Boolean compress;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false",bundle = VirtualFolder.RESOURCE_KEY, view = ENCRYPTION_VIEW)
	Boolean integrityCheck;
	
	@ObjectField(type = FieldType.TEXT_AREA,bundle = VirtualFolder.RESOURCE_KEY, view = KEYS_VIEW)
	String pgpPrivateKey;
	
	@ObjectField(type = FieldType.PASSWORD,  options = FieldOptions.MANUAL_ENCRYPTION, bundle = VirtualFolder.RESOURCE_KEY, view = KEYS_VIEW)
	String pgpPassphrase;
	
	@ObjectField(type = FieldType.TEXT_AREA, bundle = VirtualFolder.RESOURCE_KEY, view = KEYS_VIEW)
	String pgpPublicKey;

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public Boolean getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(Boolean encrypt) {
		this.encrypt = encrypt;
	}

	public Boolean getArmour() {
		return armour;
	}

	public void setArmour(Boolean armour) {
		this.armour = armour;
	}

	public Boolean getCompress() {
		return compress;
	}

	public void setCompress(Boolean compress) {
		this.compress = compress;
	}

	public Boolean getIntegrityCheck() {
		return integrityCheck;
	}

	public void setIntegrityCheck(Boolean integrityCheck) {
		this.integrityCheck = integrityCheck;
	}

	public String getPgpPrivateKey() {
		return pgpPrivateKey;
	}

	public void setPgpPrivateKey(String pgpPrivateKey) {
		this.pgpPrivateKey = pgpPrivateKey;
	}

	public String getPgpPassphrase() {
		return pgpPassphrase;
	}

	public void setPgpPassphrase(String pgpPassphrase) {
		this.pgpPassphrase = pgpPassphrase;
	}

	public String getPgpPublicKey() {
		return pgpPublicKey;
	}

	public void setPgpPublicKey(String pgpPublicKey) {
		this.pgpPublicKey = pgpPublicKey;
	}
}
