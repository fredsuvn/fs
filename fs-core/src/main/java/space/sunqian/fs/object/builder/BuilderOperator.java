package space.sunqian.fs.object.builder;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.ThreadSafe;

import java.lang.reflect.Type;

/**
 * This interface is used to execute the process of object creation and building for the target type through the
 * following steps:
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
public interface BuilderOperator {

    /**
     * Returns an instance of {@link BuilderOperator} for the target type, or {@code null} if the target type is
     * unsupported, using {@link BuilderOperatorProvider#defaultProvider()}.
     * <p>
     * Note this method never caches the returned {@link BuilderOperator} instances.
     *
     * @param target the target type
     * @return a new {@link BuilderOperator}, or {@code null} if the target type is unsupported via the default provider
     * @throws ObjectBuilderException if an error occurs while creating the {@link BuilderOperator}
     */
    static @Nullable BuilderOperator of(@Nonnull Type target) throws ObjectBuilderException {
        return BuilderOperatorProvider.defaultProvider().forType(target);
    }

    /**
     * Returns the builder type for the target type.
     *
     * @return the builder type for the target type
     */
    @Nonnull
    Type builderType();

    /**
     * Returns the target type of this {@link BuilderOperator}.
     *
     * @return the target type of this {@link BuilderOperator}
     */
    @Nonnull
    Type targetType();

    /**
     * Creates and returns a builder for the target type.
     *
     * @return a builder for the target type
     * @throws ObjectBuilderException if an error occurs during the creation of the builder
     */
    @Nonnull
    Object createBuilder() throws ObjectBuilderException;

    /**
     * Builds a target object from the given builder, maybe the final object is the same one as the given builder.
     *
     * @param builder the given builder
     * @return a target object from the given builder
     * @throws ObjectBuilderException if an error occurs during the creation of the target object
     */
    @Nonnull
    Object buildTarget(@Nonnull Object builder) throws ObjectBuilderException;
}
