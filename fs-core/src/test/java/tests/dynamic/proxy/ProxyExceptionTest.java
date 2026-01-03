package tests.dynamic.proxy;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.dynamic.proxy.ProxyException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProxyExceptionTest {

    @Test
    public void testProxyException() {
        {
            // ProxyException
            assertThrows(ProxyException.class, () -> {
                throw new ProxyException();
            });
            assertThrows(ProxyException.class, () -> {
                throw new ProxyException("");
            });
            assertThrows(ProxyException.class, () -> {
                throw new ProxyException("", new RuntimeException());
            });
            assertThrows(ProxyException.class, () -> {
                throw new ProxyException(new RuntimeException());
            });
        }
    }
}
