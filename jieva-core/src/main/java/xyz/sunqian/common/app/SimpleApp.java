package xyz.sunqian.common.app;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.di.InjectedSimpleApp;

import java.util.List;

/**
 * This interface represents a simple application.
 * <p>
 * A SimpleApp is typically launched by its creator, such as {@link InjectedSimpleApp#newBuilder()}, and can be shutdown
 * by {@link #shutdown()}.
 *
 * @author sunqian
 */
public interface SimpleApp {

    /**
     * Shuts down this app.
     * <p>
     * This method blocks current thread until this app is closed.
     *
     * @throws SimpleAppException if any error occurs during shutdown
     */
    void shutdown() throws SimpleAppException;

    /**
     * Returns all resources this app depends on.
     *
     * @return all resources this app depends on
     */
    @Nonnull
    List<@Nonnull SimpleResource> resources();
}
