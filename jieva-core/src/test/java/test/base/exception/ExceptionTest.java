package test.base.exception;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.base.exception.JieRuntimeException;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.exception.UnsafeException;

import java.io.IOException;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class ExceptionTest {

    @Test
    public void testUtilities() {
        RuntimeException exception = new RuntimeException();
        assertSame(
            JieException.getCauseIfTypeMatches(new IOException(exception), IOException.class),
            exception
        );
        assertNotEquals(
            JieException.getCauseIfTypeMatches(new IOException(exception), NullPointerException.class),
            exception
        );
    }

    @Test
    public void testExceptionConstructors() {
        // JieRuntimeException
        expectThrows(JieRuntimeException.class, () -> {
            throw new JieRuntimeException();
        });
        expectThrows(JieRuntimeException.class, () -> {
            throw new JieRuntimeException("");
        });
        expectThrows(JieRuntimeException.class, () -> {
            throw new JieRuntimeException("", new RuntimeException());
        });
        expectThrows(JieRuntimeException.class, () -> {
            throw new JieRuntimeException(new RuntimeException());
        });

        // UnsafeException
        expectThrows(UnsafeException.class, () -> {
            throw new UnsafeException();
        });
        expectThrows(UnsafeException.class, () -> {
            throw new UnsafeException("");
        });
        expectThrows(UnsafeException.class, () -> {
            throw new UnsafeException("", new RuntimeException());
        });
        expectThrows(UnsafeException.class, () -> {
            throw new UnsafeException(new RuntimeException());
        });

        // UnreachablePointException
        expectThrows(UnreachablePointException.class, () -> {
            throw new UnreachablePointException();
        });
        expectThrows(UnreachablePointException.class, () -> {
            throw new UnreachablePointException("");
        });
        expectThrows(UnreachablePointException.class, () -> {
            throw new UnreachablePointException("", new RuntimeException());
        });
        expectThrows(UnreachablePointException.class, () -> {
            throw new UnreachablePointException(new RuntimeException());
        });

        // ProcessingException
        expectThrows(ProcessingException.class, () -> {
            throw new ProcessingException(new RuntimeException());
        });
    }
}
