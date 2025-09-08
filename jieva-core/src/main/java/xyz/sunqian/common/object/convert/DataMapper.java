package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.data.DataSchema;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This interface is used to copy properties from a data object to another data object.
 *
 * @author sunqian
 */
public interface DataMapper {

    /**
     * Copy properties from the given source data object to the given destination data object. Note if the object's type
     * is {@link Map}, the type will be parsed as {@link Map}, otherwise, the type will be parsed to
     * {@link DataSchema}.
     *
     * @param src the given source data object
     * @param dst the given destination data object
     * @throws ObjectConversionException if an error occurs during copying properties
     */
    default void copyProperties(@Nonnull Object src, @Nonnull Object dst) throws ObjectConversionException {
        copyProperties(src, src.getClass(), dst, dst.getClass());
    }

    /**
     * Copy properties from the given source data object to the given destination data object. Note if the object's type
     * is {@link Map}, the type will be parsed as {@link Map}, otherwise, the type will be parsed to
     *
     * @param src     the given source data object
     * @param srcType the type of the given source data object
     * @param dst     the given destination data object
     * @param dstType the type of the given destination data object
     * @throws ObjectConversionException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType
    ) throws ObjectConversionException {
        copyProperties(
            src,
            srcType,
            dst,
            dstType,
            ConversionOptions.defaultOptions()
        );
    }

    /**
     * Copy properties from the given source data object to the given destination data object. Note if the object's type
     * is {@link Map}, the type will be parsed as {@link Map}, otherwise, the type will be parsed to
     *
     * @param src       the given source data object
     * @param srcType   the type of the given source data object
     * @param dst       the given destination data object
     * @param dstType   the type of the given destination data object
     * @param options   the options for copying properties
     * @throws ObjectConversionException if an error occurs during copying properties
     */
    default void copyProperties(
        @Nonnull Object src,
        @Nonnull Type srcType,
        @Nonnull Object dst,
        @Nonnull Type dstType,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConversionException {

    }
}
