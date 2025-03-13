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
     * Invokes given {@link MethodHandle} as instance method with specified instance and arguments.
     *
     * @param handle given handle
     * @param inst   specified instance
     * @param args   specified arguments
     * @return result of invocation
     * @throws Throwable anything thrown by the target method invocation
     */
    public static @Nullable Object invokeInstance(MethodHandle handle, Object inst, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return handle.invoke(inst);
            case 1:
                return handle.invoke(inst, args[0]);
            case 2:
                return handle.invoke(inst, args[0], args[1]);
            case 3:
                return handle.invoke(inst, args[0], args[1], args[2]);
            case 4:
                return handle.invoke(inst, args[0], args[1], args[2], args[3]);
            case 5:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                return handle.invokeWithArguments(copyArgs(inst, args));
        }
    }

    private static Object[] copyArgs(Object inst, Object... args) {
        Object[] ret = new Object[args.length + 1];
        ret[0] = inst;
        System.arraycopy(args, 0, ret, 1, args.length);
        return ret;
    }

    /**
     * Invokes given {@link MethodHandle} as static method with specified arguments.
     *
     * @param handle given handle
     * @param args   specified arguments
     * @return result of invocation
     * @throws Throwable anything thrown by the target method invocation
     */
    public static @Nullable Object invokeStatic(MethodHandle handle, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return handle.invoke();
            case 1:
                return handle.invoke(args[0]);
            case 2:
                return handle.invoke(args[0], args[1]);
            case 3:
                return handle.invoke(args[0], args[1], args[2]);
            case 4:
                return handle.invoke(args[0], args[1], args[2], args[3]);
            case 5:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                return handle.invokeWithArguments(args);
        }
    }
}
