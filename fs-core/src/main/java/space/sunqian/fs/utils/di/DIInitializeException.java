package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.exception.FsRuntimeException;

import java.util.List;

/**
 * Exception for failing to initialize a DI container.
 *
 * @author sunqian
 */
public class DIInitializeException extends FsRuntimeException {

    private final @Nonnull DIComponent failedComponent;
    private final @Nonnull List<@Nonnull DIComponent> initializedComponents;
    private final @Nonnull List<@Nonnull DIComponent> uninitializedComponents;

    /**
     * Constructs with the failed component, cause, initialized components, and uninitialized components.
     *
     * @param failedComponent         the component that failed to initialize
     * @param cause                   the cause
     * @param initializedComponents   the components that were successfully initialized before the failure occurred
     * @param uninitializedComponents the components that were not initialized due to the failure
     */
    public DIInitializeException(
        @Nonnull DIComponent failedComponent,
        @Nullable Throwable cause,
        @Nonnull @RetainedParam List<@Nonnull DIComponent> initializedComponents,
        @Nonnull @RetainedParam List<@Nonnull DIComponent> uninitializedComponents
    ) {
        super(cause);
        this.failedComponent = failedComponent;
        this.initializedComponents = initializedComponents;
        this.uninitializedComponents = uninitializedComponents;
    }

    /**
     * Returns the component that failed to initialize.
     *
     * @return the component that failed to initialize
     */
    public @Nonnull DIComponent failedComponent() {
        return failedComponent;
    }

    /**
     * Returns the components that were successfully initialized before the failure occurred.
     *
     * @return the components that were successfully initialized before the failure occurred
     */
    public @Nonnull List<@Nonnull DIComponent> initializedComponents() {
        return initializedComponents;
    }

    /**
     * Returns the components that were not initialized due to the failure.
     *
     * @return the components that were not initialized due to the failure
     */
    public @Nonnull List<@Nonnull DIComponent> uninitializedComponents() {
        return uninitializedComponents;
    }
}
