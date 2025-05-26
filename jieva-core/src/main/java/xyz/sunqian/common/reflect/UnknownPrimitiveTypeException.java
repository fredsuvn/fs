package xyz.sunqian.common.reflect;

import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.lang.reflect.Type;

/**
 * This runtime exception indicates that occurs an unknown primitive type.
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
