package tests.core.dynamic.proxy;

import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.dynamic.proxy.ProxyHandler;
import space.sunqian.fs.dynamic.proxy.ProxyInvoker;
import space.sunqian.fs.dynamic.proxy.ProxyMaker;
import space.sunqian.fs.dynamic.proxy.ProxySpec;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProxyTest {

    @Test
    public void testProxy() throws Exception {
        testProxyWithMaker(ProxyMaker.byAsm());
        testProxyWithMaker(ProxyMaker.byJdk());
    }

    private void testProxyWithMaker(ProxyMaker maker) throws Exception {
        String result = "ssssssss";
        ProxyHandler handler = createProxyHandler(result);
        ProxySpec pc = maker.make(null, Fs.list(InterA.class), handler);
        InterA pa = pc.newInstance();
        testProxyBehavior(pa, result);
    }

    private ProxyHandler createProxyHandler(String result) {
        return new ProxyHandler() {
            @Override
            public boolean needsProxy(@Nonnull Method method) {
                return !method.getName().equals("a1");
            }

            @Override
            public @Nonnull Object invoke(
                @Nonnull Object proxy,
                @Nonnull Method method,
                @Nonnull ProxyInvoker invoker,
                @Nullable Object @Nonnull ... args
            ) throws Throwable {
                return result;
            }
        };
    }

    private void testProxyBehavior(InterA pa, String result) {
        assertThrows(AbstractMethodError.class, pa::a1);
        assertSame(pa.a2(), result);
    }

    public interface InterA {

        String a1();

        default String a2() {
            return "a2";
        }
    }
}
