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
package edu.wisc.doit.tcrypt.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.openssl.PEMWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import edu.wisc.doit.tcrypt.TokenKeyPairGenerator;
import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.vo.ServiceKey;

@Repository("keysKeeper")
public class KeysKeeper implements IKeysKeeper
{
    private static final Pattern KEY_NAME_PATTERN = Pattern.compile("^([^_]+)_([^_]+)_(\\d{14})_(\\d+)_public\\.pem$");
    private static final DateTimeFormatter KEY_CREATED_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    
	protected final Logger logger = LoggerFactory.getLogger(KeysKeeper.class);
	private final Map<String, ServiceKey> keysCache = new ConcurrentHashMap<String, ServiceKey>();
	private final TokenKeyPairGenerator keyPairGenerator;
	private final File directory;
	
	private volatile DateTime lastScan = null;

	/**
	 * Constructor
	 * @param directory Location of keys directory
	 * @param keyPairGenerator KeyPair Generator
	 */
	@Autowired
	public KeysKeeper(@Value("${edu.wisc.doit.tcrypt.path.keydirectory:WEB-INF/keys}") String directoryname, TokenKeyPairGenerator keyPairGenerator)
	{
		this.directory = new File(directoryname);
		if (!this.directory.exists()) {
		    if (!this.directory.mkdirs()) {
		        throw new IllegalArgumentException("Failed to create keys directory: '" + directoryname + "'");
		    }
		}
		if (!this.directory.isDirectory() || !this.directory.canRead() || !this.directory.canWrite()) {
		    throw new IllegalArgumentException("'" + directoryname + "' is not a directory that we have read/write access to");
		}
		
		this.keyPairGenerator = keyPairGenerator;
		logger.info("key directory: {}", directoryname);
		
		//Init keys cache
		this.scanForKeys();
	}
	
	//Make sure this gets called at least once/hour
	@Scheduled(fixedDelay=3600000)
	public void scanForKeys() {
	    //Only re-scan every 1 minute at most
	    final DateTime now = DateTime.now();
	    if (this.lastScan != null && !this.lastScan.plusMinutes(1).isBefore(now)) {
	        return;
	    }
	    this.lastScan = now;
	    
	    forcedScanForKeys();
	}

    private void forcedScanForKeys() {
        try {
            logger.debug("Scanning {} for updates to key files", directory);
            
    	    final File[] keyfiles = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (KEY_NAME_PATTERN.matcher(name).matches()) {
                        return true;
                    }
                    
                    logger.warn("Ignoring {} in {}", name, dir);
                    
                    return false;
                }
            });
    	    
    	    final Set<String> oldKeys = new HashSet<String>(this.keysCache.keySet());
    	    
    	    for (final File keyFile : keyfiles) {
    	        final String keyName = keyFile.getName();
    	        final Matcher keyNameMatcher = KEY_NAME_PATTERN.matcher(keyName);
    	        
    	        if (!keyNameMatcher.matches()) {
    	            throw new IllegalStateException("Some how " + keyFile + " matched the FilenameFilter but does not match the key name pattern");
    	        }
    	        
    	        logger.debug("Found key: {}", keyFile);
    	        
    	        final String serviceName = keyNameMatcher.group(1);
    	        
    	        //Record that we saw the service name and if it wasn't removed it must be a new key
    	        if (!oldKeys.remove(serviceName)) {
        	        final String createdByNetId = keyNameMatcher.group(2);
        	        final String dayCreatedStr = keyNameMatcher.group(3);
        	        final DateTime dayCreated = KEY_CREATED_FORMATTER.parseDateTime(dayCreatedStr);
        	        final int keyLength = Integer.parseInt(keyNameMatcher.group(4));
        	        
        	        final ServiceKey serviceKey = new ServiceKey(serviceName, keyLength, createdByNetId, dayCreated, keyFile);
        	        this.keysCache.put(serviceName, serviceKey);
    	        }
    	    }
    	    
    	    //Remove any keys that have been deleted from the file system
    	    if (!oldKeys.isEmpty()) {
    	        logger.info("Removed old service keys: {}", oldKeys);
    	        this.keysCache.keySet().removeAll(oldKeys);
    	    }
    	    
    	    logger.info("Scanned {}, keysCache contains {} keys", directory, this.keysCache.size());
        }
        catch (Exception e) {
            logger.error("Failed to scan {} for keys", this.directory, e);
        }
    }
	
	@Override
	public Set<String> getListOfServiceNames() {
	    return this.keysCache.keySet();
	}

    @Override
    public KeyPair createServiceKey(String serviceName, int keyLength, String username) throws IOException {
        if (this.keysCache.containsKey(serviceName)) {
            throw new IllegalArgumentException("'" + serviceName + "' service key already exists.");
        }
        logger.debug("Generating {} bit KeyPair for service {} requested by {}", keyLength, serviceName, username);
        
        final KeyPair keyPair = this.keyPairGenerator.generateKeyPair(keyLength);
        
        // Build File Name
        // Pattern: SERVICENAME_NETID_YYYYMMDDHHMMSS_KEYLENGTH_public.pem
        final String fileName = serviceName + "_" + 
                username + "_" + 
                KEY_CREATED_FORMATTER.print(DateTime.now()) + "_" + 
                keyLength + "_public.pem";
        
        final File publicKeyFile = new File(this.directory, fileName);
        if (publicKeyFile.exists()) {
            logger.warn("Key file already exists at {} it will be overwritten.", publicKeyFile);
            publicKeyFile.delete();
        }

        try (final PEMWriter pemWriter = new PEMWriter(new BufferedWriter(new FileWriter(publicKeyFile)))) {
            pemWriter.writeObject(keyPair.getPublic());
        }
        logger.info("Wrote new public key for {} to {}", serviceName, publicKeyFile);
        
        this.forcedScanForKeys();
        
        return keyPair;
    }

    @Override
    public ServiceKey getServiceKey(String serviceName) {
        return this.keysCache.get(serviceName);
    }
}
