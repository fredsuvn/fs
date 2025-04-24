package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;

final class HandleBack {

    static @Nullable Object invokeInstance(MethodHandle handle, Object inst, @Nullable Object... args) throws Throwable {
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
                return handle.invokeWithArguments(buildArgs(inst, args));
        }
    }

    private static Object[] buildArgs(Object inst, @Nullable Object... args) {
        Object[] ret = new Object[args.length + 1];
        ret[0] = inst;
        System.arraycopy(args, 0, ret, 1, args.length);
        return ret;
    }

    static @Nullable Object invokeStatic(MethodHandle handle, @Nullable Object... args) throws Throwable {
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
