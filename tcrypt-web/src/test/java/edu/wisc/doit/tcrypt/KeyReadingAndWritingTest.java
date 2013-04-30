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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.PublicKey;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import edu.wisc.doit.tcrypt.dao.impl.KeysKeeper;
import edu.wisc.doit.tcrypt.vo.ServiceKey;

public class KeyReadingAndWritingTest
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
	private IKeysKeeper keysKeeper;
	
	@Before
	public void setup() throws Exception {
	    final BouncyCastleKeyPairGenerator bouncyCastleKeyPairGenerator = new BouncyCastleKeyPairGenerator();
	    this.keysKeeper = new KeysKeeper(testFolder.getRoot().getCanonicalPath(), bouncyCastleKeyPairGenerator);
	}

	@Test
	public void testCreateWriteAndReadBackKey() throws Exception
	{
	    // Create ServiceKey
	    final KeyPair kp = this.keysKeeper.createServiceKey("example.com", 2048, "username");
	    assertNotNull(kp);

		// Step 3: Read ServiceKey from filesystem
		ServiceKey foundKey = keysKeeper.getServiceKey("example.com");
		assertNotNull(foundKey);

		// Compare original ServiceKey content with new ServiceKey read from filesystem
		assertEquals("example.com", foundKey.getServiceName());
		assertEquals("username", foundKey.getCreatedByNetId());
		assertEquals(2048, foundKey.getKeyLength());
		//Verify created in same minute
        assertEquals(DateTime.now().minuteOfHour().roundFloorCopy(), foundKey.getDayCreated().minuteOfHour().roundFloorCopy());
		assertNotNull(foundKey.getFileEncrypter());
		assertNotNull(foundKey.getTokenEncrypter());
		
		final File keyFile = foundKey.getKeyFile();
        assertNotNull(keyFile);
        
        @SuppressWarnings("resource")
        PEMParser pemParser = new PEMParser(new FileReader(keyFile));
        Object object = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        final PublicKey actualPublicKey = converter.getPublicKey((SubjectPublicKeyInfo) object);
        
        assertArrayEquals(kp.getPublic().getEncoded(), actualPublicKey.getEncoded());
	}
}
