package internal.tests.api;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.runtime.aspect.AspectHandler;
import space.sunqian.common.runtime.aspect.AspectMaker;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class AspectApi {

    public static AspectApi createAspect(String aspectType) throws Exception {
        return (AspectApi) switch (aspectType) {
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
            //     .method(ElementMatchers.named("withPrimitive")
            //         .or(ElementMatchers.named("withoutPrimitive")))
            //     .intercept(MethodDelegation.to(new ByteBuddyInterceptor()))
            //     .make()
            //     .load(ProxyApi.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
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

    public static class ByteBuddyInterceptor {

        @RuntimeType
        public Object intercept(
            //@Origin Method method,
            @AllArguments Object[] args,
            //@This Object proxyObj,
            @SuperCall Callable<?> callable
        ) throws Exception {
            args[0] = (Integer) args[0] + 1;
            return callable.call();
        }
    }
}
