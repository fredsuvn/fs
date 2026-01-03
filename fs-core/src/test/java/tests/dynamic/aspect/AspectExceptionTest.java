package tests.dynamic.aspect;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.dynamic.aop.AspectException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AspectExceptionTest {

    @Test
    public void testProxyException() {
        {
            // AspectException
            assertThrows(AspectException.class, () -> {
                throw new AspectException();
            });
            assertThrows(AspectException.class, () -> {
                throw new AspectException("");
            });
            assertThrows(AspectException.class, () -> {
                throw new AspectException("", new RuntimeException());
            });
            assertThrows(AspectException.class, () -> {
                throw new AspectException(new RuntimeException());
            });
        }
    }
}
