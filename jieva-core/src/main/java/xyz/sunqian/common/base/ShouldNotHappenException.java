package xyz.sunqian.common.base;

/**
 * An exception indicates that <b>should not happen</b>.
 *
 * @author sunqian
 */
public class ShouldNotHappenException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public ShouldNotHappenException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public ShouldNotHappenException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ShouldNotHappenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public ShouldNotHappenException(Throwable cause) {
        super(cause);
    }
}
