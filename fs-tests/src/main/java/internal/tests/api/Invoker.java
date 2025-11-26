package internal.tests.api;

import space.sunqian.common.runtime.invoke.Invocable;
import space.sunqian.common.runtime.invoke.InvocationMode;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class Invoker {

    private static final Method INVOKE_STATIC;
    private static final Method INVOKE_INSTANCE;

    static {
        try {
            INVOKE_STATIC = Invoker.class.getDeclaredMethod("invokeStatic", Integer.class, Long.class);
            INVOKE_INSTANCE = Invoker.class.getDeclaredMethod("invokeInstance", Integer.class, Long.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Invocable createInvocable(String invokeType, String methodType) {
        if ("static".equals(methodType)) {
            if ("reflect".equals(invokeType)) {
                return Invocable.of(INVOKE_STATIC, InvocationMode.REFLECTION);
            } else if ("asm".equals(invokeType)) {
                return Invocable.of(INVOKE_STATIC, InvocationMode.ASM);
            } else if ("method_handle".equals(invokeType)) {
                return Invocable.of(INVOKE_STATIC, InvocationMode.METHOD_HANDLE);
            }
        } else {
            if ("reflect".equals(invokeType)) {
                return Invocable.of(INVOKE_INSTANCE, InvocationMode.REFLECTION);
            } else if ("asm".equals(invokeType)) {
                return Invocable.of(INVOKE_INSTANCE, InvocationMode.ASM);
            } else if ("method_handle".equals(invokeType)) {
                return Invocable.of(INVOKE_INSTANCE, InvocationMode.METHOD_HANDLE);
            }
        }
        return null;
    }

    public static Supplier<Object> createAction(String invokeType, String methodType) {
        Invocable invocable = createInvocable(invokeType, methodType);
        Invoker invoker = "static".equals(methodType) ? null : new Invoker();
        if (invocable != null) {
            return () -> {
                Random random = ThreadLocalRandom.current();
                return invocable.invoke(invoker, random.nextInt(), random.nextLong());
            };
        }
        return "static".equals(methodType) ?
            () -> {
                Random random = ThreadLocalRandom.current();
                return invokeStatic(random.nextInt(), random.nextLong());
            }
            :
            () -> {
                Random random = ThreadLocalRandom.current();
                return invoker.invokeInstance(random.nextInt(), random.nextLong());
            };
    }

    public static String invokeStatic(Integer i, Long l) {
        return i.toString() + l.toString();
    }

    public String invokeInstance(Integer i, Long l) {
        return i.toString() + l.toString();
    }
}
