package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.lang.reflect.Type;

/**
 * Exception for object conversion.
 *
 * @author sunqian
 */
public class ObjectConversionException extends JieRuntimeException {

    private static String buildTypeMessage(@Nonnull Type sourceType, @Nonnull Type targetType) {
        return "Conversion failed: " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".";
    }

    /**
     * Empty constructor.
     */
    public ObjectConversionException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public ObjectConversionException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ObjectConversionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public ObjectConversionException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and target type.
     *
     * @param sourceType source type
     * @param targetType target type
     */
    public ObjectConversionException(@Nonnull Type sourceType, @Nonnull Type targetType) {
        this(buildTypeMessage(sourceType, targetType));
    }

    /**
     * Constructs with source type, target type and exception cause.
     *
     * @param sourceType source type
     * @param targetType target type
     * @param cause      exception cause
     */
    public ObjectConversionException(@Nonnull Type sourceType, @Nonnull Type targetType, @Nullable Throwable cause) {
        this(buildTypeMessage(sourceType, targetType), cause);
    }
}
