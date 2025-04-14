package xyz.sunqian.common.base.exception;

/**
 * This exception is used to wrap the original exception during a processing.
 *
 * @author sunqian
 */
public class ProcessingException extends RuntimeException {

    /**
     * Constructs with the specified cause.
     *
     * @param cause the specified cause
     */
    public ProcessingException(Throwable cause) {
        super(cause);
    }
}
