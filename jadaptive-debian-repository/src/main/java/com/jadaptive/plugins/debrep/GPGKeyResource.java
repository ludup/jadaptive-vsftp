package com.jadaptive.plugins.debrep;

import java.util.Date;

import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.repository.NamedUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.api.ui.menu.PageMenu;

@ObjectDefinition(resourceKey = GPGKeyResource.RESOURCE_KEY)
@GenerateEventTemplates
@PageMenu(bundle = GPGKeyResource.RESOURCE_KEY, i18n = GPGKeyResource.RESOURCE_KEY + ".names", icon = "fa-archive", parent = ApplicationMenuService.RESOURCE_MENU_UUID)
@TableAction(icon = "fa-download", resourceKey = "downloadKey", target = Target.ROW, url = "/app/api/gpg/download", bundle = GPGKeyResource.RESOURCE_KEY)

public class GPGKeyResource extends NamedUUIDEntity {

	private static final long serialVersionUID = -6809208141909321884L;

	public static final String RESOURCE_KEY = "gpgKey";
	
	@ObjectField(type = FieldType.ENUM)
	private GPGRecordType recordType;

	private GPGValidity validity;
	private int keyLength;
	private GPGKeyAlgo publicKeyAlgo;
	private String fingerprint;

	@ObjectField(type = FieldType.TIMESTAMP)
	private Date creationDate;
	
	@ObjectField(type = FieldType.TIMESTAMP)
	private Date expirationDate;
	
	@ObjectField(type = FieldType.OBJECT_REFERENCE)
	private GPGKeyResource parent;

	@ObjectField(type = FieldType.TEXT)
	private String info;
	
	@ObjectField(type = FieldType.TEXT)
	private String ownerTrust;
	
	@ObjectField(type = FieldType.TEXT)
	private String fullName;
	
	@ObjectField(type = FieldType.TEXT)
	private String comment;
	
	@ObjectField(type = FieldType.TEXT)
	private String email;
	
	@ObjectField(type = FieldType.TEXT)
	private String signatureClass;
	
	@ObjectField(type = FieldType.TEXT)
	private String keyCapabilities;
	
	@ObjectField(type = FieldType.TEXT)
	private String issuerCertificateFingerprint;

	public String getComment() {
		return comment;
	}

	public GPGKeyResource getParent() {
		return parent;
	}

	public void setParent(GPGKeyResource parent) {
		this.parent = parent;
	}

	public void setComment(String comment) {
		this.comment = comment;
		updateName();
	}

	public GPGRecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(GPGRecordType recordType) {
		this.recordType = recordType;
		updateName();
	}

	public GPGValidity getValidity() {
		return validity;
	}

	public void setValidity(GPGValidity validity) {
		this.validity = validity;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public GPGKeyAlgo getPublicKeyAlgo() {
		return publicKeyAlgo;
	}

	public void setPublicKeyAlgo(GPGKeyAlgo publicKeyAlgo) {
		this.publicKeyAlgo = publicKeyAlgo;
	}

	public String getKeyId() {
		return fingerprint == null || fingerprint.length() < 8 ? null : fingerprint.substring(fingerprint.length() - 8);
	}

	public String getLongKeyId() {
		return fingerprint == null || fingerprint.length() < 16 ? getKeyId()
				: fingerprint.substring(fingerprint.length() - 16);
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getOwnerTrust() {
		return ownerTrust;
	}

	public void setOwnerTrust(String ownerTrust) {
		this.ownerTrust = ownerTrust;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
		updateName();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
		updateName();
	}

	public String getSignatureClass() {
		return signatureClass;
	}

	public void setSignatureClass(String signatureClass) {
		this.signatureClass = signatureClass;
	}

	public String getKeyCapabilities() {
		return keyCapabilities;
	}

	public void setKeyCapabilities(String keyCapabilities) {
		this.keyCapabilities = keyCapabilities;
	}

	public String getIssuerCertificateFingerprint() {
		return issuerCertificateFingerprint;
	}

	public void setIssuerCertificateFingerprint(String issuerCertificateFingerprint) {
		this.issuerCertificateFingerprint = issuerCertificateFingerprint;
	}

	public String getUserId() {
		return String.format("%s (%s) <%s>", fullName, comment, email);
	}

	private void updateName() {
		setName(recordType + ":" + getUserId());
	}

	@Override
	public String toString() {
		return "GPGKeyResource [recordType=" + recordType + ", validity=" + validity + ", keyLength=" + keyLength
				+ ", publicKeyAlgo=" + publicKeyAlgo + ", fingerprint=" + fingerprint + ", creationDate=" + creationDate
				+ ", expirationDate=" + expirationDate + ", parent=" + parent + ", info=" + info + ", ownerTrust="
				+ ownerTrust + ", fullName=" + fullName + ", comment=" + comment + ", email=" + email
				+ ", signatureClass=" + signatureClass + ", keyCapabilities=" + keyCapabilities
				+ ", issuerCertificateFingerprint=" + issuerCertificateFingerprint + "]";
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
