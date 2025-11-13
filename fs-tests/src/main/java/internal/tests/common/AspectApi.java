package internal.tests.common;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.runtime.aspect.AspectHandler;
import space.sunqian.common.runtime.aspect.AspectMaker;

import java.lang.reflect.Method;

public class AspectApi {

    public static AspectApi createAspect(String aspectType) {
        return switch (aspectType) {
            case "asm" -> AspectMaker.byAsm().make(
                AspectApi.class,
                new AspectHandler() {
                    @Override
                    public boolean needsAspect(@Nonnull Method method) {
                        return method.getName().startsWith("with");
                    }

                    @Override
                    public void beforeInvoking(
                        @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target
                    ) throws Throwable {
                        args[0] = (Integer) args[0] + 1;
                    }

                    @Override
                    public @Nullable Object afterReturning(
                        @Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target
                    ) throws Throwable {
                        return result;
                    }

                    @Override
                    public @Nullable Object afterThrowing(
                        @Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target
                    ) {
                        throw new RuntimeException(ex);
                    }
                }
            ).newInstance();
            case "original" -> new AspectApi() {
                @Override
                public String withPrimitive(int i, long l, String str) throws Exception {
                    return super.withPrimitive(i + 1, l, str);
                }

                @Override
                public String withoutPrimitive(Integer i, Long l, String str) throws Exception {
                    return super.withoutPrimitive(i + 1, l, str);
                }
            };
            default -> throw new IllegalArgumentException("proxyType is not support");
        };
    }

    public String withPrimitive(int i, long l, String str) throws Exception {
        return (i + l) + str;
    }

    public String withoutPrimitive(Integer i, Long l, String str) throws Exception {
        return i.toString() + l.toString() + str;
    }
}
