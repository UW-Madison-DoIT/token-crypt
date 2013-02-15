package edu.wisc.doit.tcrypt;

import org.junit.Test;
import java.security.KeyPair;
import static org.junit.Assert.assertNotNull;

public class TokenKeyPairCreationTest
{
    @Test
    public void testKeyGeneration() throws Exception
    {
        final BouncyCastleKeyPairGenerator bouncyCastleKeyPairGenerator = new BouncyCastleKeyPairGenerator();
        final KeyPair generateKeyPair = bouncyCastleKeyPairGenerator.generateKeyPair(2048);
        assertNotNull(generateKeyPair);
        assertNotNull(generateKeyPair.getPrivate());
        assertNotNull(generateKeyPair.getPublic());
/*
        final PEMWriter pemWriter = new PEMWriter(new PrintWriter(System.out));
        pemWriter.writeObject(generateKeyPair.getPrivate());
        pemWriter.flush();
        pemWriter.writeObject(generateKeyPair.getPublic());
        pemWriter.flush();
        pemWriter.close();
*/
    }
}
