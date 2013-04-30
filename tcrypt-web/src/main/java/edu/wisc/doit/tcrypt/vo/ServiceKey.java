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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.joda.time.DateTime;

import edu.wisc.doit.tcrypt.BouncyCastleFileEncrypter;
import edu.wisc.doit.tcrypt.BouncyCastleTokenEncrypter;
import edu.wisc.doit.tcrypt.FileEncrypter;
import edu.wisc.doit.tcrypt.TokenEncrypter;

public class ServiceKey
{
	private final String serviceName;
	private final int keyLength;
	private final String createdByNetId;
	private final DateTime dayCreated;
	private final File keyFile;
	private volatile TokenEncrypter tokenEncrypter;
	private volatile FileEncrypter fileEncrypter;

	public ServiceKey(String serviceName, int keyLength, String createdByNetId, DateTime dayCreated, File keyFile) {
        this.serviceName = serviceName;
        this.keyLength = keyLength;
        this.createdByNetId = createdByNetId;
        this.dayCreated = dayCreated;
        this.keyFile = keyFile;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public String getCreatedByNetId() {
        return createdByNetId;
    }

    public DateTime getDayCreated() {
        return dayCreated;
    }

    public File getKeyFile() {
        return keyFile;
    }
    
    public TokenEncrypter getTokenEncrypter() throws IOException {
        TokenEncrypter e = tokenEncrypter;
        if (e == null) {
            try (final Reader keyFileReader = new BufferedReader(new FileReader(keyFile))) {
                e = new BouncyCastleTokenEncrypter(keyFileReader);
            }
            tokenEncrypter = e;
        }
        return e;
    }

    public FileEncrypter getFileEncrypter() throws IOException {
        FileEncrypter e = fileEncrypter;
        if (e == null) {
            try (final Reader keyFileReader = new BufferedReader(new FileReader(keyFile))) {
                e = new BouncyCastleFileEncrypter(keyFileReader);
            }
            fileEncrypter = e;
        }
        return e;
    }
}
