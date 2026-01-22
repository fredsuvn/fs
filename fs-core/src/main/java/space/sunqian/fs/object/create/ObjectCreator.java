package space.sunqian.fs.object.create;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.ThreadSafe;

import java.lang.reflect.Type;

/**
 * This object creator interface is used to build an object for the target type through the following steps:
 * <ol>
 *     <li>
 *         Calls {@link #createBuilder()} to create a builder for the target type.
 *     </li>
 *     <li>
 *         Configures the builder elsewhere.
 *     </li>
 *     <li>
 *         Calls {@link #buildTarget(Object)} to build the final object from the given builder, maybe the final object
 *         is the same one as the given builder.
 *     </li>
 * </ol>
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectCreator {

    /**
     * Returns an instance of {@link ObjectCreator} for the target type, or {@code null} if the target type is
     * unsupported, using {@link CreatorProvider#defaultProvider()}.
     * <p>
     * Note this method never caches the returned {@link ObjectCreator} instances.
     *
     * @param target the target type
     * @return a new {@link ObjectCreator}, or {@code null} if the target type is unsupported via the default provider
     * @throws ObjectCreateException if an error occurs while creating the {@link ObjectCreator}
     */
    static @Nullable ObjectCreator forType(@Nonnull Type target) throws ObjectCreateException {
        return CreatorProvider.defaultProvider().forType(target);
    }

    /**
     * Returns the builder type.
     *
     * @return the builder type
     */
    @Nonnull
    Type builderType();

    /**
     * Returns the target type.
     *
     * @return the target type
     */
    @Nonnull
    Type targetType();

    /**
     * Creates a builder for the target type.
     *
     * @return a builder for the target type
     * @throws ObjectCreateException if an error occurs during the creation of the builder
     */
    @Nonnull
    Object createBuilder() throws ObjectCreateException;

    /**
     * Builds a target object from the given builder, maybe the final object is the same one as the given builder.
     *
     * @param builder the given builder
     * @return a target object from the given builder
     * @throws ObjectCreateException if an error occurs during the creation of the target object
     */
    @Nonnull
    Object buildTarget(@Nonnull Object builder) throws ObjectCreateException;
}
