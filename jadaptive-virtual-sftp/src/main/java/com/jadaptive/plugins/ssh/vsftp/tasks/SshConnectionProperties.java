package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;

@ObjectDefinition(resourceKey = SshConnectionProperties.RESOURCE_KEY, type = ObjectType.OBJECT)
public class SshConnectionProperties extends AbstractUUIDEntity {

	private static final long serialVersionUID = -1421409177277985276L;
	
	public static final String RESOURCE_KEY = "sshConnectionProperties";

	@ObjectField(type = FieldType.TEXT)
	@Validator(type = ValidationType.HOSTNAME)
	@Validator(type = ValidationType.IPV4)
	@Validator(type = ValidationType.IPV6)
	String hostname;
	
	@ObjectField(type = FieldType.INTEGER, defaultValue = "22")
	@Validator(type = ValidationType.PORT)
	Integer port;
	
	@ObjectField(type = FieldType.TEXT)
	String username;
	
	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	String password;
	
	@ObjectField(type = FieldType.TEXT_AREA, automaticEncryption = true)
	String privateKey;
	
	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	String passphrase;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
}
