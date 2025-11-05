package tests.runtime.proxy;

import space.sunqian.common.runtime.invoke.Invocable;
import space.sunqian.common.runtime.proxy.jdk.JdkProxyMaker;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class ProxyBackTest {

    static void testUnsupportedDefaultMethod() throws Exception {
        Class<?> cls = Class.forName("space.sunqian.common.runtime.proxy.jdk.ProxyBack");
        Field field = cls.getDeclaredField("UNSUPPORTED_DEFAULT_METHOD_INVOCABLE");
        field.setAccessible(true);
        Invocable invocable = (Invocable) field.get(null);
        assertThrows(JdkProxyMaker.JdkProxyException.class, () -> invocable.invokeChecked(null));
    }
}
