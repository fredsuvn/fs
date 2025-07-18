package xyz.sunqian.common.io;

import java.io.Closeable;

/**
 * This interface represents a closeable object whose {@code close} method has an unchecked exception. It is an
 * unchecked version of {@link Closeable} and {@link AutoCloseable}.
 */
public interface RuntimeCloseable {

    /**
     * Closes this object. If this object is already closed, this method has no effect.
     *
     * @throws IORuntimeException if an I/O error occurs
     */
    void close() throws IORuntimeException;
}
