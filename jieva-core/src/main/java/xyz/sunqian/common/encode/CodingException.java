package xyz.sunqian.common.encode;

/**
 * Super exception class of {@link EncodingException} and {@link DecodingException}.
 *
 * @author fredsuvn
 */
public class CodingException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public CodingException() {
        super();
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public CodingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public CodingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public CodingException(Throwable cause) {
        super(cause);
    }
}
