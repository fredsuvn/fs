package tests.runtime.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.common.runtime.proxy.ProxyException;

import static org.testng.Assert.expectThrows;

public class ProxyExceptionTest {

    @Test
    public void testProxyException() {
        {
            // ProxyException
            expectThrows(ProxyException.class, () -> {
                throw new ProxyException();
            });
            expectThrows(ProxyException.class, () -> {
                throw new ProxyException("");
            });
            expectThrows(ProxyException.class, () -> {
                throw new ProxyException("", new RuntimeException());
            });
            expectThrows(ProxyException.class, () -> {
                throw new ProxyException(new RuntimeException());
            });
        }
    }
}
