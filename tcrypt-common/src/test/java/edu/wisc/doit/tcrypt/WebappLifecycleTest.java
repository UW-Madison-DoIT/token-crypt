package edu.wisc.doit.tcrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.openssl.PEMWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test that tries to go through each of the lifecycle phases that will be used by the token crypt webapp
 * 
 * @author Eric Dalquist
 */
public class WebappLifecycleTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();    
    
    
    //instance variables correspond to objects that can be configured as Spring singleton beans 
    private final TokenKeyPairGenerator bouncyCastleKeyPairGenerator = new BouncyCastleKeyPairGenerator();
    
    
    /**
     * Generates a key pair for a user request
     */
    @Test
    public void testKeyGeneration() throws Exception {
        //Parameters that will come in to the controller
        final String serviceName = "my-predev.doit.wisc.edu";
        final String remoteUser = "bbadger";
        final Date generationTimestamp = new Date();
        
        //Generate the key pair
        final KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair();
        
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String keyFilePrefix = serviceName + "_" + remoteUser + "_" + simpleDateFormat.format(generationTimestamp) + "-";
        
        //Save the private key to the file system, in the webapp this should get saved to some directory configurable via a properties file
        final File privateKeyFile = folder.newFile(keyFilePrefix + "private.pem");
        final PEMWriter privatePemWriter = new PEMWriter(new FileWriter(privateKeyFile));
        privatePemWriter.writeObject(generateKeyPair.getPrivate());
        privatePemWriter.flush();
        privatePemWriter.close();
        
        //Save the public key to the file system, in the webapp this should get saved to some directory configurable via a properties file
        final File publicKeyFile = folder.newFile(keyFilePrefix + "public.pem");
        final PEMWriter publicPemWriter = new PEMWriter(new FileWriter(publicKeyFile));
        publicPemWriter.writeObject(generateKeyPair.getPublic());
        publicPemWriter.flush();
        publicPemWriter.close();
        
        /*
         * At this point the user's browser should automatically download the private key. Links to download both
         * keys should be present and the user should get a very blunt warning about not losing their private key.
         */
    }
    
    @Test
    public void testTokenEncryption() throws Exception {
        //Parameters that will come in to the controller
        final String serviceName = "my.wisc.edu";
        final String plainText = "CWRnT63u";
        
        /*
         * this step will have happened in the background in the real app. On init, after key pair generation, and periodically
         * the public key directory will be scanned and all public keys loaded and indexed by service name.
         */
        final String keyFileName = "/" + serviceName + "-public.pem";
        
        //Note that BouncyCastleTokenEncrypter instances are thread safe so this creation could be cached in a Map<serviceName, BouncyCastleTokenEncrypter>
        final TokenEncrypter tokenEncrypter = new BouncyCastleTokenEncrypter(new InputStreamReader(this.getClass().getResourceAsStream(keyFileName)));
        
        final String token = tokenEncrypter.encrypt(plainText);
        
        /*
         * At this point the user should be shown the encrypted token
         */
        
    }
}
