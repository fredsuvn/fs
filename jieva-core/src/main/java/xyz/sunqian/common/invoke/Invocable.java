package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This functional interface represents an executable entity such as a method, function, constructor, or other
 * executable task.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface Invocable {

    /**
     * Returns an {@link Invocable} instance that wraps given method using reflection-based invocation.
     *
     * @param method given method
     * @return an {@link Invocable} instance that wraps given method using reflection-based invocation
     */
    static Invocable reflect(Method method) {
        return InvocableBack.ofMethod(method);
    }

    /**
     * Returns an {@link Invocable} instance that wraps given constructor using reflection-based invocation.
     *
     * @param constructor given constructor
     * @return an {@link Invocable} instance that wraps given constructor using reflection-based invocation
     */
    static Invocable reflect(Constructor<?> constructor) {
        return InvocableBack.ofConstructor(constructor);
    }

    /**
     * Returns an {@link Invocable} instance that wraps given method using {@link MethodHandle}-based invocation.
     *
     * @param method given method
     * @return an {@link Invocable} instance that wraps given method using {@link MethodHandle}-based invocation
     */
    static Invocable handle(Method method) {
        return InvocableBack.ofMethodHandle(method);
    }

    /**
     * Returns an {@link Invocable} instance that wraps given constructor using {@link MethodHandle}-based invocation.
     *
     * @param constructor given constructor
     * @return an {@link Invocable} instance that wraps given constructor using {@link MethodHandle}-based invocation
     */
    static Invocable handle(Constructor<?> constructor) {
        return InvocableBack.ofMethodHandle(constructor);
    }

    /**
     * Returns an {@link Invocable} instance that wraps given method handle.
     *
     * @param handle   given method handle
     * @param isStatic whether the handle is for a static method or similar
     * @return an {@link Invocable} instance that wraps given method handle
     */
    static Invocable handle(MethodHandle handle, boolean isStatic) {
        return InvocableBack.ofMethodHandle(handle, isStatic);
    }

    /**
     * Executes this entity with specified instance and arguments. The specified instance can be {@code null} if this
     * entity is not an instance method or similar.
     * <p>
     * This method only throws {@link InvocationException} for any problem, the raw cause can be retrieved by
     * {@link InvocationException#getCause()}.
     *
     * @param inst specified instance
     * @param args specified arguments
     * @return result of invocation
     * @throws InvocationException for any problem
     */
    @Nullable
    Object invoke(@Nullable Object inst, Object... args) throws InvocationException;
}
