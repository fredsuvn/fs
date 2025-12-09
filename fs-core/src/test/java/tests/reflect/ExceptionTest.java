package tests.reflect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.reflect.ReflectionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTest {

    @Test
    public void testExceptions() {
        {
            // ReflectionException
            assertThrows(ReflectionException.class, () -> {
                throw new ReflectionException();
            });
            assertThrows(ReflectionException.class, () -> {
                throw new ReflectionException("");
            });
            assertThrows(ReflectionException.class, () -> {
                throw new ReflectionException("", new RuntimeException());
            });
            assertThrows(ReflectionException.class, () -> {
                throw new ReflectionException(new RuntimeException());
            });
        }
    }
}
