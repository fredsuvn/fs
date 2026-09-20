package space.sunqian.fs.io;

import java.io.Closeable;

/**
 * The runtime version of {@link Closeable}, represents a source or destination of data that can be closed with runtime
 * exception declaration.
 *
 * @author sunqian
 */
public interface RuntimeCloseable extends Closeable {

    /**
     * Closes and releases this resource. If the resource is already closed then invoking this method has no effect.
     * This is the runtime version of {@link Closeable#close()} and {@link AutoCloseable#close()}.
     *
     * @throws IORuntimeException if an error occurs during the close operation
     */
    @Override
    void close() throws IORuntimeException;
}
