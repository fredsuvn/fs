package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;
import xyz.sunqian.common.base.option.Option;

import java.lang.reflect.Type;

/**
 * This exception is thrown when an object conversion is unsupported.
 *
 * @author sunqian
 */
public class UnsupportedObjectConversionException extends JieRuntimeException {

    private static @Nonnull String toMessage(
        @Nonnull Type srcType,
        @Nonnull Type target
    ) {
        return "Unsupported object conversion from " + srcType + " to " + target + ".";
    }

    private final @Nullable Object src;
    private final @Nonnull Type srcType;
    private final @Nonnull Type target;
    private final @Nonnull ObjectConverter converter;
    private final @Nonnull Option<?, ?> @Nonnull [] options;

    /**
     * Constructs with the specified conversion parameters.
     *
     * @param src       the source object to be converted
     * @param srcType   the specified type of the source object
     * @param target    the target type to convert to
     * @param converter the converter object where this exception throws from
     * @param options   the options used in the conversion
     */
    public UnsupportedObjectConversionException(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull [] options
    ) {
        super(toMessage(srcType, target));
        this.src = src;
        this.srcType = srcType;
        this.target = target;
        this.converter = converter;
        this.options = options;
    }

    /**
     * Returns the source object to be converted.
     *
     * @return the source object to be converted
     */
    public @Nullable Object sourceObject() {
        return src;
    }

    /**
     * Returns the specified type of the source object.
     *
     * @return the specified type of the source object
     */
    public @Nonnull Type sourceObjectType() {
        return srcType;
    }

    /**
     * Returns the target type to convert to.
     *
     * @return the target type to convert to
     */
    public @Nonnull Type targetType() {
        return target;
    }

    /**
     * Returns the converter object where this exception throws from.
     *
     * @return the converter object where this exception throws from
     */
    public @Nonnull ObjectConverter converter() {
        return converter;
    }

    /**
     * Returns the options used in the conversion.
     *
     * @return the options used in the conversion
     */
    public @Nonnull Option<?, ?> @Nonnull [] options() {
        return options;
    }
}
