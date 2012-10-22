package edu.wisc.doit.tcrypt;

import java.io.PrintWriter;
import java.security.KeyPair;

import org.bouncycastle.openssl.PEMWriter;
import org.junit.Test;

public class TokenKeyPairCreationTest {
    @Test
    public void testKeyGeneration() throws Exception {
        final BouncyCastleKeyPairGenerator bouncyCastleKeyPairGenerator = new BouncyCastleKeyPairGenerator();
        final KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair();
        
        final PEMWriter pemWriter = new PEMWriter(new PrintWriter(System.out));
        
        pemWriter.writeObject(generateKeyPair.getPrivate());
        pemWriter.flush();
        pemWriter.writeObject(generateKeyPair.getPublic());
        pemWriter.flush();
        pemWriter.close();
        
    }
}
