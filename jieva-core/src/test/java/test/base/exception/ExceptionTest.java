package test.base.exception;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.exception.ExceptionKit;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.base.exception.JieRuntimeException;
import xyz.sunqian.common.base.exception.UnknownArrayTypeException;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import xyz.sunqian.common.base.exception.UnknownTypeException;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.exception.WrappedException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ExceptionTest {

    @Test
    public void testException() {
        assertNull(ExceptionKit.getMessage(null));
        assertEquals(ExceptionKit.getMessage(new RuntimeException("hello")), "hello");
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

        // UnknownTypeException
        expectThrows(UnknownTypeException.class, () -> {
            throw new UnknownTypeException(Object.class);
        });
        // UnknownArrayTypeException
        expectThrows(UnknownArrayTypeException.class, () -> {
            throw new UnknownArrayTypeException(Object.class);
        });
        // UnknownPrimitiveTypeException
        expectThrows(UnknownPrimitiveTypeException.class, () -> {
            throw new UnknownPrimitiveTypeException(Object.class);
        });

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
        {
            // WrappedException
            assertEquals(new WrappedException(cause).getMessage(), message);
            assertSame(new WrappedException(cause).getCause(), cause);
        }
    }
}
