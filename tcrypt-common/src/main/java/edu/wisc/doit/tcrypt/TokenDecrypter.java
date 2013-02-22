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

import java.util.regex.Pattern;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Decrypts tokens
 * 
 * @author Eric Dalquist
 */
public interface TokenDecrypter extends TokenEncrypter {

    public static final Pattern BASE64_PATTERN = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})");
    public static final Pattern TOKEN_PATTERN = Pattern.compile(Pattern.quote(TOKEN_PREFIX) + "(" + BASE64_PATTERN.pattern() + ")" + Pattern.quote(TOKEN_SUFFIX));

    /**
     * Determine if the specified cipher text LOOKS LIKE an encrypted token. No actual verification of the encrypted data is done beyond pattern matching.
     */
    boolean isEncryptedToken(String ciphertext);

    /**
     * Decrypt the specified cipher text. 
     * 
     * @param ciphertext text to decrypt
     * @return The decrypted text
     * @throws InvalidCipherTextException If the cipher is invalid
     */
    String decrypt(String ciphertext) throws InvalidCipherTextException;

}