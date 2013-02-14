package edu.wisc.doit.tcrypt.vo;

public class EncryptToken {
	
	//variables
	private String serviceKeyName;
	private String unencryptedText;
	private String encryptedText;
	
	//getters/setters
	public String getServiceKeyName() {
		return serviceKeyName;
	}
	public void setServiceKeyName(String serviceKeyName) {
		this.serviceKeyName = serviceKeyName;
	}
	public String getUnencryptedText() {
		return unencryptedText;
	}
	public void setUnencryptedText(String unencryptedText) {
		this.unencryptedText = unencryptedText;
	}
	public String getEncryptedText() {
		return encryptedText;
	}
	public void setEncryptedText(String encryptedText) {
		this.encryptedText = encryptedText;
	}
}
