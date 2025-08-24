package tests.runtime.invoke;

import org.testng.annotations.Test;
import xyz.sunqian.common.runtime.invoke.InvocationException;

import java.lang.reflect.InvocationTargetException;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class InvokeExceptionTest {

    @Test
    public void testExceptionConstructors() {

        String message = "hello";
        Throwable cause = new RuntimeException(message);
        InvocationTargetException targetException = new InvocationTargetException(cause);
        {
            // InvocationException
            expectThrows(InvocationException.class, () -> {
                throw new InvocationException("");
            });
            expectThrows(InvocationException.class, () -> {
                throw new InvocationException(cause);
            });
            expectThrows(InvocationException.class, () -> {
                throw new InvocationException(targetException);
            });
            InvocationException e = new InvocationException(targetException);
            assertSame(e.getCause(), cause);
        }
    }
}
