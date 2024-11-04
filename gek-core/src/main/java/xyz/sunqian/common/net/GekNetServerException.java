package xyz.sunqian.common.net;

import java.net.ServerSocket;

/**
 * Exception for network server.
 *
 * @author fredsuvn
 */
public class GekNetServerException extends RuntimeException {

    private final Object source;

    /**
     * Constructs with server source object and cause.
     *
     * @param source server source object, such as {@link ServerSocket}
     * @param cause  the cause
     */
    public GekNetServerException(Object source, Throwable cause) {
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
}
