package com.jadaptive.plugins.ssh.vsftp.behaviours;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderBehaviour;

@ObjectDefinition(resourceKey = PGPBehaviour.RESOURCE_KEY, bundle = VirtualFolder.RESOURCE_KEY, type = ObjectType.OBJECT, scope = ObjectScope.GLOBAL, defaultColumn = "name")
@ObjectViews({ 
	@ObjectViewDefinition(value = PGPBehaviour.ENCRYPTION_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -75),
	@ObjectViewDefinition(value = PGPBehaviour.KEYS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -70)})
public class PGPBehaviour extends VirtualFolderBehaviour {

	private static final long serialVersionUID = 3900310246508116815L;

	public static final String ENCRYPTION_VIEW = "encryptionView";
	public static final String KEYS_VIEW = "keysView";
	
	public static final String RESOURCE_KEY = "pgpBehaviour";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	@Override
	public boolean supportsMultipleInstances() {
		return false;
	}
	
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
	
	public Boolean getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(Boolean encrypt) {
		this.encrypt = encrypt;
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

}
