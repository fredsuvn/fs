package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * The runtime exception for byte encoding.
 *
 * @author sunqian
 */
public class ByteEncodingException extends JieRuntimeException {

    private final long position;

    /**
     * Empty constructor.
     */
    public ByteEncodingException() {
        super();
        position = -1;
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ByteEncodingException(@Nullable String message) {
        super(message);
        position = -1;
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ByteEncodingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        position = -1;
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ByteEncodingException(@Nullable Throwable cause) {
        super(cause);
        position = -1;
    }

    /**
     * Constructs with the position.
     *
     * @param position the position
     */
    public ByteEncodingException(long position) {
        super();
        this.position = position;
    }

    /**
     * Constructs with the position and message.
     *
     * @param position the position
     * @param message  the message
     */
    public ByteEncodingException(long position, @Nullable String message) {
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
    public ByteEncodingException(long position, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.position = position;
    }

    /**
     * Constructs with the position and cause.
     *
     * @param position the position
     * @param cause    the cause
     */
    public ByteEncodingException(long position, @Nullable Throwable cause) {
        super(cause);
        this.position = position;
    }

    /**
     * Returns the position of the encoding error starting from {@code 0}, may be {@code -1} if the error is not related
     * to a specific position.
     *
     * @return the position of the encoding error, may be {@code -1} if the error is not related
     */
    public long position() {
        return position;
    }
}
