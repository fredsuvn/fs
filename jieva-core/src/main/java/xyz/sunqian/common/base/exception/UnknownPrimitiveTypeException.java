package xyz.sunqian.common.base.exception;

import java.lang.reflect.Type;

/**
 * This runtime exception indicates encountering an unknown primitive type.
 *
 * @author sunqian
 */
public class UnknownPrimitiveTypeException extends JieRuntimeException {

    /**
     * Constructs with the unknown primitive type.
     *
     * @param type the unknown primitive type
     */
    public UnknownPrimitiveTypeException(Type type) {
        super("Unknown primitive type: " + type + ".");
    }
}
