package space.sunqian.test;

import java.io.IOException;

/**
 * Specifies the I/O related options.
 *
 * @author sunqian
 */
public enum ReadOps {

    /**
     * Represents the normal read behavior, typically means directly using the methods of the wrapped read source.
     */
    READ_NORMAL,

    /**
     * Represents the behavior: reads 0-byte.
     */
    READ_ZERO,

    /**
     * Represents the behavior: reaches the end of the read source.
     */
    REACH_END,

    /**
     * Represents the behavior: throws an {@link IOException} for all I/O operations.
     */
    THROW,
    ;
}
