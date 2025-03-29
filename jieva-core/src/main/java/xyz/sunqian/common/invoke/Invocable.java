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
     * Returns an {@link Invocable} that wraps the specified method based on reflection.
     *
     * @param method the specified method
     * @return an {@link Invocable} that wraps the specified method based on reflection
     */
    static Invocable reflect(Method method) {
        return InvocableBack.ofMethod(method);
    }

    /**
     * Returns an {@link Invocable} that wraps the specified constructor based on reflection.
     *
     * @param constructor the specified constructor
     * @return an {@link Invocable} that wraps the specified constructor based on reflection
     */
    static Invocable reflect(Constructor<?> constructor) {
        return InvocableBack.ofConstructor(constructor);
    }

    /**
     * Returns an {@link Invocable} that wraps the specified method based on {@link MethodHandle}.
     *
     * @param method the specified method
     * @return an {@link Invocable} that wraps the specified method based on {@link MethodHandle}
     */
    static Invocable handle(Method method) {
        return InvocableBack.ofMethodHandle(method);
    }

    /**
     * Returns an {@link Invocable} that wraps the specified constructor based on {@link MethodHandle}.
     *
     * @param constructor the specified constructor
     * @return an {@link Invocable} that wraps the specified constructor based on {@link MethodHandle}
     */
    static Invocable handle(Constructor<?> constructor) {
        return InvocableBack.ofMethodHandle(constructor);
    }

    /**
     * Returns an {@link Invocable} that wraps the specified method handle.
     *
     * @param handle   the specified method handle
     * @param isStatic whether the method handle is for a static method, constructor, or similar
     * @return an {@link Invocable} that wraps the specified method handle
     */
    static Invocable handle(MethodHandle handle, boolean isStatic) {
        return InvocableBack.ofMethodHandle(handle, isStatic);
    }

    /**
     * Executes this entity with an instance and invocation arguments. The instance typically represents the instance of
     * a method or the scope of {@code this}, while the arguments typically represent the actual arguments of a method
     * or function. The instance can be {@code null} if this entity is a static method, constructor, or other similar.
     * <p>
     * This method only throws {@link InvocationException} for any error, the raw cause can be retrieved by
     * {@link InvocationException#getCause()}.
     *
     * @param inst the instance
     * @param args the invocation arguments
     * @return result of invocation
     * @throws InvocationException for any error
     */
    @Nullable
    Object invoke(@Nullable Object inst, Object... args) throws InvocationException;
}
