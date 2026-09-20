package tests.core.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.lang.MTuple2;
import space.sunqian.fs.base.lang.MTuple3;
import space.sunqian.fs.base.lang.MTuple4;
import space.sunqian.fs.base.lang.Tuple2;
import space.sunqian.fs.base.lang.Tuple3;
import space.sunqian.fs.base.lang.Tuple4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TupleTest {

    @Test
    public void testTuple2() {
        {
            // test immutable
            Tuple2<String, Integer> tuple = Tuple2.of("hello", 2026);
            assertEquals("hello", tuple.get0());
            assertEquals(2026, tuple.get1());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, Tuple2.of("hello", 2026));
            assertNotEquals(tuple, Tuple2.of("hello", 2027));
            assertNotEquals(tuple, Tuple2.of("world", 2026));
            assertNotEquals(tuple, "[hello, 2026]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), Tuple2.of("hello", 2026).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple2.of("hello", 2027).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple2.of("world", 2026).hashCode());

            // test toString
            assertEquals("[hello, 2026]", tuple.toString());
            assertEquals("[world, 2027]", Tuple2.of("world", 2027).toString());
        }
        {
            // test mutable
            MTuple2<String, Integer> tuple = MTuple2.of("hello", 2026);
            assertEquals("hello", tuple.get0());
            assertEquals(2026, tuple.get1());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, MTuple2.of("hello", 2026));
            assertNotEquals(tuple, MTuple2.of("hello", 2027));
            assertNotEquals(tuple, MTuple2.of("world", 2026));
            assertNotEquals(tuple, "[hello, 2026]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), MTuple2.of("hello", 2026).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple2.of("hello", 2027).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple2.of("world", 2026).hashCode());

            // test toString
            assertEquals("[hello, 2026]", tuple.toString());
            assertEquals("[world, 2027]", MTuple2.of("world", 2027).toString());

            // clone
            assertEquals(tuple, tuple.clone());
            assertNotSame(tuple, tuple.clone());

            // to immutable
            assertEquals(Tuple2.of("hello", 2026), tuple.immutable());

            // change elements:
            assertSame(tuple, tuple.set0("world"));
            assertSame(tuple, tuple.set1(2027));
            assertEquals("world", tuple.get0());
            assertEquals(2027, tuple.get1());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, MTuple2.of("world", 2027));
            assertNotEquals(tuple, MTuple2.of("hello", 2027));
            assertNotEquals(tuple, MTuple2.of("world", 2026));
            assertNotEquals(tuple, "[world, 2027]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), MTuple2.of("world", 2027).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple2.of("hello", 2027).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple2.of("world", 2026).hashCode());

            // test toString
            assertEquals("[world, 2027]", tuple.toString());
            assertEquals("[hello, 2026]", MTuple2.of("hello", 2026).toString());

            // clone
            assertEquals(tuple, tuple.clone());
            assertNotSame(tuple, tuple.clone());

            // to immutable
            assertEquals(Tuple2.of("world", 2027), tuple.immutable());

            // test empty
            MTuple2<String, Integer> emptyTuple = MTuple2.newTuple();
            assertNull(emptyTuple.get0());
            assertNull(emptyTuple.get1());
        }
    }

    @Test
    public void testTuple3() {
        {
            // test immutable
            Tuple3<String, Integer, Long> tuple = Tuple3.of("hello", 2026, 920L);
            assertEquals("hello", tuple.get0());
            assertEquals(2026, tuple.get1());
            assertEquals(920L, tuple.get2());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, Tuple3.of("hello", 2026, 920L));
            assertNotEquals(tuple, Tuple3.of("world", 2026, 920L));
            assertNotEquals(tuple, Tuple3.of("hello", 2027, 920L));
            assertNotEquals(tuple, Tuple3.of("hello", 2026, 930L));
            assertNotEquals(tuple, "[hello, 2026, 920]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), Tuple3.of("hello", 2026, 920L).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple3.of("world", 2026, 920L).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple3.of("hello", 2027, 920L).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple3.of("hello", 2026, 930L).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920]", tuple.toString());
            assertEquals("[world, 2027, 920]", Tuple3.of("world", 2027, 920L).toString());
        }
        {
            // test mutable
            MTuple3<String, Integer, Long> tuple = MTuple3.of("hello", 2026, 920L);
            assertEquals("hello", tuple.get0());
            assertEquals(2026, tuple.get1());
            assertEquals(920L, tuple.get2());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, MTuple3.of("hello", 2026, 920L));
            assertNotEquals(tuple, MTuple3.of("world", 2026, 920L));
            assertNotEquals(tuple, MTuple3.of("hello", 2027, 920L));
            assertNotEquals(tuple, MTuple3.of("hello", 2026, 930L));
            assertNotEquals(tuple, "[hello, 2026, 920]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), MTuple3.of("hello", 2026, 920L).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple3.of("world", 2026, 920L).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple3.of("hello", 2027, 920L).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple3.of("hello", 2026, 930L).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920]", tuple.toString());
            assertEquals("[world, 2027, 920]", MTuple3.of("world", 2027, 920L).toString());

            // clone
            assertEquals(tuple, tuple.clone());
            assertNotSame(tuple, tuple.clone());

            // to immutable
            assertEquals(Tuple3.of("hello", 2026, 920L), tuple.immutable());

            // change elements:
            assertSame(tuple, tuple.set0("world"));
            assertSame(tuple, tuple.set1(2027));
            assertSame(tuple, tuple.set2(930L));
            assertEquals("world", tuple.get0());
            assertEquals(2027, tuple.get1());
            assertEquals(930L, tuple.get2());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, MTuple3.of("world", 2027, 930L));
            assertNotEquals(tuple, MTuple3.of("hello", 2027, 930L));
            assertNotEquals(tuple, MTuple3.of("world", 2026, 930L));
            assertNotEquals(tuple, MTuple3.of("world", 2027, 920L));
            assertNotEquals(tuple, "[world, 2027, 930]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), MTuple3.of("world", 2027, 930L).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple3.of("hello", 2027, 930L).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple3.of("world", 2026, 930L).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple3.of("world", 2027, 920L).hashCode());

            // test toString
            assertEquals("[world, 2027, 930]", tuple.toString());
            assertEquals("[hello, 2026, 920]", MTuple3.of("hello", 2026, 920L).toString());

            // clone
            assertEquals(tuple, tuple.clone());
            assertNotSame(tuple, tuple.clone());

            // to immutable
            assertEquals(Tuple3.of("world", 2027, 930L), tuple.immutable());

            // test empty
            MTuple3<String, Integer, Long> emptyTuple = MTuple3.newTuple();
            assertNull(emptyTuple.get0());
            assertNull(emptyTuple.get1());
            assertNull(emptyTuple.get2());
        }
    }

    @Test
    public void testTuple4() {
        {
            // test immutable
            Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("hello", 2026, 920L, true);
            assertEquals("hello", tuple.get0());
            assertEquals(2026, tuple.get1());
            assertEquals(920L, tuple.get2());
            assertEquals(true, tuple.get3());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, Tuple4.of("hello", 2026, 920L, true));
            assertNotEquals(tuple, Tuple4.of("world", 2026, 920L, true));
            assertNotEquals(tuple, Tuple4.of("hello", 2027, 920L, true));
            assertNotEquals(tuple, Tuple4.of("hello", 2026, 930L, true));
            assertNotEquals(tuple, Tuple4.of("hello", 2026, 920L, false));
            assertNotEquals(tuple, "[hello, 2026, 920, true]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), Tuple4.of("hello", 2026, 920L, true).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple4.of("world", 2026, 920L, true).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple4.of("hello", 2027, 920L, true).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple4.of("hello", 2026, 930L, true).hashCode());
            assertNotEquals(tuple.hashCode(), Tuple4.of("hello", 2026, 920L, false).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920, true]", tuple.toString());
            assertEquals("[world, 2027, 920, true]", Tuple4.of("world", 2027, 920L, true).toString());
        }
        {
            // test mutable
            MTuple4<String, Integer, Long, Boolean> tuple = MTuple4.of("hello", 2026, 920L, true);
            assertEquals("hello", tuple.get0());
            assertEquals(2026, tuple.get1());
            assertEquals(920L, tuple.get2());
            assertEquals(true, tuple.get3());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, MTuple4.of("hello", 2026, 920L, true));
            assertNotEquals(tuple, MTuple4.of("world", 2026, 920L, true));
            assertNotEquals(tuple, MTuple4.of("hello", 2027, 920L, true));
            assertNotEquals(tuple, MTuple4.of("hello", 2026, 930L, true));
            assertNotEquals(tuple, MTuple4.of("hello", 2026, 920L, false));
            assertNotEquals(tuple, "[hello, 2026, 920, true]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), MTuple4.of("hello", 2026, 920L, true).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("world", 2026, 920L, true).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("hello", 2027, 920L, true).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("hello", 2026, 930L, true).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("hello", 2026, 920L, false).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920, true]", tuple.toString());
            assertEquals("[world, 2027, 920, true]", MTuple4.of("world", 2027, 920L, true).toString());

            // clone
            assertEquals(tuple, tuple.clone());
            assertNotSame(tuple, tuple.clone());

            // to immutable
            assertEquals(Tuple4.of("hello", 2026, 920L, true), tuple.immutable());

            // change elements:
            assertSame(tuple, tuple.set0("world"));
            assertSame(tuple, tuple.set1(2027));
            assertSame(tuple, tuple.set2(930L));
            assertSame(tuple, tuple.set3(false));
            assertEquals("world", tuple.get0());
            assertEquals(2027, tuple.get1());
            assertEquals(930L, tuple.get2());
            assertEquals(false, tuple.get3());

            // test equals
            assertEquals(tuple, tuple);
            assertEquals(tuple, MTuple4.of("world", 2027, 930L, false));
            assertNotEquals(tuple, MTuple4.of("hello", 2027, 930L, false));
            assertNotEquals(tuple, MTuple4.of("world", 2026, 930L, false));
            assertNotEquals(tuple, MTuple4.of("world", 2027, 920L, false));
            assertNotEquals(tuple, MTuple4.of("world", 2027, 930L, true));
            assertNotEquals(tuple, "[world, 2027, 930, false]");

            // test hashCode
            assertEquals(tuple.hashCode(), tuple.hashCode());
            assertEquals(tuple.hashCode(), MTuple4.of("world", 2027, 930L, false).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("hello", 2027, 930L, false).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("world", 2026, 930L, false).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("world", 2027, 920L, false).hashCode());
            assertNotEquals(tuple.hashCode(), MTuple4.of("world", 2027, 930L, true).hashCode());

            // test toString
            assertEquals("[world, 2027, 930, false]", tuple.toString());
            assertEquals("[hello, 2026, 920, false]", MTuple4.of("hello", 2026, 920L, false).toString());

            // clone
            assertEquals(tuple, tuple.clone());
            assertNotSame(tuple, tuple.clone());

            // to immutable
            assertEquals(Tuple4.of("world", 2027, 930L, false), tuple.immutable());

            // test empty
            MTuple4<String, Integer, Long, Boolean> emptyTuple = MTuple4.newTuple();
            assertNull(emptyTuple.get0());
            assertNull(emptyTuple.get1());
            assertNull(emptyTuple.get2());
            assertNull(emptyTuple.get3());
        }
    }
}
