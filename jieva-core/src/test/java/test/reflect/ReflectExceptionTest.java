package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.JvmException;
import xyz.sunqian.common.reflect.ReflectionException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ReflectExceptionTest {

    @Test
    public void testExceptionConstructors() {

        String message = "hello";
        Throwable cause = new RuntimeException(message);

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
            ReflectionException e = new ReflectionException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
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
            JvmException e = new JvmException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }
    }
}
