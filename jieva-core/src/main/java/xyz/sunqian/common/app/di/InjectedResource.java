package xyz.sunqian.common.app.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.runtime.invoke.InvocationException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents resource of a {@link InjectedApp}.
 * <p>
 * A resource contains a specified type and a singleton instance of the type. The different generics defined under the
 * same type are considered as different types.
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
     * Returns the instance of this resource.
     *
     * @return the instance of this resource
     */
    @Nonnull
    Object instance();

    /**
     * Returns whether this resource is generated and managed by current app rather than its parents.
     *
     * @return {@code true} if this resource is generated and managed by current app rather than its parents,
     * {@code false} otherwise
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
     * @throws InvocationException for wrapping any error during execution of post-construct method
     */
    void postConstruct() throws InvocationException;

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
     * @throws InvocationException for wrapping any error during execution of pre-destroy method
     */
    void preDestroy() throws InvocationException;
}
