package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.proxy.AsmProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.JdkProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyException;

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
        {
            // AsmProxyException
            expectThrows(AsmProxyClassGenerator.AsmProxyException.class, () -> {
                throw new AsmProxyClassGenerator.AsmProxyException(new RuntimeException());
            });
        }
        {
            // JdkProxyException
            expectThrows(JdkProxyClassGenerator.JdkProxyException.class, () -> {
                throw new JdkProxyClassGenerator.JdkProxyException(new RuntimeException());
            });
        }
    }
}
