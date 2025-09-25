package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.util.List;

/**
 * Exception for failing to initialize (post-construct) resources during startup of a {@link SimpleApp}.
 *
 * @author sunqian
 */
public class SimpleResourceInitialException extends JieRuntimeException {

    private final @Nonnull SimpleResource failedResource;
    private final @Nonnull List<@Nonnull SimpleResource> initializedResources;
    private final @Nonnull List<@Nonnull SimpleResource> uninitializedResources;

    /**
     * Constructs an exception with detailed context about the resource initialize failure. This exception provides
     * complete information about the resource that caused the failure, resources that were successfully initialized
     * before the failure, and resources that remained uninitialized due to the abortive termination of the startup
     * process.
     * <p>
     * Note the resources have been sorted according to their dependency relationships.
     *
     * @param failedResource         the resource that failed during initialization, causing the startup to abort
     * @param cause                  the exception that occurred during the failed resource's initialization
     * @param initializedResources   the resources that were successfully initialized before the failure occurred
     * @param uninitializedResources the resources that were not initialized due to the abortive termination
     */
    public SimpleResourceInitialException(
        @Nonnull SimpleResource failedResource,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull SimpleResource> initializedResources,
        @Nonnull @RetainedParam List<@Nonnull SimpleResource> uninitializedResources
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
    public @Nonnull SimpleResource failedResource() {
        return failedResource;
    }

    /**
     * Returns the resources that were successfully initialized before the failure occurred.
     *
     * @return the resources that were successfully initialized before the failure occurred
     */
    public @Nonnull List<@Nonnull SimpleResource> initializedResources() {
        return initializedResources;
    }

    /**
     * Returns the resources that were not initialized due to the abortive termination.
     *
     * @return the resources that were not initialized due to the abortive termination
     */
    public @Nonnull List<@Nonnull SimpleResource> uninitializedResources() {
        return uninitializedResources;
    }
}
