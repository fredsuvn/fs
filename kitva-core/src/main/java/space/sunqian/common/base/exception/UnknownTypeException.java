package space.sunqian.common.base.exception;

import space.sunqian.annotations.Nonnull;

import java.lang.reflect.Type;

/**
 * This runtime exception indicates encountering an unknown type.
 *
 * @author sunqian
 */
public class UnknownTypeException extends KitvaRuntimeException {

    private static @Nonnull String toMessage(@Nonnull Type type) {
        return "Unknown type: " + type + ".";
    }

    private static @Nonnull String toMessage(@Nonnull Type type, @Nonnull String keyword) {
        return "Unknown " + keyword + " type: " + type + ".";
    }

    private static @Nonnull String toMessage(@Nonnull String typeName) {
        return "Unknown type: " + typeName + ".";
    }

    /**
     * Constructs with the unknown type.
     *
     * @param type the unknown type
     */
    public UnknownTypeException(@Nonnull Type type) {
        super(toMessage(type));
    }

    /**
     * Constructs with the unknown type and the keyword for the type. The message of this exception will be:
     * {@code "Unknown " + keyword + " type: " + type + "."}.
     *
     * @param type    the unknown type
     * @param keyword the keyword for the type
     */
    public UnknownTypeException(@Nonnull Type type, @Nonnull String keyword) {
        super(toMessage(type, keyword));
    }

    /**
     * Constructs with the name of unknown type.
     *
     * @param typeName the name of unknown type
     */
    public UnknownTypeException(@Nonnull String typeName) {
        super(toMessage(typeName));
    }
}
