/**
 * @author Brad Leege <leege@doit.wisc.edu>
 * Created on 2/14/13 at 4:29 PM
 */

package edu.wisc.doit.tcrypt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class KeyReadingAndWritingTest
{
	private static final Logger logger = LoggerFactory.getLogger(KeyReadingAndWritingTest.class);
	private IKeysKeeper keysKeeper;
	private final File tempKeyDirctory = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "tempKeys");

	@Before
	public void setup() throws IOException
	{
		logger.info("TempKeyDirectory = {}", tempKeyDirctory.getAbsolutePath());
		keysKeeper = new KeysKeeper(tempKeyDirctory.getAbsolutePath());
		if (!tempKeyDirctory.exists())
		{
			Boolean result = tempKeyDirctory.createNewFile();
			logger.info("tempKeyDirectory creation attempt result: {}", result);
		}
	}

	@After
	public void cleanup()
	{
		if (tempKeyDirctory.exists())
		{
			Boolean result = tempKeyDirctory.delete();
			logger.info("tempKeyDirectory deletion attempt result: {}", result);
		}
	}

	@Test
	public void testCreateWriteAndReadBackKey() throws Exception
	{
		// TODO Step 1: Create ServiceKey

		// TODO Step 2: Write ServiceKey to filesystem

		// TODO Step 3: Read ServiceKey from filesystem

		// TODO Step 4: Compare original ServiceKey content with new ServiceKey read from filesystem
	}
}
