package space.sunqian.fs.invoke;

import space.sunqian.annotation.CachedResult;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.value.SimpleKey;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This interface represents an invocable entity, such as a method, constructor, function, executable task, executable
 * scope or other similar entity.
 * <p>
 * This interface provides two equivalent methods: {@link #invoke(Object, Object...)} which declares an unchecked
 * exception, and {@link #invokeDirectly(Object, Object...)} which declares a checked exception.
 * <p>
 * The thread-safety of this interface depends on invocable entity it holds.
 *
 * @author sunqian
 */
public interface Invocable {

    /**
     * Returns a singleton {@link Invocable} which has no effect and always returns {@code null}.
     *
     * @return a singleton {@link Invocable} which has no effect and always returns {@code null}
     */
    static @Nonnull Invocable returnNull() {
        return InvocableBack.ReturnNull.INST;
    }

    /**
     * Returns an instance of {@link Invocable} represents the specified method, using recommended implementation in
     * current environment.
     *
     * @param method the specified method
     * @return an instance of {@link Invocable} represents the specified method
     */
    @CachedResult
    static @Nonnull Invocable of(@Nonnull Method method) {
        return of(method, InvocationMode.recommended(method));
    }

    /**
     * Returns an instance of {@link Invocable} represents the specified method, using the specified implementation.
     *
     * @param method the specified method
     * @param mode   the specified implementation
     * @return an instance of {@link Invocable} represents the specified method
     */
    @CachedResult
    static @Nonnull Invocable of(@Nonnull Method method, @Nonnull InvocationMode mode) {
        SimpleKey key = SimpleKey.of(method, mode);
        return InvocableBack.Cache.get(key, k -> {
            SimpleKey sk = (SimpleKey) k;
            Method m = sk.getAs(0);
            InvocationMode im = sk.getAs(1);
            return newInvocable(m, im);
        });
    }

    /**
     * Returns an instance of {@link Invocable} represents the specified constructor, using recommended implementation
     * in current environment.
     *
     * @param constructor the specified constructor
     * @return an instance of {@link Invocable} represents the specified constructor
     */
    @CachedResult
    static @Nonnull Invocable of(@Nonnull Constructor<?> constructor) {
        return of(constructor, InvocationMode.recommended(constructor));
    }

    /**
     * Returns an instance of {@link Invocable} represents the specified constructor, using the specified
     * implementation.
     *
     * @param constructor the specified constructor
     * @param mode        the specified implementation
     * @return an instance of {@link Invocable} represents the specified constructor
     */
    @CachedResult
    static @Nonnull Invocable of(@Nonnull Constructor<?> constructor, @Nonnull InvocationMode mode) {
        SimpleKey key = SimpleKey.of(constructor, mode);
        return InvocableBack.Cache.get(key, k -> {
            SimpleKey sk = (SimpleKey) k;
            Constructor<?> c = sk.getAs(0);
            InvocationMode im = sk.getAs(1);
            return newInvocable(c, im);
        });
    }

    /**
     * Returns an instance of {@link Invocable} represents the specified method handle.
     *
     * @param handle   the specified method handle
     * @param isStatic to specify whether the method handle is for a static method/constructor
     * @return an instance of {@link Invocable} represents the specified method handle
     */
    @CachedResult
    static @Nonnull Invocable of(@Nonnull MethodHandle handle, boolean isStatic) {
        SimpleKey key = SimpleKey.of(handle, isStatic);
        return InvocableBack.Cache.get(key, k -> {
            SimpleKey sk = (SimpleKey) k;
            MethodHandle mh = sk.getAs(0);
            boolean is = sk.getAs(1);
            return newInvocable(mh, is);
        });
    }

    /**
     * Returns a new {@link Invocable} represents the specified method handle.
     *
     * @param handle   the specified method handle
     * @param isStatic to specify whether the method handle is for a static method/constructor
     * @return a new {@link Invocable} represents the specified method handle
     */
    static @Nonnull Invocable newInvocable(@Nonnull MethodHandle handle, boolean isStatic) {
        return ByMethodHandle.newInvocable(handle, isStatic);
    }

    /**
     * Returns a new {@link Invocable} represents the specified constructor, using the specified implementation.
     *
     * @param constructor the specified constructor
     * @param mode        the specified implementation
     * @return a new {@link Invocable} represents the specified constructor
     */
    @SuppressWarnings("EnhancedSwitchMigration")
    static @Nonnull Invocable newInvocable(@Nonnull Constructor<?> constructor, @Nonnull InvocationMode mode) {
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
     * Returns a new {@link Invocable} represents the specified method, using the specified implementation.
     *
     * @param method the specified method
     * @param mode   the specified implementation
     * @return a new {@link Invocable} represents the specified method
     */
    @SuppressWarnings("EnhancedSwitchMigration")
    static @Nonnull Invocable newInvocable(@Nonnull Method method, @Nonnull InvocationMode mode) {
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
    default Object invoke(
        @Nullable Object inst, Object @Nonnull ... args
    ) throws InvocationException {
        try {
            return invokeDirectly(inst, args);
        } catch (Throwable e) {
            throw new InvocationException(e);
        }
    }

    /**
     * Executes this invocable entity with an instance and the invocation arguments, and directly throws any exception
     * thrown from the underlying invocation (if any). The instance typically represents the owner object of a method or
     * the scope of '{@code this}', and the arguments typically represent the actual arguments passed to the method or
     * function. The instance can be {@code null} if this entity is a static method, constructor, or other similar
     * entity.
     *
     * @param inst the instance
     * @param args the invocation arguments
     * @return the invocation result
     * @throws Throwable the exception directly thrown from the underlying invocation
     */
    Object invokeDirectly(@Nullable Object inst, Object @Nonnull ... args) throws Throwable;
}
