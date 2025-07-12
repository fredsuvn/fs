package xyz.sunqian.common.base.exception;

import java.lang.reflect.Type;

/**
 * This runtime exception indicates encountering an unknown type.
 *
 * @author sunqian
 */
public class UnknownTypeException extends JieRuntimeException {

    /**
     * Constructs with the unknown type.
     *
     * @param type the unknown type
     */
    public UnknownTypeException(Type type) {
        super("Unknown type: " + type + ".");
    }

    /**
     * Constructs with the unknown type and the keyword for the type. The message of this exception will be:
     * {@code "Unknown " + keyword + " type: " + type + "."}.
     *
     * @param type    the unknown type
     * @param keyword the keyword for the type
     */
    public UnknownTypeException(Type type, String keyword) {
        super("Unknown " + keyword + " type: " + type + ".");
    }
}
