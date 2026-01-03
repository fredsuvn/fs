package internal.tests.api;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.dynamic.proxy.ProxyHandler;
import space.sunqian.fs.dynamic.proxy.ProxyInvoker;
import space.sunqian.fs.dynamic.proxy.ProxyMaker;

import java.lang.reflect.Method;
import java.util.List;

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
            // case "byte-buddy" -> new ByteBuddy()
            //     .subclass(Object.class).implement(ProxyApi.class)
            //     .method(ElementMatchers.named("withPrimitive")
            //         .or(ElementMatchers.named("withoutPrimitive")))
            //     .intercept(MethodDelegation.to(new ByteBuddyInterceptor()))
            //     .make()
            //     .load(ProxyApi.class.getClassLoader())
            //     .getLoaded()
            //     .newInstance();
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

    // class ByteBuddyInterceptor {
    //
    //     @RuntimeType
    //     public Object intercept(
    //         //@Origin Method method,
    //         //@AllArguments Object[] args,
    //         //@This Object proxyObj,
    //         @SuperCall Callable<?> callable
    //     ) throws Exception {
    //         return callable.call() + "[proxy]";
    //     }
    // }
    //
    // class CglibInterceptor implements MethodInterceptor {
    //
    //     @Override
    //     public Object intercept(
    //         Object obj,
    //         Method method,
    //         Object[] args,
    //         MethodProxy proxy
    //     ) throws Throwable {
    //         return proxy.invokeSuper(obj, args) + "[proxy]";
    //     }
    // }
    //
    // class ByteBuddyCallInterceptor {
    //
    //     @RuntimeType
    //     public Object intercept(
    //         @Origin Method method,
    //         @AllArguments Object[] args,
    //         @This Object proxyObj
    //         //@SuperCall Callable<?> callable
    //     ) throws Exception {
    //         return MethodCall.invoke(method)
    //             .on(proxyObj)
    //             .with(args) + "[proxy]";
    //     }
    // }
}
