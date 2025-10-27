package tests.runtime.aspect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.runtime.aspect.AspectException;

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
