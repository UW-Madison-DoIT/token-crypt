/**
 * Copyright 2012, Board of Regents of the University of
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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

public class ServiceKey
{
	//variables
	private String serviceName;
	private Integer keyLength;
	private String createdByNetId;
	private Date dayCreated;
	private PublicKey publicKey;
	private PrivateKey privateKey;

	/**
	 * Constructor
	 */
	public ServiceKey()
	{
		super();
	}
	public ServiceKey(String serviceName, Integer keyLength, String createdByNetId, Date dayCreated, PublicKey publicKey, PrivateKey privateKey) {
		this.serviceName = serviceName;
		this.keyLength = keyLength;
		this.createdByNetId = createdByNetId;
		this.dayCreated = dayCreated;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	//Getters/setters
	public Integer getKeyLength()
	{
		return keyLength;
	}

	public void setKeyLength(Integer keyLength)
	{
		this.keyLength = keyLength;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public Date getDayCreated()
	{
		return dayCreated;
	}

	public void setDayCreated(Date dayCreated)
	{
		this.dayCreated = dayCreated;
	}

	public String getCreatedByNetId()
	{
		return createdByNetId;
	}

	public void setCreatedByNetId(String createdByNetId)
	{
		this.createdByNetId = createdByNetId;
	}

	public PublicKey getPublicKey()
	{
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey)
	{
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey()
	{
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey)
	{
		this.privateKey = privateKey;
	}
}
