package space.sunqian.common.object.convert;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.exception.KitvaRuntimeException;

import java.lang.reflect.Type;

/**
 * Exception for object conversion.
 *
 * @author sunqian
 */
public class ObjectConvertException extends KitvaRuntimeException {

    private static String toMessage(@Nonnull Type sourceType, @Nonnull Type targetType) {
        return "Conversion failed: " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".";
    }

    /**
     * Empty constructor.
     */
    public ObjectConvertException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectConvertException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectConvertException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectConvertException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with the source type and target type.
     *
     * @param sourceType the source type
     * @param targetType the target type
     */
    public ObjectConvertException(@Nonnull Type sourceType, @Nonnull Type targetType) {
        this(toMessage(sourceType, targetType));
    }

    /**
     * Constructs with the source type, target type and cause.
     *
     * @param sourceType the source type
     * @param targetType the target type
     * @param cause      the cause
     */
    public ObjectConvertException(@Nonnull Type sourceType, @Nonnull Type targetType, @Nullable Throwable cause) {
        this(toMessage(sourceType, targetType), cause);
    }
}
