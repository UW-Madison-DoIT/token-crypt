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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * Encrypts tokens using a public key
 * 
 * @author Eric Dalquist
 * @version $Revision: 187 $
 */
public class BouncyCastleFileEncrypter extends AbstractPublicKeyEncrypter implements FileEncrypter {
    public static final int FILE_KEY_LENGTH = 128;
    
    public BouncyCastleFileEncrypter(AsymmetricKeyParameter publicKeyParam) {
        super(publicKeyParam);
    }

    public BouncyCastleFileEncrypter(PublicKey publicKey) throws IOException {
        super(publicKey);
    }

    public BouncyCastleFileEncrypter(Reader publicKeyReader) throws IOException {
        super(publicKeyReader);
    }

    public BouncyCastleFileEncrypter(SubjectPublicKeyInfo publicKey) throws IOException {
        super(publicKey);
    }

    @Override
    public void encrypt(String fileName, InputStream inputStream, OutputStream outputStream) throws InvalidCipherTextException {
    }
}
