package internal.samples;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;

import java.lang.reflect.Method;
import java.util.Collections;

public class ProxySample {

    public static void main(String[] args) {
        Hello hello = ProxyMaker.byAsm().make(Hello.class, Collections.emptyList(), new ProxyHandler() {

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
        System.out.println(hello.hello());
    }

    public static class Hello {

        public String hello() {
            return "hello";
        }
    }
}
