package space.sunqian.fs.object;

import space.sunqian.annotation.Nonnull;

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
 *         Calls {@link #createTarget(Object)} to build the final object from the given builder, maybe the final object
 *         is the same one as the given builder.
 *     </li>
 * </ol>
 *
 * @author sunqian
 */
public interface ObjectCreator {

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
     * @throws ObjectException if an error occurs during the creation of the builder
     */
    @Nonnull
    Object createBuilder() throws ObjectException;

    /**
     * Creates a target object from the given builder, maybe the final object is the same one as the given builder.
     *
     * @param builder the given builder
     * @return a target object from the given builder
     * @throws ObjectException if an error occurs during the creation of the target object
     */
    @Nonnull
    Object createTarget(@Nonnull Object builder) throws ObjectException;
}
