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
 * <p>
 * The thread-safety of this interface depends on invocable entity it holds.
 *
 * @author sunqian
 */
public interface Invocable {

    /**
     * Returns a new {@link Invocable} represents the specified method, using recommended implementation in current
     * environment.
     *
     * @param method the specified method
     * @return a new {@link Invocable} represents the specified method
     */
    static @Nonnull Invocable of(@Nonnull Method method) {
        return of(method, InvocationMode.recommended(method));
    }

    /**
     * Returns a new {@link Invocable} represents the specified constructor, using recommended implementation in current
     * environment.
     *
     * @param constructor the specified constructor
     * @return a new {@link Invocable} represents the specified constructor
     */
    static @Nonnull Invocable of(@Nonnull Constructor<?> constructor) {
        return of(constructor, InvocationMode.recommended(constructor));
    }

    /**
     * Returns a new {@link Invocable} represents the specified method handle.
     *
     * @param handle   the specified method handle
     * @param isStatic specifies whether the method handle is for a static method, constructor, or similar
     * @return a new {@link Invocable} represents the specified method handle
     */
    static @Nonnull Invocable of(@Nonnull MethodHandle handle, boolean isStatic) {
        return ByMethodHandle.newInvocable(handle, isStatic);
    }

    /**
     * Returns a new {@link Invocable} represents the specified method, using specified implementation.
     *
     * @param method the specified method
     * @param mode   specifies the implementation of the returned instance
     * @return a new {@link Invocable} represents the specified method
     */
    static @Nonnull Invocable of(@Nonnull Method method, @Nonnull InvocationMode mode) {
        switch (mode) {
            case METHOD_HANDLE:
                return ByMethodHandle.newInvocable(method);
            case ASM:
                return ByAsm.newInvocable(method);
            default:
                return ByReflection.newInvocable(method);
        }
    }

    /**
     * Returns a new {@link Invocable} represents the specified constructor, using specified implementation.
     *
     * @param constructor the specified constructor
     * @param mode        specifies the implementation of the returned instance
     * @return a new {@link Invocable} represents the specified constructor
     */
    static @Nonnull Invocable of(@Nonnull Constructor<?> constructor, @Nonnull InvocationMode mode) {
        switch (mode) {
            case METHOD_HANDLE:
                return ByMethodHandle.newInvocable(constructor);
            case ASM:
                return ByAsm.newInvocable(constructor);
            default:
                return ByReflection.newInvocable(constructor);
        }
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
