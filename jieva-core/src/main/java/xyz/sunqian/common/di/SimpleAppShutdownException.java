package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.util.List;

/**
 * Exception for shutdown failure of a {@link SimpleApp}.
 *
 * @author sunqian
 */
public class SimpleAppShutdownException extends JieRuntimeException {

    private final @Nonnull SimpleResource failedResource;
    private final @Nonnull List<@Nonnull SimpleResource> destroyedResources;
    private final @Nonnull List<@Nonnull SimpleResource> undestroyedResources;

    /**
     * Constructs a shutdown exception with detailed context about the resource cleanup failure. This exception provides
     * complete information about the resource that caused the failure, resources that were successfully destroyed
     * before the failure, and resources that remained undestroyed due to the abortive termination of the shutdown
     * process.
     * <p>
     * Note the resources have been sorted according to their dependency relationships.
     *
     * @param failedResource       the resource that failed during destruction, causing the shutdown to abort
     * @param cause                the exception that occurred during the failed resource's destruction
     * @param destroyedResources   the resources that were successfully destroyed before the failure occurred
     * @param undestroyedResources the resources that were not destroyed due to the abortive termination
     */
    public SimpleAppShutdownException(
        @Nonnull SimpleResource failedResource,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull SimpleResource> destroyedResources,
        @Nonnull @RetainedParam List<@Nonnull SimpleResource> undestroyedResources
    ) {
        super(cause);
        this.failedResource = failedResource;
        this.destroyedResources = destroyedResources;
        this.undestroyedResources = undestroyedResources;
    }

    /**
     * Returns the resource that failed during destruction, causing the shutdown to abort.
     *
     * @return the resource that failed during destruction, causing the shutdown to abort
     */
    public @Nonnull SimpleResource failedResource() {
        return failedResource;
    }

    /**
     * Returns the resources that were successfully destroyed before the failure occurred.
     *
     * @return the resources that were successfully destroyed before the failure occurred
     */
    public @Nonnull List<@Nonnull SimpleResource> destroyedResources() {
        return destroyedResources;
    }

    /**
     * Returns the resources that were not destroyed due to the abortive termination.
     *
     * @return the resources that were not destroyed due to the abortive termination
     */
    public @Nonnull List<@Nonnull SimpleResource> undestroyedResources() {
        return undestroyedResources;
    }
}
