/**
 * Copyright 2013, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
