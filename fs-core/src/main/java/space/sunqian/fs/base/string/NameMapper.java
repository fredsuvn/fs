package space.sunqian.fs.base.string;

import space.sunqian.annotation.Nonnull;

/**
 * Mapper for mapping name to another string.
 *
 * @author sunqian
 */
public interface NameMapper {

    /**
     * Returns a name mapper that keeps the name as it is, the logic is {@code name -> name}.
     *
     * @return a name mapper that keeps the name as it is
     */
    static @Nonnull NameMapper keep() {
        return NameMapperBack.KEEP;
    }

    /**
     * Creates a name mapper from the specified source formatter to the destination formatter.
     *
     * @param src the source formatter
     * @param dst the destination formatter
     * @return a name mapper from the specified source formatter to the destination formatter
     */
    static @Nonnull NameMapper with(@Nonnull NameFormatter src, @Nonnull NameFormatter dst) {
        return name -> src.format(name, dst);
    }

    /**
     * Maps the specified name to another string.
     *
     * @param name the specified name to be mapped
     * @return the mapped name
     */
    @Nonnull
    String map(@Nonnull String name);
}
