package xyz.sunqian.common.encode;

/**
 * Exception for encoding.
 *
 * @author fredsuvn
 */
public class EncodingException extends CodingException {

    /**
     * Empty constructor.
     */
    public EncodingException() {
        super();
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public EncodingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public EncodingException(Throwable cause) {
        super(cause);
    }
}
