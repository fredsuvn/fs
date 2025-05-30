package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.proxy.ProxyException;
import xyz.sunqian.common.reflect.proxy.asm.AsmProxyException;
import xyz.sunqian.common.reflect.proxy.jdk.JdkProxyException;

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
            expectThrows(AsmProxyException.class, () -> {
                throw new AsmProxyException();
            });
            expectThrows(AsmProxyException.class, () -> {
                throw new AsmProxyException("");
            });
            expectThrows(AsmProxyException.class, () -> {
                throw new AsmProxyException("", new RuntimeException());
            });
            AsmProxyException e = new AsmProxyException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }

        {
            // JdkProxyException
            expectThrows(JdkProxyException.class, () -> {
                throw new JdkProxyException();
            });
            expectThrows(JdkProxyException.class, () -> {
                throw new JdkProxyException("");
            });
            expectThrows(JdkProxyException.class, () -> {
                throw new JdkProxyException("", new RuntimeException());
            });
            JdkProxyException e = new JdkProxyException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }
    }
}
