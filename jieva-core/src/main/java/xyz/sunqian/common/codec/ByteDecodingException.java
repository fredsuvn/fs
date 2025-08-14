package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * The runtime exception for byte decoding.
 *
 * @author sunqian
 */
public class ByteDecodingException extends JieRuntimeException {

    private final long position;

    /**
     * Empty constructor.
     */
    public ByteDecodingException() {
        super();
        position = -1;
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ByteDecodingException(@Nullable String message) {
        super(message);
        position = -1;
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ByteDecodingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        position = -1;
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ByteDecodingException(@Nullable Throwable cause) {
        super(cause);
        position = -1;
    }

    /**
     * Constructs with the position.
     *
     * @param position the position
     */
    public ByteDecodingException(long position) {
        super();
        this.position = position;
    }

    /**
     * Constructs with the position and message.
     *
     * @param position the position
     * @param message  the message
     */
    public ByteDecodingException(long position, @Nullable String message) {
        super(message);
        this.position = position;
    }

    /**
     * Constructs with the position, message and cause.
     *
     * @param position the position
     * @param message  the message
     * @param cause    the cause
     */
    public ByteDecodingException(long position, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.position = position;
    }

    /**
     * Constructs with the position and cause.
     *
     * @param position the position
     * @param cause    the cause
     */
    public ByteDecodingException(long position, @Nullable Throwable cause) {
        super(cause);
        this.position = position;
    }

    /**
     * Returns the position of the decoding error starting from {@code 0}, may be {@code -1} if the error is not related
     * to a specific position.
     *
     * @return the position of the decoding error, may be {@code -1} if the error is not related
     */
    public long position() {
        return position;
    }
}
