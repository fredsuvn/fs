package xyz.sunqian.common.invoke;

import java.lang.invoke.MethodHandle;

/**
 * This enum indicates the way to implement {@link Invocable}.
 *
 * @author sunqian
 */
public enum InvocationMode {

    /**
     * Using reflection to implement {@link Invocable}.
     */
    REFLECTION,

    /**
     * Using {@link MethodHandle} to implement {@link Invocable}.
     */
    METHOD_HANDLE,
    ;
}
