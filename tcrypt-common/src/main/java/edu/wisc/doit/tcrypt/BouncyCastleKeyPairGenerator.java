package edu.wisc.doit.tcrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.*;

public class BouncyCastleKeyPairGenerator implements TokenKeyPairGenerator
{
    static {
        //TODO hook to unregister?
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    @Override
    public KeyPair generateKeyPair() {
        final KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA support could not be found. There is a problem with this JVM.", e);
        }
        catch (NoSuchProviderException e) {
            throw new IllegalStateException("BounceCastle support could not be found. There is a problem with this JVM.", e);
        }
        
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }
}

