package space.sunqian.common.base.lang;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

/**
 * Utilities for enum.
 *
 * @author sunqian
 */
public class EnumKit {

    /**
     * Returns the enum constant of the specified enum type with the specified name, or {@code null} if the constant
     * does not exist.
     *
     * @param enumType the specified enum type
     * @param name     the specified name
     * @param <T>      the type of enum
     * @return the enum constant of the specified enum type with the specified name, or {@code null} if the constant
     * does not exist
     */
    public static <T extends Enum<T>> @Nullable T findEnum(@Nonnull Class<T> enumType, @Nonnull String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the enum constant of the specified enum type at the specified index where the constant is declared,
     * starting at {@code 0}, or {@code null} if the index is out of bounds.
     *
     * @param enumType the specified enum type
     * @param index    the specified index
     * @param <T>      the type of enum
     * @return the enum constant of the specified enum type with the specified name, or {@code null} if the index is out
     * of bounds
     */
    public static <T extends Enum<T>> @Nullable T findEnum(@Nonnull Class<T> enumType, int index) {
        T[] enums = enumType.getEnumConstants();
        if (enums == null || index < 0 || index >= enums.length) {
            return null;
        }
        return enums[index];
    }
}
