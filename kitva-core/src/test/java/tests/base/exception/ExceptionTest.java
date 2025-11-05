package tests.base.exception;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.base.exception.KitvaException;
import space.sunqian.common.base.exception.KitvaRuntimeException;
import space.sunqian.common.base.exception.ThrowKit;
import space.sunqian.common.base.exception.UnknownArrayTypeException;
import space.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import space.sunqian.common.base.exception.UnknownTypeException;
import space.sunqian.common.base.exception.UnreachablePointException;
import space.sunqian.common.base.exception.WrappedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            assertThrows(KitvaException.class, () -> {
                throw new KitvaException();
            });
            assertThrows(KitvaException.class, () -> {
                throw new KitvaException("");
            });
            assertThrows(KitvaException.class, () -> {
                throw new KitvaException("", new RuntimeException());
            });
            assertThrows(KitvaException.class, () -> {
                throw new KitvaException(new RuntimeException());
            });
        }
        {
            // KitvaRuntimeException
            assertThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException();
            });
            assertThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException("");
            });
            assertThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException("", new RuntimeException());
            });
            assertThrows(KitvaRuntimeException.class, () -> {
                throw new KitvaRuntimeException(new RuntimeException());
            });
        }
        {
            // UnreachablePointException
            assertThrows(UnreachablePointException.class, () -> {
                throw new UnreachablePointException();
            });
            assertThrows(UnreachablePointException.class, () -> {
                throw new UnreachablePointException("");
            });
            assertThrows(UnreachablePointException.class, () -> {
                throw new UnreachablePointException("", new RuntimeException());
            });
            assertThrows(UnreachablePointException.class, () -> {
                throw new UnreachablePointException(new RuntimeException());
            });
        }
        {
            // UnknownTypeException
            assertThrows(UnknownTypeException.class, () -> {
                throw new UnknownTypeException(Object.class);
            });
            assertThrows(UnknownTypeException.class, () -> {
                throw new UnknownTypeException(Object.class, "keyword");
            });
            assertThrows(UnknownTypeException.class, () -> {
                throw new UnknownTypeException("unknow.type");
            });
            // UnknownArrayTypeException
            assertThrows(UnknownArrayTypeException.class, () -> {
                throw new UnknownArrayTypeException(Object.class);
            });
            // UnknownPrimitiveTypeException
            assertThrows(UnknownPrimitiveTypeException.class, () -> {
                throw new UnknownPrimitiveTypeException(Object.class);
            });
        }
        {
            // AwaitingException
            assertThrows(AwaitingException.class, () -> {
                throw new AwaitingException();
            });
            assertThrows(AwaitingException.class, () -> {
                throw new AwaitingException("");
            });
            assertThrows(AwaitingException.class, () -> {
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
