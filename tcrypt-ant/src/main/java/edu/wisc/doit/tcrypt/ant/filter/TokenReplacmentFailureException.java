package edu.wisc.doit.tcrypt.ant.filter;

/**
 * Thrown if a token replacement fails
 * 
 * @author Eric Dalquist
 */
public class TokenReplacmentFailureException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TokenReplacmentFailureException() {
        super();
    }

    public TokenReplacmentFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenReplacmentFailureException(String message) {
        super(message);
    }

    public TokenReplacmentFailureException(Throwable cause) {
        super(cause);
    }

}
