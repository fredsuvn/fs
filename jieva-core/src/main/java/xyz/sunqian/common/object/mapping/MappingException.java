package xyz.sunqian.common.object.mapping;

import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Exception for mapper.
 *
 * @author fredsuvn
 */
public class MappingException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public MappingException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public MappingException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with source type and target type.
     *
     * @param obj        object to be mapped
     * @param sourceType source type
     * @param targetType target type
     */
    public MappingException(@Nullable Object obj, Type sourceType, Type targetType) {
        this("Failed to map object from " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ": " + obj + ".");
    }

    /**
     * Constructs with source type, target type and exception cause.
     *
     * @param sourceType source type
     * @param targetType target type
     * @param cause      exception cause
     */
    public MappingException(Type sourceType, Type targetType, Throwable cause) {
        this("Failed to map " + sourceType.getTypeName() + " to " + targetType.getTypeName() + ".", cause);
    }
}
