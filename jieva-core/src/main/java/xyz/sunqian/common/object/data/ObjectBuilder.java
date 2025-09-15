package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

/**
 * This interface is used to build an object for the target type through the following methods:
 * <ol>
 *     <li>
 *         Calls {@link #newBuilder()} to create a builder for the target type.
 *     </li>
 *     <li>
 *         Sets the properties to the builder.
 *     </li>
 *     <li>
 *         Calls {@link #build(Object)} to make the builder generate the target object.
 *     </li>
 * </ol>
 * It unifies two methods for generating and setting data objects: directly setting or through a builder.
 *
 * @author sunqian
 */
public interface ObjectBuilder {

    /**
     * Returns an instance of {@link ObjectBuilder}, or {@code null} if the target type is unsupported.
     * <p>
     * Note this method is supported by {@link ObjectBuilderProvider#defaultProvider()}.
     *
     * @param target the target type
     * @return an instance of {@link ObjectBuilder}, or {@code null} if the target type is unsupported
     * @throws DataObjectException if an error occurs
     */
    static @Nullable ObjectBuilder get(@Nonnull Class<?> target) throws DataObjectException {
        return ObjectBuilderProvider.defaultProvider().builder(target);
    }

    /**
     * Creates and returns a new builder for the target type.
     *
     * @return a new builder for the target type
     * @throws DataObjectException if failed to create
     */
    @Nonnull
    Object newBuilder() throws DataObjectException;

    /**
     * Makes the given builder, which is returned by {@link #newBuilder()} of the same instance of
     * {@link ObjectBuilder}, generate the target object of the specified target type.
     *
     * @param builder the given builder from {@link #newBuilder()} of the same instance of {@link ObjectBuilder}
     * @return the object of the target type
     * @throws DataObjectException if failed to build
     */
    @Nonnull
    Object build(@Nonnull Object builder) throws DataObjectException;
}
