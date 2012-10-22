package edu.wisc.doit.tcrypt;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Encrypts tokens
 * 
 * @author Eric Dalquist
 */
public interface TokenEncrypter {
    /**
     * Prefix added to all encrypted strings 
     */
    public static final String TOKEN_PREFIX = "ENC(";
    
    /**
     * Suffix added to all encrypted strings 
     */
    public static final String TOKEN_SUFFIX = ")";
    
    String encrypt(String token) throws InvalidCipherTextException;

}