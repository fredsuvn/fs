package internal.tests.common;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;

import java.lang.reflect.Method;
import java.util.List;

public interface ProxyApi {

    static ProxyApi createProxy(String proxyType) {
        return switch (proxyType) {
            case "asm" -> ProxyMaker.byAsm().make(
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
            case "jdk" -> ProxyMaker.byJdk().make(
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
            case "original" -> new ProxyApi() {
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
}
