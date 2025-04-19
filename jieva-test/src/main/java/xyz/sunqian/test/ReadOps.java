package xyz.sunqian.test;

import java.io.IOException;

/**
 * Specifies the read options.
 *
 * @author sunqian
 */
public enum ReadOps {

    /**
     * Represents the behavior for next read operation: directly using the read methods of wrapped input stream.
     */
    READ_NORMAL,

    /**
     * Represents the behavior for next read operation: reads 0-byte.
     */
    READ_ZERO,

    /**
     * Represents the behavior for next read operation: reaches the end of the stream.
     */
    REACH_END,

    /**
     * Represents the behavior for next read operation: throws an {@link IOException}.
     * <p>
     * The read operation includes read, skip, and available methods.
     */
    THROW,
    ;
}
