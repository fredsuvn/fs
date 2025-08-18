package test.runtime.proxy;

import xyz.sunqian.common.runtime.invoke.Invocable;
import xyz.sunqian.common.runtime.proxy.JdkProxyMaker;

import java.lang.reflect.Field;

import static org.testng.Assert.expectThrows;

final class Proxy8Test {

    static void testUnsupportedDefaultMethod() throws Exception {
        Class<?> cls = Class.forName("xyz.sunqian.common.runtime.proxy.Proxy8");
        Field field = cls.getDeclaredField("UNSUPPORTED_DEFAULT_METHOD_INVOCABLE");
        field.setAccessible(true);
        Invocable invocable = (Invocable) field.get(null);
        expectThrows(JdkProxyMaker.JdkProxyException.class, () -> invocable.invokeChecked(null));
    }
}
