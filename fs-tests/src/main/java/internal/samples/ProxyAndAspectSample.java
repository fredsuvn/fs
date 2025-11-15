package internal.samples;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.runtime.aspect.AspectHandler;
import space.sunqian.common.runtime.aspect.AspectMaker;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;

import java.lang.reflect.Method;
import java.util.Collections;

public class ProxyAndAspectSample {

    public static void main(String[] args) {
        // proxy
        Hello proxy = ProxyMaker.byAsm().make(Hello.class, Collections.emptyList(), new ProxyHandler() {

            @Override
            public boolean needsProxy(@Nonnull Method method) {
                return method.getName().equals("hello");
            }

            @Override
            public @Nonnull Object invoke(
                @Nonnull Object proxy, @Nonnull Method method, @Nonnull ProxyInvoker invoker, Object @Nonnull ... args
            ) throws Throwable {
                return invoker.invokeSuper(proxy, args) + "[proxy]";
            }
        }).newInstance();
        System.out.println(proxy.hello());

        // aspect
        Hello aspect = AspectMaker.byAsm().make(Hello.class, new AspectHandler() {

            @Override
            public boolean needsAspect(@Nonnull Method method) {
                return method.getName().equals("hello");
            }

            @Override
            public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
            }

            @Override
            public @Nonnull Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                return result + "[aspect]";
            }

            @Override
            public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
                return null;
            }
        }).newInstance();
        System.out.println(aspect.hello());
    }

    public static class Hello {

        public String hello() {
            return "hello";
        }
    }
}
