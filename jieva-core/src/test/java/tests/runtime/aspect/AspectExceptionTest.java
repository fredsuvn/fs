package tests.runtime.aspect;

import org.testng.annotations.Test;
import xyz.sunqian.common.runtime.aspect.AspectException;

import static org.testng.Assert.expectThrows;

public class AspectExceptionTest {

    @Test
    public void testProxyException() {
        {
            // AspectException
            expectThrows(AspectException.class, () -> {
                throw new AspectException();
            });
            expectThrows(AspectException.class, () -> {
                throw new AspectException("");
            });
            expectThrows(AspectException.class, () -> {
                throw new AspectException("", new RuntimeException());
            });
            expectThrows(AspectException.class, () -> {
                throw new AspectException(new RuntimeException());
            });
        }
    }
}
