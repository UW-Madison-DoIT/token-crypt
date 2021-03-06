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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.bouncycastle.crypto.InvalidCipherTextException;

import edu.wisc.doit.tcrypt.BouncyCastleFileDecrypter;
import edu.wisc.doit.tcrypt.FileDecrypter;
import edu.wisc.doit.tcrypt.FileEncrypter;

/**
 * Decrypts an encrypted tar archive, if neither {@link #setDestDir(File)} or {@link #setDestFile(File)} are
 * specified ${basedir} is used
 * 
 * @author Eric Dalquist
 */
public class DecryptFileTask extends AbstractFileTask {
    private Resource privateKey;
    
    /**
     * @param privateKey The private key used to decrypt
     */
    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @param srcFile The encrypted tar file
     */
    public void setSrcFile(Resource srcFile) {
        this.srcFile = srcFile;
    }

    /**
     * @param destDir Dest directory to decrypt to, either this or destFile must be specified
     */
    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    /**
     * @param destFile Dest file to decrypt to, either this or destDir must be specified
     */
    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public void execute() throws BuildException {
        final FileDecrypter fileDecrypter;
        Reader privateKeyReader = null;
        try {
            privateKeyReader = new InputStreamReader(new BufferedInputStream(this.privateKey.getInputStream()), FileEncrypter.CHARSET);
            fileDecrypter = new BouncyCastleFileDecrypter(privateKeyReader);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create BouncyCastleFileDecrypter for private key: " + this.privateKey, e);
        }
        finally {
            IOUtils.closeQuietly(privateKeyReader);
        }
        
        final File outputFile = getOutputFile();
        
        InputStream encryptedFileInputStream = null;
        OutputStream decryptedFileOutputStream = null;
        try {
            encryptedFileInputStream = new BufferedInputStream(this.srcFile.getInputStream());
            decryptedFileOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            
            this.log("Decrypting '" + this.srcFile + "' to '" + outputFile + "'");
            fileDecrypter.decrypt(encryptedFileInputStream, decryptedFileOutputStream);
        }
        catch (IOException e) {
            throw new BuildException("Failed to decrypt file from '" + this.srcFile + "' to '" + outputFile + "'", e);
        }
        catch (InvalidCipherTextException e) {
            throw new BuildException("Encrypted archive '" + this.srcFile + "' or private key '" + this.privateKey + "' are invalid", e);
        }
        catch (DecoderException e) {
            throw new BuildException("Encrypted archive '" + this.srcFile + "' or private key '" + this.privateKey + "' are invalid", e);
        }
        finally {
            IOUtils.closeQuietly(encryptedFileInputStream);
            IOUtils.closeQuietly(decryptedFileOutputStream);
        }
    }

    @Override
    String getFileName(String srcFileName) {
        return FilenameUtils.getBaseName(srcFileName);
    }
}
