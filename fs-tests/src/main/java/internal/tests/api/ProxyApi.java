package internal.tests.api;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

public interface ProxyApi {

    static ProxyApi createProxy(String proxyType) throws Exception {
        return (ProxyApi) switch (proxyType) {
            case "fs-asm" -> ProxyMaker.byAsm().make(
                null,
                List.of(ProxyApi.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getName().startsWith("with");
                    }

                    @Override
                    public @Nonnull Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invokeSuper(proxy, args) + "[proxy]";
                    }
                }
            ).newInstance();
            case "fs-jdk" -> ProxyMaker.byJdk().make(
                null,
                List.of(ProxyApi.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getName().startsWith("with");
                    }

                    @Override
                    public @Nonnull Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invokeSuper(proxy, args) + "[proxy]";
                    }
                }
            ).newInstance();
            case "byte-buddy" -> new ByteBuddy()
                .subclass(Object.class).implement(ProxyApi.class)
                .method(ElementMatchers.named("withPrimitive")
                    .or(ElementMatchers.named("withoutPrimitive")))
                .intercept(MethodDelegation.to(new ByteBuddyInterceptor()))
                .make()
                .load(ProxyApi.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();
            // case "cglib" -> {
            //     Enhancer enhancer = new Enhancer();
            //     enhancer.setInterfaces(new Class[]{ProxyApi.class});
            //     enhancer.setCallback(new CglibInterceptor());
            //     yield enhancer.create();
            // }
            case "direct" -> new ProxyApi() {
                @Override
                public String withPrimitive(int i, long l, String str) throws Exception {
                    return ProxyApi.super.withPrimitive(i, l, str) + "[proxy]";
                }

                @Override
                public String withoutPrimitive(Integer i, Long l, String str) throws Exception {
                    return ProxyApi.super.withoutPrimitive(i, l, str) + "[proxy]";
                }
            };
            default -> throw new IllegalArgumentException();
        };
    }

    default String withPrimitive(int i, long l, String str) throws Exception {
        return (i + l) + str;
    }

    default String withoutPrimitive(Integer i, Long l, String str) throws Exception {
        return i.toString() + l.toString() + str;
    }

    class ByteBuddyInterceptor {

        @RuntimeType
        public Object intercept(
            //@Origin Method method,
            //@AllArguments Object[] args,
            //@This Object proxyObj,
            @SuperCall Callable<?> callable
        ) throws Exception {
            return callable.call() + "[proxy]";
        }
    }

    class CglibInterceptor implements MethodInterceptor {

        @Override
        public Object intercept(
            Object obj,
            Method method,
            Object[] args,
            MethodProxy proxy
        ) throws Throwable {
            return proxy.invokeSuper(obj, args) + "[proxy]";
        }
    }
}
