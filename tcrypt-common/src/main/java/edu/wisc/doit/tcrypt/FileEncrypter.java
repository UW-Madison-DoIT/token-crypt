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
import java.nio.charset.Charset;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Encrypts a file. The result is a TAR file with two entries that are both encrypted.
 * 
 * @author Eric Dalquist
 */
public interface FileEncrypter {
    static final String ENCODING = "UTF-8";
    static final Charset CHARSET = Charset.forName(ENCODING);
    
    
    static final String ENC_SUFFIX = ".enc";
    static final String KEYFILE_ENC_NAME = "keyfile" + ENC_SUFFIX;
    static final String HASHFILE_ENC_NAME = "hashfile" + ENC_SUFFIX;
    
    static final char KEYFILE_LINE_SEPERATOR = '\n';

    static final int KEY_LENGTH = 256;
    static final int IV_LENGTH = 128;

    /**
     * @param fileName Name of the file being encrypted
     * @param size Size of the file being encrypted, this MUST be accurate
     * @param inputStream Input stream to read the file from
     * @param outputStream Output stream to write the TAR file to
     */
    void encrypt(String fileName, int size, InputStream inputStream, OutputStream outputStream) throws InvalidCipherTextException, IOException;
    
    /**
     * Adds a wrapper to the specified OutputStream to encrypt it on the fly
     * 
     * @param outputStream the stream the data to encrypt is written to
     * @return An OutputStream that the encrypted tar file is written to
     */
    OutputStream encrypt(String fileName, int size, OutputStream outputStream) throws InvalidCipherTextException, IOException, DecoderException;

}