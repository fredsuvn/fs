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
            Tuple2<String, Integer> siTuple = Tuple2.of("hello", 2026);
            assertEquals("hello", siTuple.get0());
            assertEquals(2026, siTuple.get1());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, Tuple2.of("hello", 2026));
            assertNotEquals(siTuple, Tuple2.of("hello", 2027));
            assertNotEquals(siTuple, Tuple2.of("world", 2026));
            assertNotEquals(siTuple, "[hello, 2026]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), Tuple2.of("hello", 2026).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple2.of("hello", 2027).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple2.of("world", 2026).hashCode());

            // test toString
            assertEquals("[hello, 2026]", siTuple.toString());
            assertEquals("[world, 2027]", Tuple2.of("world", 2027).toString());
        }
        {
            // test mutable
            MTuple2<String, Integer> siTuple = MTuple2.of("hello", 2026);
            assertEquals("hello", siTuple.get0());
            assertEquals(2026, siTuple.get1());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, MTuple2.of("hello", 2026));
            assertNotEquals(siTuple, MTuple2.of("hello", 2027));
            assertNotEquals(siTuple, MTuple2.of("world", 2026));
            assertNotEquals(siTuple, "[hello, 2026]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), MTuple2.of("hello", 2026).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple2.of("hello", 2027).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple2.of("world", 2026).hashCode());

            // test toString
            assertEquals("[hello, 2026]", siTuple.toString());
            assertEquals("[world, 2027]", MTuple2.of("world", 2027).toString());

            // clone
            assertEquals(siTuple, siTuple.clone());
            assertNotSame(siTuple, siTuple.clone());

            // to immutable
            assertEquals(Tuple2.of("hello", 2026), siTuple.immutable());

            // change elements:
            assertSame(siTuple, siTuple.set0("world"));
            assertSame(siTuple, siTuple.set1(2027));
            assertEquals("world", siTuple.get0());
            assertEquals(2027, siTuple.get1());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, MTuple2.of("world", 2027));
            assertNotEquals(siTuple, MTuple2.of("hello", 2027));
            assertNotEquals(siTuple, MTuple2.of("world", 2026));
            assertNotEquals(siTuple, "[world, 2027]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), MTuple2.of("world", 2027).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple2.of("hello", 2027).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple2.of("world", 2026).hashCode());

            // test toString
            assertEquals("[world, 2027]", siTuple.toString());
            assertEquals("[hello, 2026]", MTuple2.of("hello", 2026).toString());

            // clone
            assertEquals(siTuple, siTuple.clone());
            assertNotSame(siTuple, siTuple.clone());

            // to immutable
            assertEquals(Tuple2.of("world", 2027), siTuple.immutable());

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
            Tuple3<String, Integer, Long> siTuple = Tuple3.of("hello", 2026, 920L);
            assertEquals("hello", siTuple.get0());
            assertEquals(2026, siTuple.get1());
            assertEquals(920L, siTuple.get2());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, Tuple3.of("hello", 2026, 920L));
            assertNotEquals(siTuple, Tuple3.of("world", 2026, 920L));
            assertNotEquals(siTuple, Tuple3.of("hello", 2027, 920L));
            assertNotEquals(siTuple, Tuple3.of("hello", 2026, 930L));
            assertNotEquals(siTuple, "[hello, 2026, 920]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), Tuple3.of("hello", 2026, 920L).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple3.of("world", 2026, 920L).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple3.of("hello", 2027, 920L).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple3.of("hello", 2026, 930L).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920]", siTuple.toString());
            assertEquals("[world, 2027, 920]", Tuple3.of("world", 2027, 920L).toString());
        }
        {
            // test mutable
            MTuple3<String, Integer, Long> siTuple = MTuple3.of("hello", 2026, 920L);
            assertEquals("hello", siTuple.get0());
            assertEquals(2026, siTuple.get1());
            assertEquals(920L, siTuple.get2());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, MTuple3.of("hello", 2026, 920L));
            assertNotEquals(siTuple, MTuple3.of("world", 2026, 920L));
            assertNotEquals(siTuple, MTuple3.of("hello", 2027, 920L));
            assertNotEquals(siTuple, MTuple3.of("hello", 2026, 930L));
            assertNotEquals(siTuple, "[hello, 2026, 920]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), MTuple3.of("hello", 2026, 920L).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple3.of("world", 2026, 920L).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple3.of("hello", 2027, 920L).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple3.of("hello", 2026, 930L).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920]", siTuple.toString());
            assertEquals("[world, 2027, 920]", MTuple3.of("world", 2027, 920L).toString());

            // clone
            assertEquals(siTuple, siTuple.clone());
            assertNotSame(siTuple, siTuple.clone());

            // to immutable
            assertEquals(Tuple3.of("hello", 2026, 920L), siTuple.immutable());

            // change elements:
            assertSame(siTuple, siTuple.set0("world"));
            assertSame(siTuple, siTuple.set1(2027));
            assertSame(siTuple, siTuple.set2(930L));
            assertEquals("world", siTuple.get0());
            assertEquals(2027, siTuple.get1());
            assertEquals(930L, siTuple.get2());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, MTuple3.of("world", 2027, 930L));
            assertNotEquals(siTuple, MTuple3.of("hello", 2027, 930L));
            assertNotEquals(siTuple, MTuple3.of("world", 2026, 930L));
            assertNotEquals(siTuple, MTuple3.of("world", 2027, 920L));
            assertNotEquals(siTuple, "[world, 2027, 930]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), MTuple3.of("world", 2027, 930L).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple3.of("hello", 2027, 930L).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple3.of("world", 2026, 930L).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple3.of("world", 2027, 920L).hashCode());

            // test toString
            assertEquals("[world, 2027, 930]", siTuple.toString());
            assertEquals("[hello, 2026, 920]", MTuple3.of("hello", 2026, 920L).toString());

            // clone
            assertEquals(siTuple, siTuple.clone());
            assertNotSame(siTuple, siTuple.clone());

            // to immutable
            assertEquals(Tuple3.of("world", 2027, 930L), siTuple.immutable());

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
            Tuple4<String, Integer, Long, Boolean> siTuple = Tuple4.of("hello", 2026, 920L, true);
            assertEquals("hello", siTuple.get0());
            assertEquals(2026, siTuple.get1());
            assertEquals(920L, siTuple.get2());
            assertEquals(true, siTuple.get3());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, Tuple4.of("hello", 2026, 920L, true));
            assertNotEquals(siTuple, Tuple4.of("world", 2026, 920L, true));
            assertNotEquals(siTuple, Tuple4.of("hello", 2027, 920L, true));
            assertNotEquals(siTuple, Tuple4.of("hello", 2026, 930L, true));
            assertNotEquals(siTuple, Tuple4.of("hello", 2026, 920L, false));
            assertNotEquals(siTuple, "[hello, 2026, 920, true]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), Tuple4.of("hello", 2026, 920L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple4.of("world", 2026, 920L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple4.of("hello", 2027, 920L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple4.of("hello", 2026, 930L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), Tuple4.of("hello", 2026, 920L, false).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920, true]", siTuple.toString());
            assertEquals("[world, 2027, 920, true]", Tuple4.of("world", 2027, 920L, true).toString());
        }
        {
            // test mutable
            MTuple4<String, Integer, Long, Boolean> siTuple = MTuple4.of("hello", 2026, 920L, true);
            assertEquals("hello", siTuple.get0());
            assertEquals(2026, siTuple.get1());
            assertEquals(920L, siTuple.get2());
            assertEquals(true, siTuple.get3());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, MTuple4.of("hello", 2026, 920L, true));
            assertNotEquals(siTuple, MTuple4.of("world", 2026, 920L, true));
            assertNotEquals(siTuple, MTuple4.of("hello", 2027, 920L, true));
            assertNotEquals(siTuple, MTuple4.of("hello", 2026, 930L, true));
            assertNotEquals(siTuple, MTuple4.of("hello", 2026, 920L, false));
            assertNotEquals(siTuple, "[hello, 2026, 920, true]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), MTuple4.of("hello", 2026, 920L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("world", 2026, 920L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("hello", 2027, 920L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("hello", 2026, 930L, true).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("hello", 2026, 920L, false).hashCode());

            // test toString
            assertEquals("[hello, 2026, 920, true]", siTuple.toString());
            assertEquals("[world, 2027, 920, true]", MTuple4.of("world", 2027, 920L, true).toString());

            // clone
            assertEquals(siTuple, siTuple.clone());
            assertNotSame(siTuple, siTuple.clone());

            // to immutable
            assertEquals(Tuple4.of("hello", 2026, 920L, true), siTuple.immutable());

            // change elements:
            assertSame(siTuple, siTuple.set0("world"));
            assertSame(siTuple, siTuple.set1(2027));
            assertSame(siTuple, siTuple.set2(930L));
            assertSame(siTuple, siTuple.set3(false));
            assertEquals("world", siTuple.get0());
            assertEquals(2027, siTuple.get1());
            assertEquals(930L, siTuple.get2());
            assertEquals(false, siTuple.get3());

            // test equals
            assertEquals(siTuple, siTuple);
            assertEquals(siTuple, MTuple4.of("world", 2027, 930L, false));
            assertNotEquals(siTuple, MTuple4.of("hello", 2027, 930L, false));
            assertNotEquals(siTuple, MTuple4.of("world", 2026, 930L, false));
            assertNotEquals(siTuple, MTuple4.of("world", 2027, 920L, false));
            assertNotEquals(siTuple, MTuple4.of("world", 2027, 930L, true));
            assertNotEquals(siTuple, "[world, 2027, 930, false]");

            // test hashCode
            assertEquals(siTuple.hashCode(), siTuple.hashCode());
            assertEquals(siTuple.hashCode(), MTuple4.of("world", 2027, 930L, false).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("hello", 2027, 930L, false).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("world", 2026, 930L, false).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("world", 2027, 920L, false).hashCode());
            assertNotEquals(siTuple.hashCode(), MTuple4.of("world", 2027, 930L, true).hashCode());

            // test toString
            assertEquals("[world, 2027, 930, false]", siTuple.toString());
            assertEquals("[hello, 2026, 920, false]", MTuple4.of("hello", 2026, 920L, false).toString());

            // clone
            assertEquals(siTuple, siTuple.clone());
            assertNotSame(siTuple, siTuple.clone());

            // to immutable
            assertEquals(Tuple4.of("world", 2027, 930L, false), siTuple.immutable());

            // test empty
            MTuple4<String, Integer, Long, Boolean> emptyTuple = MTuple4.newTuple();
            assertNull(emptyTuple.get0());
            assertNull(emptyTuple.get1());
            assertNull(emptyTuple.get2());
            assertNull(emptyTuple.get3());
        }
    }
}
