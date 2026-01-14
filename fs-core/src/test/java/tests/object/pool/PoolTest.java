package tests.object.pool;

import internal.test.AssertTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.object.pool.ObjectPoolException;
import space.sunqian.fs.object.pool.SimplePool;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PoolTest implements AssertTest, PrintTest {

    @Test
    public void testSimplePool() throws Exception {

        class X {
        }

        {
            // normal case
            IntVar counter = IntVar.of(0);
            IntVar discardCounter = IntVar.of(0);
            X[] xs = new X[5];
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
            assertFalse(pool.isClosed());
            assertEquals(2, counter.get());
            assertEquals(2, pool.size());
            assertEquals(2, pool.idleSize());
            assertEquals(0, pool.activeSize());
            for (int i = 0; i < 5; i++) {
                X obj = pool.get();
                assertNotNull(obj);
                xs[i] = obj;
            }
            assertEquals(5, pool.size());
            assertEquals(0, pool.idleSize());
            assertEquals(5, pool.activeSize());
            pool.clean();
            assertEquals(5, pool.size());
            assertEquals(0, pool.idleSize());
            assertEquals(5, pool.activeSize());
            assertEquals(5, counter.get());
            assertNull(pool.get());
            assertNull(pool.get());
            assertEquals(5, counter.get());
            pool.release(xs[2]);
            assertEquals(1, pool.idleSize());
            assertEquals(4, pool.activeSize());
            assertEquals(5, pool.size());
            assertSame(xs[2], pool.get());
            assertNull(pool.get());
            pool.release(new X());
            assertEquals(0, pool.idleSize());
            assertEquals(5, pool.activeSize());
            assertEquals(5, pool.size());
            assertNull(pool.get());
            assertEquals(5, counter.get());
            assertEquals(Collections.emptyList(), pool.unreleasedObjects());
            pool.clean();
            pool.close();
            assertTrue(pool.isClosed());
            assertEquals(0, pool.size());
            assertThrows(ObjectPoolException.class, pool::get);
            assertThrows(ObjectPoolException.class, () -> pool.release(xs[2]));
            assertThrows(ObjectPoolException.class, pool::clean);
            pool.close();
            assertTrue(pool.isClosed());
            assertEquals(0, pool.size());
            assertEquals(SetKit.set(xs), SetKit.toSet(pool.unreleasedObjects()));
        }
        {
            // init failed
            IntVar counter = IntVar.of(0);
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(Duration.ofHours(99).toMillis())
                .supplier(() -> {
                    int count = counter.getAndIncrement();
                    if (count >= 2) {
                        throw new ObjectPoolException();
                    }
                    return xs[count] = new X();
                })
                .validator(t -> true)
                .build();
            assertTrue(pool.isClosed());
            assertEquals(Collections.emptyList(), pool.unreleasedObjects());
            assertNotNull(xs[0]);
            assertNotNull(xs[1]);
            assertNull(xs[2]);
            assertNull(xs[3]);
            assertNull(xs[4]);
        }
        {
            // init failed and discard failed
            IntVar counter = IntVar.of(0);
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(Duration.ofHours(99).toMillis())
                .supplier(() -> {
                    int count = counter.getAndIncrement();
                    if (count >= 2) {
                        throw new ObjectPoolException();
                    }
                    return xs[count] = new X();
                })
                .discarder(t -> {throw new ObjectPoolException();})
                .build();
            assertTrue(pool.isClosed());
            assertEquals(SetKit.set(xs[0], xs[1]), SetKit.toSet(pool.unreleasedObjects()));
            assertNotNull(xs[0]);
            assertNotNull(xs[1]);
            assertNull(xs[2]);
            assertNull(xs[3]);
            assertNull(xs[4]);
        }
        {
            // discard failed
            IntVar counter = IntVar.of(0);
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(Duration.ofHours(99))
                .supplier(() -> {
                    int count = counter.getAndIncrement();
                    if (count >= 2) {
                        throw new ObjectPoolException();
                    }
                    return xs[count] = new X();
                })
                .validator(t -> true)
                .discarder(t -> {throw new ObjectPoolException();})
                .build();
            assertTrue(pool.isClosed());
            assertEquals(0, pool.size());
            assertEquals(SetKit.set(xs[0], xs[1]), SetKit.toSet(pool.unreleasedObjects()));
        }
        {
            // validator false
            IntVar counter = IntVar.of(0);
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(2)
                .idleTimeout(Duration.ofHours(99))
                .supplier(() -> {
                    int count = counter.getAndIncrement();
                    if (count >= 3) {
                        throw new ObjectPoolException();
                    }
                    return xs[count] = new X();
                })
                .validator(t -> false)
                .build();
            assertNotNull(xs[0]);
            assertNotNull(xs[1]);
            assertNull(xs[2]);
            X o = pool.get();
            assertNotNull(o);
            assertSame(xs[2], o);
            assertFalse(pool.isClosed());
            assertThrows(ObjectPoolException.class, pool::get);
            assertNull(xs[3]);
            assertNull(xs[4]);
            assertTrue(pool.isClosed());
        }
        {
            // release validator false
            IntVar counter = IntVar.of(0);
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(Duration.ofHours(99))
                .supplier(() -> new X())
                .validator(t -> {
                    int count = counter.getAndIncrement();
                    return count == 0;
                })
                .build();
            assertEquals(5, pool.size());
            X x = pool.get();
            assertNotNull(x);
            pool.release(x);
            assertEquals(4, pool.size());
        }
        {
            // release error
            IntVar counter = IntVar.of(0);
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(Duration.ofHours(99))
                .supplier(() -> new X())
                .validator(t -> {
                    int count = counter.getAndIncrement();
                    if (count == 0) {
                        return true;
                    } else {
                        throw new ObjectPoolException();
                    }
                })
                .build();
            assertEquals(5, pool.size());
            X x = pool.get();
            assertNotNull(x);
            assertThrows(ObjectPoolException.class, () -> pool.release(x));
            assertTrue(pool.isClosed());
            assertEquals(0, pool.size());
            assertEquals(Collections.emptyList(), pool.unreleasedObjects());
        }
        {
            // clean normal
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(1)
                .maxSize(5)
                .idleTimeout(1)
                .supplier(() -> new X())
                .build();
            assertFalse(pool.isClosed());
            for (int i = 0; i < xs.length; i++) {
                X x = pool.get();
                assertNotNull(x);
                xs[i] = x;
            }
            assertEquals(5, pool.size());
            assertEquals(0, pool.idleSize());
            assertEquals(5, pool.activeSize());
            pool.release(xs[0]);
            assertEquals(5, pool.size());
            assertEquals(1, pool.idleSize());
            assertEquals(4, pool.activeSize());
            Thread.sleep(10L);
            pool.clean();
            assertEquals(4, pool.size());
            assertEquals(0, pool.idleSize());
            assertEquals(4, pool.activeSize());
            for (X x : xs) {
                pool.release(x);
            }
            Thread.sleep(10L);
            pool.clean();
            assertEquals(1, pool.size());
            assertEquals(1, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertFalse(pool.isClosed());
        }
        {
            // clean validator false
            IntVar counter = IntVar.of(0);
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(1)
                .supplier(() -> {
                    counter.getAndIncrement();
                    return new X();
                })
                .validator(t -> false)
                .build();
            assertFalse(pool.isClosed());
            assertEquals(5, pool.size());
            assertEquals(5, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertEquals(5, counter.get());
            pool.clean();
            assertEquals(5, pool.size());
            assertEquals(5, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertEquals(10, counter.get());

        }
        {
            // clean no timeout
            IntVar counter = IntVar.of(0);
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(1)
                .maxSize(5)
                .idleTimeout(Duration.ofHours(99))
                .supplier(() -> {
                    counter.getAndIncrement();
                    return new X();
                })
                .build();
            for (int i = 0; i < xs.length; i++) {
                xs[i] = pool.get();
                assertNotNull(xs[i]);
            }
            for (X x : xs) {
                pool.release(x);
            }
            assertFalse(pool.isClosed());
            assertEquals(5, pool.size());
            assertEquals(5, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertEquals(5, counter.get());
            pool.clean();
            assertEquals(5, pool.size());
            assertEquals(5, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertEquals(5, counter.get());
        }
        {
            // clean error
            IntVar counter = IntVar.of(0);
            X[] xs = new X[5];
            SimplePool<X> pool = SimplePool.newBuilder()
                .coreSize(5)
                .idleTimeout(Duration.ofHours(99))
                .supplier(() -> {
                    int c = counter.getAndIncrement();
                    xs[c] = new X();
                    return xs[c];
                })
                .validator(t -> {throw new ObjectPoolException();})
                .build();
            for (X value : xs) {
                assertNotNull(value);
            }
            assertFalse(pool.isClosed());
            assertEquals(5, pool.size());
            assertEquals(5, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertEquals(5, counter.get());
            assertThrows(ObjectPoolException.class, pool::clean);
            assertTrue(pool.isClosed());
            assertEquals(0, pool.size());
            assertEquals(0, pool.idleSize());
            assertEquals(0, pool.activeSize());
            assertEquals(5, counter.get());
            assertEquals(Collections.emptyList(), pool.unreleasedObjects());
        }
        {
            assertThrows(IllegalArgumentException.class, () -> {
                SimplePool.newBuilder().coreSize(-1);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                SimplePool.newBuilder().coreSize(10).maxSize(5);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                SimplePool.newBuilder().idleTimeout(-1);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                SimplePool.newBuilder().idleTimeout(Duration.ofHours(-1));
            });
            assertThrows(IllegalArgumentException.class, () -> {
                SimplePool.newBuilder().build();
            });
        }
    }

    @Test
    public void testException() {
        {
            // ObjectPoolException
            assertThrows(ObjectPoolException.class, () -> {
                throw new ObjectPoolException();
            });
            assertThrows(ObjectPoolException.class, () -> {
                throw new ObjectPoolException("");
            });
            assertThrows(ObjectPoolException.class, () -> {
                throw new ObjectPoolException("", new RuntimeException());
            });
            assertThrows(ObjectPoolException.class, () -> {
                throw new ObjectPoolException(new RuntimeException());
            });
        }
    }
}
