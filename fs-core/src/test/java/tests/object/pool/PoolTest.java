package tests.object.pool;

import internal.test.AssertTest;
import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.object.pool.SimplePool;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PoolTest implements AssertTest, PrintTest {

    @Test
    public void testSimplePool() throws Exception {
        SimplePool<SimpleObject> pool = SimplePool.newBuilder()
            .coreSize(2)
            .maxSize(5)
            .idleTimeout(1000)
            .supplier(SimpleObject::new)
            .build();
        for (int i = 0; i < 5; i++) {
            SimpleObject obj = pool.get();
            assertNotNull(obj);
            obj.discard = true;
            pool.release(obj);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleObject {
        private boolean discard = false;
    }
}
