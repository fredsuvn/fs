package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.proxy.JieProxy;
import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;
import xyz.sunqian.common.reflect.proxy.ProxyMode;

import java.lang.reflect.Method;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ProxyTest {

    @Test
    public void testProxy() throws Exception {
        for (ProxyMode mode : ProxyMode.values()) {
            testProxy(mode);
        }
    }

    private void testProxy(ProxyMode mode) throws Exception {
        String result = "ssssssss";
        ProxyClass pc = JieProxy.proxy(null, Jie.list(InterA.class), new ProxyMethodHandler() {

            @Override
            public boolean requiresProxy(Method method) {
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
        }, mode);
        InterA pa = pc.newInstance();
        expectThrows(AbstractMethodError.class, pa::a1);
        assertSame(pa.a2(), result);
    }

    public interface InterA {

        String a1();

        default String a2() {
            return "a2";
        }
    }
}
