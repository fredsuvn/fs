package xyz.sunqian.common.base.exception;

import java.lang.reflect.Type;

/**
 * This runtime exception indicates encountering an unknown array type.
 *
 * @author sunqian
 */
public class UnknownArrayTypeException extends UnknownTypeException {

    /**
     * Constructs with the unknown array type.
     *
     * @param type the unknown array type
     */
    public UnknownArrayTypeException(Type type) {
        super(type, "array");
    }
}
