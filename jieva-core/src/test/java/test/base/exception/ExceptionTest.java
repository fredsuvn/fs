package test.base.exception;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.base.exception.JieRuntimeException;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.base.exception.UnreachablePointException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ExceptionTest {

    @Test
    public void testException() {
        assertNull(JieException.getMessage(null));
        assertEquals(JieException.getMessage(new RuntimeException("hello")), "hello");
    }

    @Test
    public void testExceptionConstructors() {

        String message = "hello";
        Throwable cause = new RuntimeException(message);

        // JieException
        expectThrows(JieException.class, () -> {
            throw new JieException();
        });
        expectThrows(JieException.class, () -> {
            throw new JieException("");
        });
        expectThrows(JieException.class, () -> {
            throw new JieException("", new RuntimeException());
        });
        expectThrows(JieException.class, () -> {
            throw new JieException(new RuntimeException());
        });
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

        {
            // ProcessingException
            expectThrows(ProcessingException.class, () -> {
                throw new ProcessingException();
            });
            expectThrows(ProcessingException.class, () -> {
                throw new ProcessingException("");
            });
            expectThrows(ProcessingException.class, () -> {
                throw new ProcessingException("", new RuntimeException());
            });
            ProcessingException e = new ProcessingException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
        }
        {
            // AwaitingException
            expectThrows(AwaitingException.class, () -> {
                throw new AwaitingException();
            });
            expectThrows(AwaitingException.class, () -> {
                throw new AwaitingException("");
            });
            expectThrows(AwaitingException.class, () -> {
                throw new AwaitingException("", new RuntimeException());
            });
            AwaitingException e = new AwaitingException(cause);
            assertEquals(e.getMessage(), message);
            assertSame(e.getCause(), cause);
            assertTrue(new AwaitingException(new InterruptedException()).isInterrupted());
            assertFalse(new AwaitingException().isInterrupted());
        }
    }
}
