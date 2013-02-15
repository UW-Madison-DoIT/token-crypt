package edu.wisc.doit.tcrypt;

import java.security.KeyPair;

/**
 * Generates key pairs used for token encryption and decryption
 * 
 * @author Eric Dalquist
 */
public interface TokenKeyPairGenerator {
    /**
     * @return A newly generated key pair
     */
    KeyPair generateKeyPair(Integer keyLength);
}
