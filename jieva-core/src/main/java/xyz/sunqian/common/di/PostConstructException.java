package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.util.List;

/**
 * This exception is thrown when a post-construct method execution fails for {@link SimpleApp}.
 *
 * @author sunqian
 */
public class PostConstructException extends JieRuntimeException {

    private final @Nonnull List<@Nonnull SimpleResource> executedResources;
    private final @Nonnull SimpleResource failedResource;

    /**
     * Constructs with the resource of the failed execution, the cause for the failed execution, and the resources that
     * have already been executed before the failed resource.
     *
     * @param failedResource    the resource of the failed execution
     * @param cause             the cause for the failed execution
     * @param executedResources the resources that have already been executed before the failed resource
     */
    public PostConstructException(
        @Nonnull SimpleResource failedResource,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull SimpleResource> executedResources
    ) {
        super(cause);
        this.executedResources = executedResources;
        this.failedResource = failedResource;
    }

    /**
     * Returns the resource of the failed execution.
     *
     * @return the resource of the failed execution
     */
    public @Nonnull SimpleResource failedResource() {
        return failedResource;
    }

    /**
     * Returns the resources that have already been executed before the failed resource.
     *
     * @return the resources that have already been executed before the failed resource
     */
    public @Nonnull List<@Nonnull SimpleResource> executedResources() {
        return executedResources;
    }
}
