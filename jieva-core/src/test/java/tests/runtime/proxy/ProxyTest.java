package tests.runtime.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.runtime.proxy.ProxyHandler;
import xyz.sunqian.common.runtime.proxy.ProxyInvoker;
import xyz.sunqian.common.runtime.proxy.ProxyMaker;
import xyz.sunqian.common.runtime.proxy.ProxySpec;

import java.lang.reflect.Method;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ProxyTest {

    @Test
    public void testProxy() throws Exception {
        testProxy(ProxyMaker.byAsm());
        testProxy(ProxyMaker.byJdk());
    }

    private void testProxy(ProxyMaker maker) throws Exception {
        String result = "ssssssss";
        ProxySpec pc = maker.make(null, Jie.list(InterA.class), new ProxyHandler() {

            @Override
            public boolean shouldProxyMethod(@Nonnull Method method) {
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
        });
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
