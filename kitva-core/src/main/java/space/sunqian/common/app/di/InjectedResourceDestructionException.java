package space.sunqian.common.app.di;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.common.base.exception.KitvaRuntimeException;

import java.util.List;

/**
 * Exception for failing to destroy resources during {@link InjectedApp} shutdown.
 *
 * @author sunqian
 */
public class InjectedResourceDestructionException extends KitvaRuntimeException {

    private final @Nonnull InjectedResource failedResource;
    private final @Nonnull List<@Nonnull InjectedResource> destroyedResources;
    private final @Nonnull List<@Nonnull InjectedResource> undestroyedResources;

    /**
     * Constructs an exception with detailed context about the resource destroy failure. This exception provides
     * complete information about the resource that caused the failure, resources that were successfully destroyed
     * before the failure, and resources that remained undestroyed due to the failure.
     * <p>
     * Note the resources have already been sorted according to their dependency relationships.
     *
     * @param failedResource       the resource that failed during destruction, causing the shutdown to abort
     * @param cause                the exception that occurred during the failed resource's destruction
     * @param destroyedResources   the resources that were successfully destroyed before the failure occurred
     * @param undestroyedResources the resources that were not destroyed due to the abortive termination
     */
    public InjectedResourceDestructionException(
        @Nonnull InjectedResource failedResource,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull InjectedResource> destroyedResources,
        @Nonnull @RetainedParam List<@Nonnull InjectedResource> undestroyedResources
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
    public @Nonnull InjectedResource failedResource() {
        return failedResource;
    }

    /**
     * Returns the resources that were successfully destroyed before the failure occurred.
     *
     * @return the resources that were successfully destroyed before the failure occurred
     */
    public @Nonnull List<@Nonnull InjectedResource> destroyedResources() {
        return destroyedResources;
    }

    /**
     * Returns the resources that were not destroyed due to the failure.
     *
     * @return the resources that were not destroyed due to the failure
     */
    public @Nonnull List<@Nonnull InjectedResource> undestroyedResources() {
        return undestroyedResources;
    }
}
