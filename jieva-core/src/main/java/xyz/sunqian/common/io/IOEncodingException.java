package xyz.sunqian.common.io;

/**
 * This exception is used to wrap exceptions thrown by {@link ByteStream.Encoder} or {@link CharStream.Encoder}.
 *
 * @author sunqian
 */
public class IOEncodingException extends IORuntimeException {

    /**
     * Empty constructor.
     */
    public IOEncodingException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public IOEncodingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public IOEncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public IOEncodingException(Throwable cause) {
        super(cause);
    }
}
