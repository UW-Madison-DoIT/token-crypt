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
package edu.wisc.doit.tcrypt.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.bouncycastle.crypto.InvalidCipherTextException;

import edu.wisc.doit.tcrypt.BouncyCastleFileEncrypter;
import edu.wisc.doit.tcrypt.FileEncrypter;

/**
 * Encrypts a file using the public key, if neither {@link #setDestDir(File)} or {@link #setDestFile(File)} are
 * specified ${basedir} is used
 * 
 * @author Eric Dalquist
 */
public class EncryptFileTask extends AbstractFileTask {
    private Resource publicKey;
    
    /**
     * @param publicKey The public key used to encrypt
     */
    public void setPublicKey(Resource privateKey) {
        this.publicKey = privateKey;
    }

    /**
     * @param srcFile The file to encrypt
     */
    public void setSrcFile(Resource srcFile) {
        this.srcFile = srcFile;
    }

    /**
     * @param destDir Dest directory to encrypt to
     */
    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    /**
     * @param destFile Dest file to encrypt to
     */
    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public void execute() throws BuildException {
        final FileEncrypter fileEncrypter;
        Reader publicKeyReader = null;
        try {
            publicKeyReader = new InputStreamReader(new BufferedInputStream(this.publicKey.getInputStream()), FileEncrypter.CHARSET);
            fileEncrypter = new BouncyCastleFileEncrypter(publicKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create BouncyCastleFileEncrypter for public key: " + this.publicKey, e);
        }
        finally {
            IOUtils.closeQuietly(publicKeyReader);
        }
        
        final File outputFile = getOutputFile();
        
        InputStream srcFileInputStream = null;
        OutputStream encryptedFileOutputStream = null;
        try {
            srcFileInputStream = new BufferedInputStream(this.srcFile.getInputStream());
            encryptedFileOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            
            this.log("Encrypting '" + this.srcFile + "' to '" + outputFile + "'");
            
            final String fileName = FilenameUtils.getName(this.srcFile.getName());
            final long size = this.srcFile.getSize();
            fileEncrypter.encrypt(fileName, (int)size, srcFileInputStream, encryptedFileOutputStream);
        }
        catch (IOException e) {
            throw new BuildException("Failed to encrypt file from '" + this.srcFile + "' to '" + outputFile + "'", e);
        }
        catch (InvalidCipherTextException e) {
            throw new BuildException("Public key '" + this.publicKey + "' is invalid", e);
        }
        finally {
            IOUtils.closeQuietly(srcFileInputStream);
            IOUtils.closeQuietly(encryptedFileOutputStream);
        }
    }

    @Override
    String getFileName(String srcFileName) {
        return srcFileName + ".tar";
    }
    
    
}
