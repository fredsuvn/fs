package tests.object.pool;

import internal.test.AssertTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.object.pool.SimplePool;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PoolTest implements AssertTest, PrintTest {

    @Test
    public void testSimplePool() throws Exception {

        IntVar counter = IntVar.of(0);
        IntVar discardCounter = IntVar.of(0);
        class X {
        }

        SimplePool<X> pool = SimplePool.newBuilder()
            .coreSize(2)
            .maxSize(5)
            .idleTimeout(Duration.ofHours(99))
            .supplier(() -> {
                counter.incrementAndGet();
                return new X();
            })
            .discarder(t -> discardCounter.incrementAndGet())
            .build();
        assertEquals(2, counter.get());
        for (int i = 0; i < 5; i++) {
            X obj = pool.get();
            assertNotNull(obj);
        }
        assertEquals(5, counter.get());
        //assertNull();
    }
}
