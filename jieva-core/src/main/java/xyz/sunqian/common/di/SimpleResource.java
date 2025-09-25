package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents resource of a {@link SimpleApp}.
 * <p>
 * A resource is a singleton instance of a specified type, and different generics defined under the same type are
 * considered as different types.
 *
 * @author sunqian
 */
public interface SimpleResource {

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
     * Returns the pre-destroy method of this resource.
     *
     * @return the pre-destroy method of this resource
     */
    @Nullable
    Method preDestroyMethod();
}
