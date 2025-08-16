package test.reflect.proxy;

import org.testng.annotations.Test;
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
        // {
        //     // AsmProxyException
        //     expectThrows(AsmProxyMaker.AsmProxyException.class, () -> {
        //         throw new AsmProxyMaker.AsmProxyException(new RuntimeException());
        //     });
        // }
        // {
        //     // JdkProxyException
        //     expectThrows(JdkProxyMaker.JdkProxyException.class, () -> {
        //         throw new JdkProxyMaker.JdkProxyException(new RuntimeException());
        //     });
        // }
    }
}
