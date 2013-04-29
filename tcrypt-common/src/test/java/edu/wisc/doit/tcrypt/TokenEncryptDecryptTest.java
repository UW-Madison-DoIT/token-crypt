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
        final String token = "";
        testTokenRoundTrip(token);
        testDecrypt(token, "ENC(IWqGtDnoXBr7Kk2X/fXjWCAjfJYgisO0F5+tlEgF99ypVdzrg7Um1e0ROolgjz54aubUF0+0lD1FYEYA32pW+lph+8SZNdZn74pjbJ5HB6aQ1tMIDy96zRU7uPmzDFjQj9Oa5XAcyn8bTyHO/r4VXgBj7yleNL8mQ07vM4lmCIp50Ey1xZbNhg88d3RaX6LEuxqzhG0PZPgFLr8E+5k0bJ8NsUHLZBm429++xjyOwxlF6XYW5qfex0ZOoHQt3cw/+JC7bphxe7gnmShf4jpGPfyVaATKyQZe33tKPtw0sTeeplZbUVsYw8wQrDChzkZclaXKGKq6RKgyWdLsFooEaw==)");
    }
    
    @Test
    public void testEncryptDecryptSeparatorOnly() throws InvalidCipherTextException {
        final String token = Character.toString(BouncyCastleTokenEncrypter.SEPARATOR);
        testTokenRoundTrip(token);
        testDecrypt(token, "ENC(HlHzqaedTV+2UzqJRvFayoKrGgvSTlf4k3h4qp+KLtNaWv/PQPeTlCetFqLTWOmTCWNPMwRl7J+awdsID/s0lfUb1n296tOvpwxCIYDE1c/hchCt1fghzubM4kk1Fg0LA1S2WU1MZxJ5fimhLdL24S+MZbKk+HI1Vk8sAg8VeGN7dQo2UdhhaXdu3H/fsrGMKT3+Noe/rhGo4xoinjjdB8y03XB1wlDJX3uT6Tm1UYSAKZhfQnf5A0qONoep2Q3dHarskF7RpDmMlXUIBiLpX10FzHJddHsp4GKQ1b2rgMmJnZgqG8r1iHJ2hG5vmWL9cGwlqMyJc7pWtXDawXOC8w==)");
    }

    @Test
    public void testEncryptDecryptSeparatorIn() throws InvalidCipherTextException {
        final String token = "foo" + Character.toString(BouncyCastleTokenEncrypter.SEPARATOR) + "bar";
        testTokenRoundTrip(token);
        testDecrypt(token, "ENC(Xdr2DD33fqHxoWrdfdw54X1bohzrZbaGkV3o0WDyCpr4hefz3QWH0xCxz76v18dlJGbdDSuJ9BbUB5ZFC0JYFvWgEthMkA60OnSxGEFFEooWae0vjkTIbPfJpA1ciwdEO4yNsGRSWeiSnaaJt+NR7gX7aXmDm5RvuJpnVU20/2Rsd10UBV61iMLpVX01owpZPsec7jX1LlL42DztHloFE0hKgvIOPRf80yGL+k4jQo+Kij6JBi7IWAkr+9cxllCi8itJiBw4Tb91AmDl5lVOMAFJBl9qQGL5mOreYzs7iGXp66LdJqv97AwIegNnUGIxheRlkhwprMVCCQ+00U7Bkw==)");
    }
    
    @Test
    public void testEncryptDecrypt() throws InvalidCipherTextException {
        final String token = "foobar";
        testTokenRoundTrip(token);
        testDecrypt(token, "ENC(D/2SWYY5WcCOSIxLdBdpn+icciyxkuJ+grpjuOwT0+JszeaAmO+sQxm7JLTj39f5VEOIXyyOnf26YHYesVygrltJrOSJnlaWgo0/V4xVOrWLklvDJa9XrbY30XfutxFnxrYShQK9pSbyjyH3T6KJ1/vEa7viBJ7mh+Qca9lO72hT2gmYrL+hClgGBqa0eYMlG8lQ+FldfnQocs/guBAT2e5l5XKZXtR7LRlHlpXolMdAoLEqO/Gtkw2l59Y0n1ZfzBhWj36iCZQiONI63aQKG93JeWsIlUqtCdlKq6hrn8y0oP0vmmWQMAEfbJNvZ33O7GDckcXt6Gj5lqAgZ+I5mg==)");
    }
    
    @Test
    public void testEncryptDecryptNewline() throws InvalidCipherTextException {
        final String token = "foo\nbar";
        testTokenRoundTrip(token);
        testDecrypt(token, "ENC(Z0iok4XMH2CtJYOPSnG8ERa2XNTRwCEgv0irsdlp9Lnu8YaY0HlyWu6wQRXQ0E+j2he78KaaFDLY5RWeIxwWv38Tj5Lu1CBPjAQcBEkRYvV194cPxbaQlf4JK37zITU0CfCMHjpRNxTYr91igCREAeEjiBg5hrxfL/K0jkepcHTI95uCJeltYe2C8iy+Cj6NC9atrtW2EhztdE+q+9PmjoSedOFOksj/eo6n64/8vf9EROXgkun4OjsbgnIc5fGugVy45s72QTWWbv6eRZ4tsWud+P776pRSWgW2omUZdlNPK7PIwDWIVZ4kr1sRMGKPzoq7r3u6dgYZO2kFf3wMOw==)");
    }
    
    @Test
    public void testEncryptDecryptUTF() throws InvalidCipherTextException {
        final String token = "Ä ä Ü ü ß - Я Б Г Д Ж Й -  Ł Ą Ż Ę Ć Ń Ś Ź - てすと - ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ";
        testTokenRoundTrip(token);
        testDecrypt(token, "ENC(YlXvNVY8FaQl+83DPyLp0CpMpabjBueWsWFCekJQGmNgFMK/faQGUmm48+nGAAHsSFQF+99g5TQePCnubhm4LBM0Da7Nim3WGDze/3Vl3/r/d68gCGzv81Fb+Sd2g2+e6HkzohXU/Ke1d8tfNZIlEhRWrQSN/i+uUaHGKbi6Rb1+/giKTAUAz1RUbad3H4XoMqhyGzKJSEi24k+3gQa7tzIW6weqmlEy/lUNPC2z8UIFeJ6uP+d1SyF4Cdc2cR2gv2Pb28CQLMAOqz9vysdHko/gm+omJS27W9hhkFJDGbwPRAfLHJgU6srCSG8mh6prVDVDSyPaQ2wBPH4U0i1FtA==)");
    }

    protected void testTokenRoundTrip(String token) throws InvalidCipherTextException {
        String encryptedToken = tokenEncrypter.encrypt(token);
        testDecrypt(token, encryptedToken);

        encryptedToken = tokenEncrypter.encrypt(token);
        testDecrypt(token, encryptedToken);
    }

    protected void testDecrypt(String token, String encryptedToken) throws InvalidCipherTextException {
        assertTrue(tokenDecrypter.isEncryptedToken(encryptedToken));
        final String decrypted1 = tokenDecrypter.decrypt(encryptedToken);
        assertEquals(token, decrypted1);
    }
}
