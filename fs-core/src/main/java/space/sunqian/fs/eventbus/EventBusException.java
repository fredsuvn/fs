package space.sunqian.fs.eventbus;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Exception for event bus.
 *
 * @author sunqian
 */
public class EventBusException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public EventBusException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public EventBusException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public EventBusException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public EventBusException(@Nullable Throwable cause) {
        super(cause);
    }
}