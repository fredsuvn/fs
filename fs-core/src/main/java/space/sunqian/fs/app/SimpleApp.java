package space.sunqian.fs.app;

import space.sunqian.fs.di.DIContainer;

/**
 * This interface represents a simple application. It is typically started by its creator and terminated by itself or
 * {@link #shutdown()}.
 *
 * @author sunqian
 * @implSpec This is the top-level interface, and {@link DIContainer} is typically used in practice.
 */
public interface SimpleApp {

    /**
     * Shuts down this app.
     * <p>
     * This method blocks current thread until the shutdown operation is completed.
     *
     * @throws SimpleAppException if any error occurs during shutdown operation
     */
    void shutdown() throws SimpleAppException;
}
