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

import edu.wisc.doit.tcrypt.TokenKeyPairGenerator;
import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository("keysKeeper")
public class KeysKeeper implements IKeysKeeper
{
	protected final Logger logger = LoggerFactory.getLogger(KeysKeeper.class);
	private String directoryname;
	private TokenKeyPairGenerator keyPairGenerator;
	private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * Constructor
	 * @param directoryname Location of keys directory
	 * @param keyPairGenerator KeyPair Generator
	 */
	@Autowired
	public KeysKeeper(@Value("${edu.wisc.doit.tcrypt.path.keydirectory:WEB-INF/keys}") String directoryname, TokenKeyPairGenerator keyPairGenerator)
	{
		super();
		this.directoryname = directoryname;
		logger.info("DirectoryName = {}", directoryname);
		this.keyPairGenerator = keyPairGenerator;
	}

	@Override
	public List<String> getListOfServiceNames()
	{
		File dir = new File(directoryname);
		String[] fileNames = dir.list();
		List<String> results = new ArrayList<String>();

		if (fileNames != null)
		{
			for (String string : fileNames)
			{
				results.add(string.substring(0,string.indexOf("_")));
			}
		}
		return results;
	}

	@Override
	public synchronized ServiceKey readServiceKeyFromFileSystem(final String serviceName)
	{
		logger.info("ServiceName to look for '{}'", serviceName);
		// Look For File with Service Name in Directory
		File dir = new File(directoryname);
		String[] fileNames = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s)
			{
				return s.toUpperCase().contains(serviceName.toUpperCase());
			}
		});

		// If File Found Read It Into ServiceKey object
		if (fileNames.length > 0)
		{
			String file = fileNames[0];
			logger.info("Found file = {}", file);

			ServiceKey serviceKey = null;
			try
			{
				serviceKey = new ServiceKey();
				Integer lastIndex = file.indexOf("_");
				serviceKey.setServiceName(file.substring(0, lastIndex));
				Integer newLastIndex = file.indexOf("_", lastIndex + 1);
				serviceKey.setCreatedByNetId(file.substring(lastIndex + 1, newLastIndex));
				lastIndex = newLastIndex;
				newLastIndex = file.indexOf("_", lastIndex + 1);
				serviceKey.setDayCreated(fileDateFormat.parse(file.substring(lastIndex + 1, newLastIndex)));
				lastIndex = newLastIndex;
				newLastIndex = file.indexOf("_", lastIndex + 1);
				serviceKey.setKeyLength(Integer.parseInt(file.substring(lastIndex + 1, newLastIndex)));

				final PEMReader pemReader = new PEMReader(new FileReader(new File(directoryname + System.getProperty("file.separator") + file)));
				serviceKey.setPublicKey((PublicKey)pemReader.readObject());
				pemReader.close();
			}
			catch (Exception e)
			{
				logger.error("Error reading ServiceKey: ", e);
			}
			return serviceKey;
		}

		logger.warn("Didn't find a key on the file system for this service.");
		return null;
	}

	@Override
	public InputStream getKeyAsInputStream(Key key)
	{
		return new ByteArrayInputStream(key.getEncoded());
	}

	@Override
	public KeyPair generateKeyPair(Integer keyLength)
	{
		if (keyLength == null || keyLength == 0)
		{
			keyLength = 2048;
		}
		return keyPairGenerator.generateKeyPair(keyLength);
	}

	@Override
	public synchronized Boolean writeServiceKeyToFileSystem(ServiceKey serviceKey)
	{
		// Build File Name
		// Pattern: SERVICENAME_NETID_YYYYMMDDHHMMSS_KEYLENGTH_public.pem
		StringBuffer fileName = new StringBuffer();
		fileName.append(serviceKey.getServiceName());
		fileName.append("_").append(serviceKey.getCreatedByNetId()).append("_");
		fileName.append(fileDateFormat.format(serviceKey.getDayCreated()));
		fileName.append("_").append(serviceKey.getKeyLength());
		fileName.append("_public.pem");
		logger.info("ServiceKey file name = {}", fileName.toString());
		String path = directoryname + System.getProperty("file.separator") + fileName;
		logger.info("Path = {}", path);

		// Write To FileSystem
		Boolean result = Boolean.TRUE;
		try
		{
			File file = new File(path);
			if (file.exists())
			{
				file.delete();
			}
			file.createNewFile();
			final PEMWriter pemWriter = new PEMWriter(new FileWriter(file));
			pemWriter.writeObject(serviceKey.getPublicKey());
			pemWriter.close();
		}
		catch (IOException e)
		{
			logger.error(e.toString());
			result = Boolean.FALSE;
		}
		logger.info("Result of writing to file: {}", result);

		// Return Results
		return result;
	}

	@Override
	public Boolean writeKeyToOutputStream(Key key, OutputStream outputStream)
	{
		try
		{
			final PEMWriter pemWriter = new PEMWriter(new PrintWriter(outputStream));
			pemWriter.writeObject(key);
			pemWriter.close();
		}
		catch (IOException e)
		{
			logger.error("Error writing Key to OutputStream: ", e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
