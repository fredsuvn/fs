package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;

/**
 * Static utilities class for {@link MethodHandle}.
 *
 * @author sunqian
 */
public class JieHandle {

    /**
     * Invokes the {@link MethodHandle} as an instance method, with the specified instance and invocation arguments.
     *
     * @param handle the {@link MethodHandle} to be invoked
     * @param inst   the specified instance
     * @param args   the invocation arguments
     * @return result of invocation
     * @throws Throwable for any error thrown by the specified method handle
     */
    public static @Nullable Object invokeInstance(MethodHandle handle, Object inst, Object... args) throws Throwable {
        return HandleBack.invokeInstance(handle, inst, args);
    }

    /**
     * Invokes the {@link MethodHandle} as a static method or constructor, with the invocation arguments.
     *
     * @param handle the {@link MethodHandle} to be invoked
     * @param args   the invocation arguments
     * @return result of invocation
     * @throws Throwable for any error thrown by the specified method handle
     */
    public static @Nullable Object invokeStatic(MethodHandle handle, Object... args) throws Throwable {
        return HandleBack.invokeStatic(handle, args);
    }
}
