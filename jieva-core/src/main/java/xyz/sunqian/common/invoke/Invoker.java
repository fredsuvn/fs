package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interface represents an invocation handle to executable content, which can be a method, a constructor, a block
 * of code, or any other executable object.
 *
 * @author fredsuvn
 */
@FunctionalInterface
public interface Invoker {

    /**
     * Returns an {@link Invoker} instance for given method by reflecting.
     * <p>
     * Note if underlying exception is wrapped by {@link InvocationTargetException} when calls
     * {@link #invoke(Object, Object...)}, the {@link InvocationTargetException} will be erased, actual cause from
     * {@link InvocationTargetException#getCause()} will be wrapped by {@link InvocationException} then thrown.
     *
     * @param method given method
     * @return {@link Invoker} instance
     */
    static Invoker reflect(Method method) {
        return Impls.ofMethod(method);
    }

    /**
     * Returns an {@link Invoker} instance for given constructor by reflecting.
     * <p>
     * Note if underlying exception is wrapped by {@link InvocationTargetException} when calls
     * {@link #invoke(Object, Object...)}, the {@link InvocationTargetException} will be erased, actual cause from
     * {@link InvocationTargetException#getCause()} will be wrapped by {@link InvocationException} then thrown.
     *
     * @param constructor given constructor
     * @return {@link Invoker} instance
     */
    static Invoker reflect(Constructor<?> constructor) {
        return Impls.ofConstructor(constructor);
    }

    /**
     * Returns {@link Invoker} instance for given method by {@link MethodHandles.Lookup#unreflect(Method)}.
     *
     * @param method given method
     * @return {@link Invoker} instance
     */
    static Invoker handle(Method method) {
        return Impls.ofMethodHandle(method);
    }

    /**
     * Returns an {@link Invoker} instance for given constructor by
     * {@link MethodHandles.Lookup#unreflectConstructor(Constructor)}.
     *
     * @param constructor given constructor
     * @return {@link Invoker} instance
     */
    static Invoker handle(Constructor<?> constructor) {
        return Impls.ofMethodHandle(constructor);
    }

    /**
     * Returns an {@link Invoker} instance with specified {@link MethodHandle}.
     *
     * @param handle   specified {@link MethodHandle}
     * @param isStatic whether treats the handle as static when invoking
     * @return {@link Invoker} instance
     */
    static Invoker handle(MethodHandle handle, boolean isStatic) {
        return Impls.ofMethodHandle(handle, isStatic);
    }

    /**
     * Invokes with specified object and arguments.
     * <p>
     * If this invoker references a {@link Method}, {@link Constructor} or {@link MethodHandle}, the usage of its
     * parameters are equivalent to {@link Method#invoke(Object, Object...)}, {@link Constructor#newInstance(Object...)}
     * or {@link MethodHandle#invokeWithArguments(Object...)}. Otherwise, usage of the parameters is up to the
     * implementation.
     * <p>
     * This method only throws {@link InvocationException} if any problem occurs, use
     * {@link InvocationException#getCause()} to get underlying cause.
     *
     * @param obj  specified object
     * @param args specified arguments
     * @return result of invoking
     * @throws InvocationException wraps if any problem occurs
     */
    @Nullable
    Object invoke(@Nullable Object obj, Object... args) throws InvocationException;
}


