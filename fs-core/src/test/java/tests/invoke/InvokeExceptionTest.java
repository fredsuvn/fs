package tests.invoke;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.invoke.InvocationException;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvokeExceptionTest {

    @Test
    public void testExceptionConstructors() {

        String message = "hello";
        Throwable cause = new RuntimeException(message);
        InvocationTargetException targetException = new InvocationTargetException(cause);
        {
            // InvocationException
            assertThrows(InvocationException.class, () -> {
                throw new InvocationException("");
            });
            assertThrows(InvocationException.class, () -> {
                throw new InvocationException(cause);
            });
            assertThrows(InvocationException.class, () -> {
                throw new InvocationException(targetException);
            });
            InvocationException e = new InvocationException(targetException);
            assertSame(e.getCause(), cause);
        }
    }
}
