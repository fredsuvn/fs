package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.reflect.ClassKit;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

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

    /**
     * Using <a href="https://asm.ow2.io/">ASM</a> to implement {@link Invocable}.
     */
    ASM,
    ;

    /**
     * Returns the recommended implementation for the specified method in current environment.
     *
     * @param method the specified method
     * @return the recommended implementation for the specified method in current environment
     */
    public static @Nonnull InvocationMode recommended(@Nonnull Method method) {
        return recommended(method, ClassKit.isStatic(method));
    }

    /**
     * Returns the recommended implementation for the specified constructor in current environment.
     *
     * @param constructor the specified constructor
     * @return the recommended implementation for the specified constructor in current environment
     */
    public static @Nonnull InvocationMode recommended(@Nonnull Constructor<?> constructor) {
        return recommended(constructor, true);
    }

    private static @Nonnull InvocationMode recommended(@Nonnull Executable executable, boolean isStatic) {
        int paramCount = executable.getParameterCount();
        if (isStatic) {
            return paramCount <= OfMethodHandle.MAX_INSTANCE_ARGS_IMPL ? METHOD_HANDLE : REFLECTION;
        }
        return paramCount <= OfMethodHandle.MAX_INSTANCE_ARGS_IMPL ? METHOD_HANDLE : ASM;
    }
}
