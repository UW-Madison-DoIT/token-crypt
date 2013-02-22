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
/**
 * @author Brad Leege <leege@doit.wisc.edu>
 * Created on 2/14/13 at 4:29 PM
 */

package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.dao.impl.KeysKeeper;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Date;
import static org.junit.Assert.*;

public class KeyReadingAndWritingTest
{
	private static final Logger logger = LoggerFactory.getLogger(KeyReadingAndWritingTest.class);
	private IKeysKeeper keysKeeper;
	private final File tempKeyDirctory = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "tempKeys");

	@Before
	public void setup() throws IOException
	{
		logger.info("TempKeyDirectory = {}", tempKeyDirctory.getAbsolutePath());
		keysKeeper = new KeysKeeper(tempKeyDirctory.getAbsolutePath(), new BouncyCastleKeyPairGenerator());
		if (!tempKeyDirctory.exists())
		{
			Boolean result = tempKeyDirctory.mkdir();
			logger.info("tempKeyDirectory creation attempt result: {}", result);
		}
	}

	@After
	public void cleanup()
	{
		if (tempKeyDirctory.exists())
		{
			try
			{
				FileUtils.deleteDirectory(tempKeyDirctory);
			}
			catch (Exception e)
			{
				logger.error("Error Deleting tempKeyDirectory", e);
			}
			logger.info("tempKeyDirectory attempted to be deleted. Still exist? {}", tempKeyDirctory.exists());
		}
	}

	@Test
	public void testCreateWriteAndReadBackKey() throws Exception
	{
		// Create ServiceKey
		ServiceKey original = new ServiceKey();
		original.setCreatedByNetId(System.getProperty("user.name"));
		original.setDayCreated(new Date());
		original.setKeyLength(2048);
		KeyPair keyPair = keysKeeper.generateKeyPair(2048);
		original.setPrivateKey(keyPair.getPrivate());
		original.setPublicKey(keyPair.getPublic());
		original.setServiceName("test.doit.wisc.edu");

		// Write ServiceKey to filesystem
		assertTrue(keysKeeper.writeServiceKeyToFileSystem(original));

		// Step 3: Read ServiceKey from filesystem
		ServiceKey fileKey = keysKeeper.readServiceKeyFromFileSystem(original.getServiceName());
		assertNotNull(fileKey);

		// Compare original ServiceKey content with new ServiceKey read from filesystem
		assertEquals(original.getCreatedByNetId(), fileKey.getCreatedByNetId());
		assertTrue(DateUtils.isSameDay(original.getDayCreated(), fileKey.getDayCreated()));
		assertEquals(original.getKeyLength(), fileKey.getKeyLength());
		assertTrue(Arrays.equals(original.getPublicKey().getEncoded(), fileKey.getPublicKey().getEncoded()));
		assertNull(fileKey.getPrivateKey());
		assertEquals(original.getServiceName(), fileKey.getServiceName());
	}

	@Test
	public void testCreateReadAndWriteKeyInMemory() throws Exception
	{
		// Create ServiceKey
		ServiceKey original = new ServiceKey();
		original.setCreatedByNetId(System.getProperty("user.name"));
		original.setDayCreated(new Date());
		original.setKeyLength(2048);
		KeyPair keyPair = keysKeeper.generateKeyPair(2048);
		original.setPrivateKey(keyPair.getPrivate());
		original.setPublicKey(keyPair.getPublic());
		original.setServiceName("test-memory.doit.wisc.edu");

		assertNotNull(original);

		// Write Service Key to Memory Using InputStream
		InputStream inputStream = keysKeeper.getKeyAsInputStream(original.getPublicKey());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, outputStream);
		logger.debug("Writer toString(): {}", outputStream.toString("UTF-8"));
		assertTrue(Arrays.equals(original.getPublicKey().getEncoded(), outputStream.toByteArray()));
		final String originalString = new String(original.getPublicKey().getEncoded());
		final String outputString = new String(outputStream.toByteArray());
		assertEquals(originalString, outputString);
	}
}
