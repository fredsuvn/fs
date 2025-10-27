package tests.runtime.proxy;

import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;
import space.sunqian.common.runtime.proxy.ProxySpec;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProxyTest {

    @Test
    public void testProxy() throws Exception {
        testProxy(ProxyMaker.byAsm());
        testProxy(ProxyMaker.byJdk());
    }

    private void testProxy(ProxyMaker maker) throws Exception {
        String result = "ssssssss";
        ProxySpec pc = maker.make(null, Kit.list(InterA.class), new ProxyHandler() {

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
        });
        InterA pa = pc.newInstance();
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
