package xyz.sunqian.common.encode;

/**
 * Exception for decoding.
 *
 * @author fredsuvn
 */
public class DecodingException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public DecodingException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public DecodingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public DecodingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public DecodingException(Throwable cause) {
        super(cause);
    }
}
