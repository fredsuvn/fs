package tests.base.exception;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.exception.KitvaException;
import xyz.sunqian.common.base.exception.KitvaRuntimeException;
import xyz.sunqian.common.base.exception.ThrowKit;
import xyz.sunqian.common.base.exception.UnknownArrayTypeException;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import xyz.sunqian.common.base.exception.UnknownTypeException;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.exception.WrappedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ExceptionTest {

    @Test
    public void testThrowKit() {
        {
            // print stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            Exception e = new Exception();
            String traceInfo = ThrowKit.toString(e);
            e.printStackTrace(pw);
            assertEquals(sw.toString(), traceInfo);
        }
    }

    @Test
    public void testExceptionConstructors() {

        String message = "hello";
        Throwable cause = new RuntimeException(message);
        {
            // KitvaException
            expectThrows(KitvaException.class, () -> {
                throw new KitvaException();
            });
            expectThrows(KitvaException.class, () -> {
                throw new KitvaException("");
            });
            expectThrows(KitvaException.class, () -> {
                throw new KitvaException("", new RuntimeException());
            });
            expectThrows(KitvaException.class, () -> {
                throw new KitvaException(new RuntimeException());
            });
        }
        {
            // KitvaRuntimeException
            expectThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException();
            });
            expectThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException("");
            });
            expectThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException("", new RuntimeException());
            });
            expectThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException(new RuntimeException());
            });
        }
        {
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
        }
        {
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
            assertTrue(new AwaitingException(new InterruptedException()).isCausedByInterruption());
            assertFalse(new AwaitingException().isCausedByInterruption());
        }
        {
            // WrappedException
            assertSame(new WrappedException(cause).getCause(), cause);
        }
    }

    @Test
    public void testAwaitingException() {
        assertTrue(new AwaitingException(new TimeoutException()).isCausedByTimeout());
        assertFalse(new AwaitingException().isCausedByTimeout());
        assertTrue(new AwaitingException(new CancellationException()).isCausedByCancellation());
        assertFalse(new AwaitingException().isCausedByCancellation());
        assertTrue(new AwaitingException(new ExecutionException(new RuntimeException())).isCausedByExecution());
        assertFalse(new AwaitingException().isCausedByExecution());
        assertTrue(new AwaitingException(new InterruptedException()).isCausedByInterruption());
        assertFalse(new AwaitingException().isCausedByInterruption());
    }
}
