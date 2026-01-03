package internal.tests.api;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.dynamic.aop.AspectHandler;
import space.sunqian.fs.dynamic.aop.AspectMaker;

import java.lang.reflect.Method;

public class AspectApi {

    public static AspectApi createAspect(String aspectType) throws Exception {
        return switch (aspectType) {
            case "fs-asm" -> AspectMaker.byAsm().make(
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
            // case "byte-buddy" -> new ByteBuddy()
            //     .subclass(AspectApi.class)
            //     .method(ElementMatchers.named("withPrimitive"))
            //     .intercept(Advice.to(ByteBuddyAdvice1.class))
            //     .method(ElementMatchers.named("withoutPrimitive"))
            //     .intercept(Advice.to(ByteBuddyAdvice2.class))
            //     .make()
            //     .load(ProxyApi.class.getClassLoader())
            //     .getLoaded()
            //     .newInstance();
            case "direct" -> new AspectApi() {
                @Override
                public String withPrimitive(int i, long l, String str) throws Exception {
                    return super.withPrimitive(i + 1, l, str);
                }

                @Override
                public String withoutPrimitive(Integer i, Long l, String str) throws Exception {
                    return super.withoutPrimitive(i + 1, l, str);
                }
            };
            default -> throw new IllegalArgumentException();
        };
    }

    public String withPrimitive(int i, long l, String str) throws Exception {
        return (i + l) + str;
    }

    public String withoutPrimitive(Integer i, Long l, String str) throws Exception {
        return i.toString() + l.toString() + str;
    }

    // static class ByteBuddyAdvice1 {
    //
    //     @Advice.OnMethodEnter
    //     public static void before(
    //         @Advice.Argument(value = 0, readOnly = false) int i,
    //         @Advice.Argument(value = 1, readOnly = false) long l
    //     ) {
    //         i = i + 1;
    //     }
    //
    //     @Advice.OnMethodExit
    //     public static void after(
    //         @Advice.Argument(value = 0, readOnly = false) int i,
    //         @Advice.Argument(value = 1, readOnly = false) long l
    //     ) {
    //     }
    // }
    //
    // static class ByteBuddyAdvice2 {
    //
    //     @Advice.OnMethodEnter
    //     public static void before(
    //         @Advice.Argument(value = 0, readOnly = false) Integer i,
    //         @Advice.Argument(value = 1, readOnly = false) Long l
    //     ) {
    //         i = i + 1;
    //     }
    //
    //     @Advice.OnMethodExit
    //     public static void after(
    //         @Advice.Argument(value = 0, readOnly = false) Integer i,
    //         @Advice.Argument(value = 1, readOnly = false) Long l
    //     ) {
    //     }
    // }
}
