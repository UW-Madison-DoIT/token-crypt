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
import java.io.OutputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Decrypts a file
 * 
 * @author Eric Dalquist
 */
public interface FileDecrypter {
    
    /**
     * Decrypts a specially formatted TAR file
     * 
     * @param inputStream the TAR file containing the encrypted data
     * @param outputStream the output stream to write the decrypted file to
     */
    void decrypt(TarArchiveInputStream inputStream, OutputStream outputStream) throws InvalidCipherTextException, IOException, DecoderException;

}