package tests.core.invoke;

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

        testEmptyMessageConstructor();
        testCauseConstructor(cause);
        testInvocationTargetExceptionConstructor(targetException, cause);
    }

    private void testEmptyMessageConstructor() {
        assertThrows(InvocationException.class, () -> {
            throw new InvocationException("");
        });
    }

    private void testCauseConstructor(Throwable cause) {
        assertThrows(InvocationException.class, () -> {
            throw new InvocationException(cause);
        });
    }

    private void testInvocationTargetExceptionConstructor(InvocationTargetException targetException, Throwable expectedCause) {
        assertThrows(InvocationException.class, () -> {
            throw new InvocationException(targetException);
        });

        InvocationException exception = new InvocationException(targetException);
        assertSame(exception.getCause(), expectedCause);
    }
}
