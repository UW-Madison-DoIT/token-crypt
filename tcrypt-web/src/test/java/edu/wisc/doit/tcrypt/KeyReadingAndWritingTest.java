/**
 * @author Brad Leege <leege@doit.wisc.edu>
 * Created on 2/14/13 at 4:29 PM
 */

package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.dao.impl.KeysKeeper;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
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
				logger.error("Error Deleting tempKeyDirectory {}", e.toString());
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
		KeyPair keyPair = keysKeeper.generateKeyPair();
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
}
