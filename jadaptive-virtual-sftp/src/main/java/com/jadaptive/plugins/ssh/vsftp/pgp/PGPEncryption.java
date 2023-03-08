package com.jadaptive.plugins.ssh.vsftp.pgp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectExtension;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
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
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
	Boolean encrypt;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
	Boolean armour;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
	Boolean compress;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
	Boolean integrityCheck;
	
	@ObjectField(type = FieldType.TEXT_AREA)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = KEYS_VIEW)
	String privateKey;
	
	@ObjectField(type = FieldType.PASSWORD, manualEncryption = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = KEYS_VIEW)
	String passphrase;
	
	@ObjectField(type = FieldType.TEXT_AREA)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = KEYS_VIEW)
	String publicKey;

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

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}
