package xyz.sunqian.common.app;

import xyz.sunqian.common.app.di.InjectedApp;

/**
 * This interface represents a simple application. It is typically started by its creator and terminated by itself or
 * {@link #shutdown()}. The default implementation is {@link InjectedApp}.
 *
 * @author sunqian
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
