package xyz.sunqian.common.net;

import java.net.ServerSocket;

/**
 * Exception for network server, subclass of {@link NetException}.
 *
 * @author sunqian
 */
public class NetServerException extends NetException {

    private final Object source;

    /**
     * Constructs with server source object and cause.
     *
     * @param source server source object, such as {@link ServerSocket}
     * @param cause  the cause
     */
    public NetServerException(Object source, Throwable cause) {
        super(cause);
        this.source = source;
    }

    /**
     * Returns server source object, such as {@link ServerSocket}.
     *
     * @return server source object
     */
    public Object getSource() {
        return source;
    }

    // /**
    //  * Empty constructor.
    //  */
    // public NetServerException() {
    //     super();
    // }
    //
    // /**
    //  * Constructs with the message.
    //  *
    //  * @param message the message
    //  */
    // public NetServerException(@Nullable String message) {
    //     super(message);
    // }
    //
    // /**
    //  * Constructs with the message and cause.
    //  *
    //  * @param message the message
    //  * @param cause   the cause
    //  */
    // public NetServerException(@Nullable String message, @Nullable Throwable cause) {
    //     super(message, cause);
    // }
    //
    // /**
    //  * Constructs with the cause.
    //  *
    //  * @param cause the cause
    //  */
    // public NetServerException(@Nullable Throwable cause) {
    //     super(cause);
    // }
    //
    // /**
    //  * Constructs with the {@link IOException}.
    //  *
    //  * @param cause the {@link IOException}
    //  */
    // public NetServerException(@Nonnull IOException cause) {
    //     this(cause.getMessage(), cause.getCause());
    // }
}
