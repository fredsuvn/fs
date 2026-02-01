package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Type;

/**
 * This interface is used to map the source property name to the target property name.
 *
 * @author sunqian
 */
public interface PropertyNameMapper {

    /**
     * Returns the default {@link PropertyNameMapper}, it only returns the source property name itself.
     *
     * @return the default {@link PropertyNameMapper}
     */
    static @Nonnull PropertyNameMapper defaultMapper() {
        return PropertyNameMapperImpl.INST;
    }

    /**
     * Maps the source property name to the target property name.
     *
     * @param srcPropertyName the source property name
     * @param srcType         the source type
     * @return the target property name
     */
    @Nonnull
    String map(@Nonnull String srcPropertyName, @Nonnull Type srcType);
}
