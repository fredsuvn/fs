package tests.core.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.lang.MTuple2;
import space.sunqian.fs.base.lang.Tuple2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

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
            siTuple.set0("world");
            siTuple.set1(2027);
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
}
