package xyz.sunqian.common.base.exception;

/**
 * This exception is used to wrap the original exception during the processing, the original exception can be obtained
 * from {@link #getCause()}.
 *
 * @author sunqian
 */
public class ProcessingException extends RuntimeException {

    /**
     * Constructs with the original cause.
     *
     * @param cause the original cause
     */
    public ProcessingException(Throwable cause) {
        super(cause);
    }
}
