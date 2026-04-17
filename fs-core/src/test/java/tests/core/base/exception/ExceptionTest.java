package tests.core.base.exception;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.exception.AwaitingException;
import space.sunqian.fs.base.exception.FsException;
import space.sunqian.fs.base.exception.FsRuntimeException;
import space.sunqian.fs.base.exception.ThrowKit;
import space.sunqian.fs.base.exception.UnknownArrayTypeException;
import space.sunqian.fs.base.exception.UnknownPrimitiveTypeException;
import space.sunqian.fs.base.exception.UnknownTypeException;
import space.sunqian.fs.base.exception.UnreachablePointException;
import space.sunqian.fs.base.exception.UnsupportedEnvException;
import space.sunqian.fs.base.exception.WrappedException;

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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Exception e = new Exception();
        String traceInfo = ThrowKit.toString(e);
        e.printStackTrace(pw);
        assertEquals(sw.toString(), traceInfo);
    }

    @Test
    public void testFsExceptionConstructors() {
        // Test FsException constructors
        assertThrows(FsException.class, () -> {throw new FsException();});
        assertThrows(FsException.class, () -> {throw new FsException("");});
        assertThrows(FsException.class, () -> {throw new FsException("", new RuntimeException());});
        assertThrows(FsException.class, () -> {throw new FsException(new RuntimeException());});
    }

    @Test
    public void testFsRuntimeExceptionConstructors() {
        // Test FsRuntimeException constructors
        assertThrows(FsRuntimeException.class, () -> {throw new FsRuntimeException();});
        assertThrows(FsRuntimeException.class, () -> {throw new FsRuntimeException("");});
        assertThrows(FsRuntimeException.class, () -> {throw new FsRuntimeException("", new RuntimeException());});
        assertThrows(FsRuntimeException.class, () -> {throw new FsRuntimeException(new RuntimeException());});
    }

    @Test
    public void testUnreachablePointExceptionConstructors() {
        // Test UnreachablePointException constructors
        assertThrows(UnreachablePointException.class, () -> {throw new UnreachablePointException();});
        assertThrows(UnreachablePointException.class, () -> {throw new UnreachablePointException("");});
        assertThrows(UnreachablePointException.class, () -> {
            throw new UnreachablePointException("", new RuntimeException());
        });
        assertThrows(UnreachablePointException.class, () -> {
            throw new UnreachablePointException(new RuntimeException());
        });
    }

    @Test
    public void testUnknownTypeExceptions() {
        // Test UnknownTypeException constructors
        assertThrows(UnknownTypeException.class, () -> {throw new UnknownTypeException(Object.class);});
        assertThrows(UnknownTypeException.class, () -> {throw new UnknownTypeException(Object.class, "keyword");});
        assertThrows(UnknownTypeException.class, () -> {throw new UnknownTypeException("unknow.type");});

        // Test UnknownArrayTypeException
        assertThrows(UnknownArrayTypeException.class, () -> {throw new UnknownArrayTypeException(Object.class);});

        // Test UnknownPrimitiveTypeException
        assertThrows(UnknownPrimitiveTypeException.class, () -> {
            throw new UnknownPrimitiveTypeException(Object.class);
        });
    }

    @Test
    public void testAwaitingExceptionConstructors() {
        // Test AwaitingException constructors
        assertThrows(AwaitingException.class, () -> {throw new AwaitingException();});
        assertThrows(AwaitingException.class, () -> {throw new AwaitingException("");});
        assertThrows(AwaitingException.class, () -> {throw new AwaitingException("", new RuntimeException());});
        assertThrows(AwaitingException.class, () -> {throw new AwaitingException(new RuntimeException());});

        // Test isCausedByInterruption
        assertTrue(new AwaitingException(new InterruptedException()).isCausedByInterruption());
        assertFalse(new AwaitingException().isCausedByInterruption());
    }

    @Test
    public void testAwaitingExceptionCauseMethods() {
        // Test isCausedByTimeout
        assertTrue(new AwaitingException(new TimeoutException()).isCausedByTimeout());
        assertFalse(new AwaitingException().isCausedByTimeout());

        // Test isCausedByCancellation
        assertTrue(new AwaitingException(new CancellationException()).isCausedByCancellation());
        assertFalse(new AwaitingException().isCausedByCancellation());

        // Test isCausedByExecution
        assertTrue(new AwaitingException(new ExecutionException(new RuntimeException())).isCausedByExecution());
        assertFalse(new AwaitingException().isCausedByExecution());

        // Test isCausedByInterruption
        assertTrue(new AwaitingException(new InterruptedException()).isCausedByInterruption());
        assertFalse(new AwaitingException().isCausedByInterruption());
    }

    @Test
    public void testWrappedException() {
        Throwable cause = new RuntimeException("hello");
        assertSame(new WrappedException(cause).getCause(), cause);
    }

    @Test
    public void testUnsupportedEnvExceptionConstructors() {
        // Test UnsupportedEnvException constructors
        assertThrows(UnsupportedEnvException.class, () -> {throw new UnsupportedEnvException();});
        assertThrows(UnsupportedEnvException.class, () -> {throw new UnsupportedEnvException("");});
        assertThrows(UnsupportedEnvException.class, () -> {
            throw new UnsupportedEnvException("", new RuntimeException());
        });
        assertThrows(UnsupportedEnvException.class, () -> {throw new UnsupportedEnvException(new RuntimeException());});
    }
}
