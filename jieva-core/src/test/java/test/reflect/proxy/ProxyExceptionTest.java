package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.proxy.AsmProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyException;
import xyz.sunqian.common.reflect.proxy.JdkProxyClassGenerator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ProxyExceptionTest {

    @Test
    public void testExceptionConstructors() {

        String message = "hello";
        Throwable cause = new RuntimeException(message);

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
            ProxyException e = new ProxyException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }

        {
            // AsmProxyException
            AsmProxyClassGenerator.AsmProxyException e = new AsmProxyClassGenerator.AsmProxyException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }

        {
            // JdkProxyException
            JdkProxyClassGenerator.JdkProxyException e = new JdkProxyClassGenerator.JdkProxyException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }
    }
}
