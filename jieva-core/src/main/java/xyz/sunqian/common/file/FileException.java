package xyz.sunqian.common.file;

/**
 * Exception for file.
 *
 * @author fredsuvn
 */
public class FileException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FileException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FileException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FileException(Throwable cause) {
        super(cause);
    }
}
