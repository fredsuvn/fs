package xyz.sunqian.common.runtime.invoke;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;

/**
 * Utilities kit for {@link Invocable}, {@link MethodHandle} and other invocation related.
 *
 * @author sunqian
 */
public class InvokeKit {

    /**
     * Invokes the {@link MethodHandle} as an instance method, with the specified instance and invocation arguments.
     *
     * @param handle the {@link MethodHandle} to be invoked
     * @param inst   the specified instance
     * @param args   the invocation arguments
     * @return the invocation result
     * @throws Throwable for anything thrown by the underlying method
     */
    public static @Nullable Object invokeInstance(
        @Nonnull MethodHandle handle, Object inst, @Nullable Object... args
    ) throws Throwable {
        return HandleInvoker.invokeInstance(handle, inst, args);
    }

    /**
     * Invokes the {@link MethodHandle} as a static method or constructor, with the invocation arguments.
     *
     * @param handle the {@link MethodHandle} to be invoked
     * @param args   the invocation arguments
     * @return the invocation result
     * @throws Throwable for anything thrown by the underlying method
     */
    public static @Nullable Object invokeStatic(
        @Nonnull MethodHandle handle, @Nullable Object... args
    ) throws Throwable {
        return HandleInvoker.invokeStatic(handle, args);
    }

    static @Nullable Object @Nonnull [] toInstanceArgs(
        @Nullable Object inst, @Nullable Object @Nonnull ... args
    ) {
        Object[] instanceArgs = new Object[args.length + 1];
        instanceArgs[0] = inst;
        System.arraycopy(args, 0, instanceArgs, 1, args.length);
        return instanceArgs;
    }
}
