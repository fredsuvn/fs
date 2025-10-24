package xyz.sunqian.common.app.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.util.List;

/**
 * Exception for failing to initialize resources during {@link InjectedApp} startup.
 *
 * @author sunqian
 */
public class InjectedResourceInitializationException extends JieRuntimeException {

    private final @Nonnull InjectedResource failedResource;
    private final @Nonnull List<@Nonnull InjectedResource> initializedResources;
    private final @Nonnull List<@Nonnull InjectedResource> uninitializedResources;

    /**
     * Constructs an exception with detailed context about the resource initialize failure. This exception provides
     * complete information about the resource that caused the failure, resources that were successfully initialized
     * before the failure, and resources that remained uninitialized due to the failure.
     * <p>
     * Note the resources have already been sorted according to their dependency relationships.
     *
     * @param failedResource         the resource that failed during initialization, causing the startup to abort
     * @param cause                  the exception that occurred during the failed resource's initialization
     * @param initializedResources   the resources that were successfully initialized before the failure occurred
     * @param uninitializedResources the resources that were not initialized due to the abortive termination
     */
    public InjectedResourceInitializationException(
        @Nonnull InjectedResource failedResource,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull InjectedResource> initializedResources,
        @Nonnull @RetainedParam List<@Nonnull InjectedResource> uninitializedResources
    ) {
        super(cause);
        this.failedResource = failedResource;
        this.initializedResources = initializedResources;
        this.uninitializedResources = uninitializedResources;
    }

    /**
     * Returns the resource that failed during initialization, causing the startup to abort。
     *
     * @return the resource that failed during initialization, causing the startup to abort
     */
    public @Nonnull InjectedResource failedResource() {
        return failedResource;
    }

    /**
     * Returns the resources that were successfully initialized before the failure occurred.
     *
     * @return the resources that were successfully initialized before the failure occurred
     */
    public @Nonnull List<@Nonnull InjectedResource> initializedResources() {
        return initializedResources;
    }

    /**
     * Returns the resources that were not initialized due to the failure.
     *
     * @return the resources that were not initialized due to the failure
     */
    public @Nonnull List<@Nonnull InjectedResource> uninitializedResources() {
        return uninitializedResources;
    }
}
