package tests.core.concurrent;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.exception.AwaitingException;
import space.sunqian.fs.concurrent.FutureKit;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FutureTest {

    @Test
    public void testFuture() {
        ExecutorService service = Executors.newCachedThreadPool();

        testFutureWithSuccess(service);
        testFutureWithException(service);
        testFutureWithExceptionAndDefaultValue(service);
        testFutureWithSuccessAndDefaultValue(service);
    }

    private void testFutureWithSuccess(ExecutorService service) {
        Future<String> future = service.submit(() -> "hello");
        assertEquals("hello", FutureKit.get(future));
        assertEquals("hello", FutureKit.get(future, 100));
        assertEquals("hello", FutureKit.get(future, Duration.ofMillis(100)));
    }

    private void testFutureWithException(ExecutorService service) {
        Future<String> future = service.submit(() -> {
            throw new Exception("hello");
        });
        assertThrows(AwaitingException.class, () -> FutureKit.get(future));
        assertThrows(AwaitingException.class, () -> FutureKit.get(future, 100));
        assertThrows(AwaitingException.class, () -> FutureKit.get(future, Duration.ofMillis(100)));
    }

    private void testFutureWithExceptionAndDefaultValue(ExecutorService service) {
        Future<String> future = service.submit(() -> {
            throw new Exception("hello");
        });
        assertEquals("world", FutureKit.get(future, "world"));
        assertEquals("world", FutureKit.get(future, 100, "world"));
        assertEquals("world", FutureKit.get(future, Duration.ofMillis(100), "world"));
    }

    private void testFutureWithSuccessAndDefaultValue(ExecutorService service) {
        Future<String> future = service.submit(() -> "hello");
        assertEquals("hello", FutureKit.get(future, "world"));
        assertEquals("hello", FutureKit.get(future, 100, "world"));
        assertEquals("hello", FutureKit.get(future, Duration.ofMillis(100), "world"));
    }
}
