package edu.wisc.doit.tcrypt.vo;

import java.io.InputStream;
import java.util.Date;

public class ServiceKey {
	
	//variables
	private String name;
	private int keyLength;
	private InputStream publicKey;
	private InputStream privateKey;
	private Date dayCreated;
	
	//Getters/setters
	
	public InputStream getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(InputStream privateKey) {
		this.privateKey = privateKey;
	}
	public InputStream getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(InputStream publicKey) {
		this.publicKey = publicKey;
	}
	public int getKeyLength() {
		return keyLength;
	}
	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDayCreated() {
		return dayCreated;
	}
	public void setDayCreated(Date dayCreated) {
		this.dayCreated = dayCreated;
	}
	
}
