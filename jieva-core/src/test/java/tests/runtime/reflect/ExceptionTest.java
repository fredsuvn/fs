package tests.runtime.reflect;

import org.testng.annotations.Test;
import space.sunqian.common.runtime.reflect.ReflectionException;

import static org.testng.Assert.expectThrows;

public class ExceptionTest {

    @Test
    public void testExceptions() {
        {
            // ReflectionException
            expectThrows(ReflectionException.class, () -> {
                throw new ReflectionException();
            });
            expectThrows(ReflectionException.class, () -> {
                throw new ReflectionException("");
            });
            expectThrows(ReflectionException.class, () -> {
                throw new ReflectionException("", new RuntimeException());
            });
            expectThrows(ReflectionException.class, () -> {
                throw new ReflectionException(new RuntimeException());
            });
        }
    }
}
