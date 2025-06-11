package xyz.sunqian.common.net.data;

/**
 * Exception for codec.
 *
 * @author fredsuvn
 */
public class CodecException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public CodecException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public CodecException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public CodecException(Throwable cause) {
        super(cause);
    }
}
