package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;

@ObjectDefinition(resourceKey = SshConnectionProperties.RESOURCE_KEY, type = ObjectType.OBJECT)
@ObjectViewDefinition(value = SshConnectionProperties.SSH_CONNECTION_VIEW, weight = 9998)
@ObjectViewDefinition(value = SshConnectionProperties.SSH_AUTHENTICATION_VIEW, weight = 9999)
public class SshConnectionProperties extends AbstractUUIDEntity {

	private static final long serialVersionUID = -1421409177277985276L;
	
	public static final String RESOURCE_KEY = "sshConnectionProperties";

	public static final String SSH_CONNECTION_VIEW = "sshConnectionView";
	public static final String SSH_AUTHENTICATION_VIEW = "sshAuthenticationView";
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(SSH_CONNECTION_VIEW)
	@Validator(type = ValidationType.HOSTNAME)
	@Validator(type = ValidationType.IPV4)
	@Validator(type = ValidationType.IPV6)
	String hostname;
	
	@ObjectField(type = FieldType.INTEGER, defaultValue = "22")
	@ObjectView(SSH_CONNECTION_VIEW)
	@Validator(type = ValidationType.PORT)
	Integer port;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(SSH_CONNECTION_VIEW)
	String username;
	
	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(SSH_AUTHENTICATION_VIEW)
	String password;
	
	@ObjectField(type = FieldType.TEXT_AREA, automaticEncryption = true)
	@ObjectView(SSH_AUTHENTICATION_VIEW)
	String privateKey;
	
	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(SSH_AUTHENTICATION_VIEW)
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
