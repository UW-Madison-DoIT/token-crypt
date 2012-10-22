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
package edu.wisc.doit.tcrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class TokenEncryptDecryptTest {
    private TokenEncrypter tokenEncrypter;
    private BouncyCastleTokenDecrypter tokenDecrypter;
    
    @Before
    public void setup() throws IOException {
        tokenEncrypter = new BouncyCastleTokenEncrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-public.pem")));
        tokenDecrypter = new BouncyCastleTokenDecrypter(new InputStreamReader(this.getClass().getResourceAsStream("/my.wisc.edu-private.pem")));
    }

    @Test
    public void testEncryptDecryptEmpty() throws InvalidCipherTextException {
        testTokenRoundTrip("");
    }
    
    @Test
    public void testEncryptDecryptSeparatorOnly() throws InvalidCipherTextException {
        testTokenRoundTrip(Character.toString(BouncyCastleTokenEncrypter.SEPARATOR));
    }

    @Test
    public void testEncryptDecryptSeparatorIn() throws InvalidCipherTextException {
        testTokenRoundTrip("foo" + Character.toString(BouncyCastleTokenEncrypter.SEPARATOR) + "bar");
    }
    
    @Test
    public void testEncryptDecrypt() throws InvalidCipherTextException {
        testTokenRoundTrip("foobar");
    }
    
    @Test
    public void testEncryptDecryptNewline() throws InvalidCipherTextException {
        testTokenRoundTrip("foo\nbar");
    }
    
    @Test
    public void testEncryptDecryptUTF() throws InvalidCipherTextException {
        testTokenRoundTrip("Ä ä Ü ü ß - Я Б Г Д Ж Й -  Ł Ą Ż Ę Ć Ń Ś Ź - てすと - ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ");
    }

    protected void testTokenRoundTrip(final String token) throws InvalidCipherTextException {
        String encryptedToken = tokenEncrypter.encrypt(token);
        testDecrypt(token, encryptedToken);

        encryptedToken = tokenDecrypter.encrypt(token);
        testDecrypt(token, encryptedToken);
    }

    protected void testDecrypt(final String token, final String encryptedToken) throws InvalidCipherTextException {
        assertTrue(tokenDecrypter.isEncryptedToken(encryptedToken));
        final String decrypted1 = tokenDecrypter.decrypt(encryptedToken);
        assertEquals(token, decrypted1);
    }
}
