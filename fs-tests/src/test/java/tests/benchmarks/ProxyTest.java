package tests.benchmarks;

import internal.tests.common.ProxyApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyTest {

    @Test
    public void testProxy() throws Exception {
        testProxy("jdk");
        testProxy("asm");
        testProxy("direct");
    }

    public void testProxy(String proxyType) throws Exception {
        assertEquals(
            "3hello[proxy]",
            ProxyApi.createProxy(proxyType).withPrimitive(1, 2, "hello")
        );
        assertEquals(
            "12hello[proxy]",
            ProxyApi.createProxy(proxyType).withoutPrimitive(1, 2L, "hello")
        );
    }
}
