package xyz.sunqian.common.app.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.runtime.invoke.InvocationException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents resource of {@link InjectedApp}, consisting of a type and a singleton instance of that type.
 * InjectedResources with the same raw class but different generic type parameters are considered distinct and are not
 * equal to each other.
 *
 * @author sunqian
 */
public interface InjectedResource {

    /**
     * Returns the type of this resource.
     *
     * @return the type of this resource
     */
    @Nonnull
    Type type();

    /**
     * Returns the singleton instance of this resource's type.
     *
     * @return the singleton instance of this resource's type
     */
    @Nonnull
    Object instance();

    /**
     * Returns whether this resource is directly owned by the current app, rather than inherited from parent apps
     *
     * @return {@code true} if this resource is directly owned by the current app, rather than inherited from parent
     * apps; {@code false} otherwise
     */
    boolean isLocal();

    /**
     * Returns the post-construct method of this resource.
     *
     * @return the post-construct method of this resource
     */
    @Nullable
    Method postConstructMethod();

    /**
     * Invokes the post-construct method of this resource if the post-construct method is not {@code null}, otherwise
     * this method has no effect.
     *
     * @throws InvocationException for wrapping any error during execution of the method
     */
    void postConstruct() throws InvocationException;

    /**
     * Returns whether this resource is initialized, the post-construct method has also been executed normally.
     *
     * @return {@code true} if this resource is initialized; {@code false} otherwise
     */
    boolean isInitialized();

    /**
     * Returns the pre-destroy method of this resource.
     *
     * @return the pre-destroy method of this resource
     */
    @Nullable
    Method preDestroyMethod();

    /**
     * Invokes the pre-destroy method of this resource if the pre-destroy method is not {@code null}, otherwise this
     * method has no effect.
     *
     * @throws InvocationException for wrapping any error during execution of the method
     */
    void preDestroy() throws InvocationException;

    /**
     * Returns whether this resource is destroyed, the pre-destroy method has also been executed normally.
     *
     * @return {@code true} if this resource is destroyed; {@code false} otherwise
     */
    boolean isDestroyed();
}
