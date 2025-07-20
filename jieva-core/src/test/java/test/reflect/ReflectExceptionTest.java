package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.JvmException;
import xyz.sunqian.common.reflect.ReflectionException;

import static org.testng.Assert.expectThrows;

public class ReflectExceptionTest {

    @Test
    public void testExceptionConstructors() {
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

        {
            // JvmException
            expectThrows(JvmException.class, () -> {
                throw new JvmException();
            });
            expectThrows(JvmException.class, () -> {
                throw new JvmException("");
            });
            expectThrows(JvmException.class, () -> {
                throw new JvmException("", new RuntimeException());
            });
            expectThrows(JvmException.class, () -> {
                throw new JvmException(new RuntimeException());
            });
        }
    }
}
