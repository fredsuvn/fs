package tests.benchmarks;

import internal.api.ProxyApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyTest {

    @Test
    public void testProxyWithDifferentImplementations() throws Exception {
        testProxyImplementation("fs-asm");
        testProxyImplementation("fs-jdk");
        // testProxyImplementation("byte-buddy");
        // testProxyImplementation("cglib");
        testProxyImplementation("direct");
    }

    private void testProxyImplementation(String proxyType) throws Exception {
        ProxyApi api = ProxyApi.createApi(proxyType);

        // Test with primitive parameters
        assertEquals("3hello[proxy]", api.withPrimitive(1, 2, "hello"));

        // Test with non-primitive parameters
        assertEquals("12hello[proxy]", api.withoutPrimitive(1, 2L, "hello"));
    }
}
