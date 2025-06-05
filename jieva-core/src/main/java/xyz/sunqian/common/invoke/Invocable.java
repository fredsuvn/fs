package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This interface represents an invocable entity, such as a method, constructor, function, executable task, executable
 * scope or other similar entity.
 * <p>
 * This interface provides two equivalent methods: {@link #invoke(Object, Object...)} which declares an unchecked
 * exception, and {@link #invokeChecked(Object, Object...)} which declares a checked exception.
 *
 * @author sunqian
 */
public interface Invocable {

    /**
     * Returns an {@link Invocable} represents the specified method.
     *
     * @param method the specified method
     * @return an {@link Invocable} represents the specified method
     */
    static @Nonnull Invocable of(@Nonnull Method method) {
        return of(method, InvocationMode.DEFAULT);
    }

    /**
     * Returns an {@link Invocable} represents the specified constructor.
     *
     * @param constructor the specified constructor
     * @return an {@link Invocable} represents the specified constructor
     */
    static @Nonnull Invocable of(@Nonnull Constructor<?> constructor) {
        return of(constructor, InvocationMode.DEFAULT);
    }

    /**
     * Returns an {@link Invocable} represents the specified method handle.
     *
     * @param handle   the specified method handle
     * @param isStatic specifies whether the method handle is for a static method, constructor, or similar
     * @return an {@link Invocable} represents the specified method handle
     */
    static @Nonnull Invocable of(@Nonnull MethodHandle handle, boolean isStatic) {
        return OfMethodHandle.forHandle(handle, isStatic);
    }

    /**
     * Returns an {@link Invocable} represents the specified method.
     *
     * @param method the specified method
     * @param mode   specifies the implementation of the returned instance
     * @return an {@link Invocable} represents the specified method
     */
    static @Nonnull Invocable of(@Nonnull Method method, @Nonnull InvocationMode mode) {
        return mode.generate(method);
    }

    /**
     * Returns an {@link Invocable} represents the specified constructor.
     *
     * @param constructor the specified constructor
     * @param mode        specifies the implementation of the returned instance
     * @return an {@link Invocable} represents the specified constructor
     */
    static @Nonnull Invocable of(@Nonnull Constructor<?> constructor, @Nonnull InvocationMode mode) {
        return mode.generate(constructor);
    }

    /**
     * Executes this invocable entity with an instance and the invocation arguments. The instance typically represents
     * the owner object of a method or the scope of '{@code this}', and the arguments typically represent the actual
     * arguments passed to the method or function. The instance can be {@code null} if this entity is a static method,
     * constructor, or other similar entity.
     * <p>
     * This method only throws {@link InvocationException} which wraps any other {@link Throwable}, the original cause
     * can be retrieved by {@link InvocationException#getCause()}. This method is equivalent to:
     * <pre>{@code
     * try {
     *     return invokeChecked(inst, args);
     * } catch (Throwable e) {
     *     throw new InvocationException(e);
     * }
     * }</pre>
     *
     * @param inst the instance
     * @param args the invocation arguments
     * @return the invocation result
     * @throws InvocationException for any {@link Throwable}
     */
    default @Nullable Object invoke(
        @Nullable Object inst, @Nullable Object @Nonnull ... args
    ) throws InvocationException {
        try {
            return invokeChecked(inst, args);
        } catch (Throwable e) {
            throw new InvocationException(e);
        }
    }

    /**
     * Executes this invocable entity with an instance and the invocation arguments. The instance typically represents
     * the owner object of a method or the scope of '{@code this}', and the arguments typically represent the actual
     * arguments passed to the method or function. The instance can be {@code null} if this entity is a static method,
     * constructor, or other similar entity.
     * <p>
     * This method directly throws any exception thrown from the underlying invocation.
     *
     * @param inst the instance
     * @param args the invocation arguments
     * @return the invocation result
     * @throws Throwable directly throws any exception thrown from the underlying invocation
     */
    @Nullable
    Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable;
}
