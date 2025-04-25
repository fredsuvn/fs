package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This functional interface represents an invocable entity such as a method, constructor, function, or other executable
 * task.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface Invocable {

    /**
     * Returns an {@link Invocable} represents the specified method.
     *
     * @param method the specified method
     * @return an {@link Invocable} represents the specified method
     */
    static Invocable of(Method method) {
        return of(method, InvocationMode.DEFAULT);
    }

    /**
     * Returns an {@link Invocable} represents the specified constructor.
     *
     * @param constructor the specified constructor
     * @return an {@link Invocable} represents the specified constructor
     */
    static Invocable of(Constructor<?> constructor) {
        return of(constructor, InvocationMode.DEFAULT);
    }

    /**
     * Returns an {@link Invocable} represents the specified method.
     *
     * @param method the specified method
     * @param mode   specifies the implementation type of the returned instance
     * @return an {@link Invocable} represents the specified method
     */
    static Invocable of(Method method, InvocationMode mode) {
        switch (mode) {
            case REFLECTION:
                return InvocableBack.withReflection(method);
            case METHOD_HANDLE:
            default:
                return InvocableBack.withMethodHandle(method);
        }
    }

    /**
     * Returns an {@link Invocable} represents the specified constructor.
     *
     * @param constructor the specified constructor
     * @param mode        specifies the implementation type of the returned instance
     * @return an {@link Invocable} represents the specified constructor
     */
    static Invocable of(Constructor<?> constructor, InvocationMode mode) {
        switch (mode) {
            case REFLECTION:
                return InvocableBack.withReflection(constructor);
            case METHOD_HANDLE:
            default:
                return InvocableBack.withMethodHandle(constructor);
        }
    }

    /**
     * Returns an {@link Invocable} represents the specified method handle.
     *
     * @param handle   the specified method handle
     * @param isStatic specifies whether the method handle is for a static method, constructor, or similar
     * @return an {@link Invocable} represents the specified method handle
     */
    static Invocable of(MethodHandle handle, boolean isStatic) {
        return InvocableBack.ofMethodHandle(handle, isStatic);
    }

    /**
     * Executes this invocable entity with an instance and the invocation arguments. The instance typically represents
     * the owner object of a method or the {@code this} of a scope, while the arguments typically represent the actual
     * arguments passed to a method or function. The instance can be {@code null} if this entity is a static method,
     * constructor, or other similar.
     * <p>
     * This method only throws {@link InvocationException} for any error, the original cause can be retrieved by
     * {@link InvocationException#getCause()}.
     *
     * @param inst the instance
     * @param args the invocation arguments
     * @return result of invocation
     * @throws InvocationException for any error
     */
    @Nullable
    Object invoke(@Nullable Object inst, @Nullable Object... args) throws InvocationException;
}
