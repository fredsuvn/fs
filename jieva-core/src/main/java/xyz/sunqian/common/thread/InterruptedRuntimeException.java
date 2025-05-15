package xyz.sunqian.common.thread;

/**
 * Runtime exception for {@link InterruptedException}.
 *
 * @author sunqian
 */
public class InterruptedRuntimeException extends RuntimeException {

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public InterruptedRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs with the {@link InterruptedException}.
     *
     * @param cause the {@link InterruptedException}
     */
    public InterruptedRuntimeException(InterruptedException cause) {
        super(cause);
    }
}
