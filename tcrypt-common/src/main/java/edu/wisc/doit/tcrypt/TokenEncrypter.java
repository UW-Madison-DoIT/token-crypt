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

import java.nio.charset.Charset;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Encrypts tokens
 * 
 * @author Eric Dalquist
 */
public interface TokenEncrypter {
    static final String ENCODING = "UTF-8";
    static final Charset CHARSET = Charset.forName(ENCODING);

    /**
     * Prefix added to all encrypted strings 
     */
    static final String TOKEN_PREFIX = "ENC(";
    
    /**
     * Suffix added to all encrypted strings 
     */
    static final String TOKEN_SUFFIX = ")";
    
    String encrypt(String token) throws InvalidCipherTextException;

}