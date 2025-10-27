package tests.concurrent;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.concurrent.FutureKit;

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
        {
            Future<String> future = service.submit(() -> "hello");
            assertEquals(FutureKit.get(future), "hello");
            assertEquals(FutureKit.get(future, 100), "hello");
            assertEquals(FutureKit.get(future, Duration.ofMillis(100)), "hello");
        }
        {
            Future<String> future = service.submit(() -> {
                throw new Exception("hello");
            });
            assertThrows(AwaitingException.class, () -> FutureKit.get(future));
            assertThrows(AwaitingException.class, () -> FutureKit.get(future, 100));
            assertThrows(AwaitingException.class, () -> FutureKit.get(future, Duration.ofMillis(100)));
        }
        {
            Future<String> future = service.submit(() -> {
                throw new Exception("hello");
            });
            assertEquals(FutureKit.get(future, "world"), "world");
            assertEquals(FutureKit.get(future, 100, "world"), "world");
            assertEquals(FutureKit.get(future, Duration.ofMillis(100), "world"), "world");
        }
    }
}
