package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.exception.FsRuntimeException;

import java.util.List;

/**
 * Exception for failing to destroy a DI container.
 *
 * @author sunqian
 */
public class DIShutdownException extends FsRuntimeException {

    private final @Nonnull DIComponent failedComponent;
    private final @Nonnull List<@Nonnull DIComponent> destroyedComponents;
    private final @Nonnull List<@Nonnull DIComponent> undestroyedComponents;

    public DIShutdownException(
        @Nonnull DIComponent failedComponent,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull DIComponent> destroyedComponents,
        @Nonnull @RetainedParam List<@Nonnull DIComponent> undestroyedComponents
    ) {
        super(cause);
        this.failedComponent = failedComponent;
        this.destroyedComponents = destroyedComponents;
        this.undestroyedComponents = undestroyedComponents;
    }

    /**
     * Returns the component that failed to destroy.
     *
     * @return the component that failed to destroy
     */
    public @Nonnull DIComponent failedComponent() {
        return failedComponent;
    }

    /**
     * Returns the components that were successfully destroyed before the failure occurred.
     *
     * @return the components that were successfully destroyed before the failure occurred
     */
    public @Nonnull List<@Nonnull DIComponent> destroyedComponents() {
        return destroyedComponents;
    }

    /**
     * Returns the components that were not destroyed due to the failure.
     *
     * @return the components that were not destroyed due to the failure
     */
    public @Nonnull List<@Nonnull DIComponent> undestroyedComponents() {
        return undestroyedComponents;
    }
}
