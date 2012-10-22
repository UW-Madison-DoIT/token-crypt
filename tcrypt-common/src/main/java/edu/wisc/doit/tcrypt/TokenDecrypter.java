package edu.wisc.doit.tcrypt;

import java.util.regex.Pattern;

import org.bouncycastle.crypto.InvalidCipherTextException;

/**
 * Decrypts tokens
 * 
 * @author Eric Dalquist
 */
public interface TokenDecrypter extends TokenEncrypter {

    public static final Pattern BASE64_PATTERN = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})");
    public static final Pattern TOKEN_PATTERN = Pattern.compile(Pattern.quote(TOKEN_PREFIX) + "(" + BASE64_PATTERN.pattern() + ")" + Pattern.quote(TOKEN_SUFFIX));

    /**
     * Determine if the specified cipher text LOOKS LIKE an encrypted token. No actual verification of the encrypted data is done beyond pattern matching.
     */
    boolean isEncryptedToken(String ciphertext);

    /**
     * Decrypt the specified cipher text. 
     * 
     * @param ciphertext text to decrypt
     * @return The decrypted text
     * @throws InvalidCipherTextException If the cipher is invalid
     */
    String decrypt(String ciphertext) throws InvalidCipherTextException;

}