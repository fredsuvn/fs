package xyz.sunqian.common.crypto;

/**
 * Exception for crypto.
 *
 * @author fredsuvn
 */
public class CryptoException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public CryptoException() {
        super();
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public CryptoException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public CryptoException(Throwable cause) {
        super(cause);
    }
}
