package tests.core.base.value;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.value.SimpleKey;
import space.sunqian.fs.base.value.SimpleKey2;
import space.sunqian.fs.base.value.SimpleKey3;
import space.sunqian.fs.base.value.SimpleKeyN;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleKeyTest {

    @Test
    public void testSimpleKeyN() {
        {
            // Test get
            assertEquals(1, (Integer) SimpleKeyN.of(1, 2, 3).getAs(0));
            assertEquals(2, (Integer) SimpleKeyN.of(1, 2, 3).getAs(1));
            assertEquals(3, (Integer) SimpleKeyN.of(1, 2, 3).getAs(2));
            assertThrows(IndexOutOfBoundsException.class, () -> SimpleKeyN.of(1, 2, 3).getAs(3));
            assertThrows(ClassCastException.class, () -> {
                long l = SimpleKeyN.of(1, 2, 3).getAs(0);
                System.out.println(l);
            });
            assertEquals(3, SimpleKeyN.of(1, 2, 3).size());
            assertEquals(4, SimpleKeyN.of(1, 2, 3, 4).size());
            assertEquals(0, SimpleKeyN.of().size());
        }
        {
            // Test toString
            assertEquals(
                "k:[1, 2, 3]",
                SimpleKeyN.of(1, 2, 3).toString()
            );
            assertEquals(
                "k:[1, 2, 3, 4]",
                SimpleKeyN.of(1, 2, 3, 4).toString()
            );
            assertEquals(
                "k:[]",
                SimpleKeyN.of().toString()
            );
        }
        {
            // Test equality
            assertEquals(SimpleKeyN.of(1, 2, 3), SimpleKeyN.of(1, 2, 3));
            assertNotEquals(SimpleKeyN.of(1, 2, 3), SimpleKeyN.of(1, 2));
            assertFalse(SimpleKeyN.of(1, 2, 3).equals(1));
            SimpleKeyN sk = SimpleKeyN.of(1, 2, 3);
            assertTrue(sk.equals(sk));
        }
        {
            // Test hashCode
            SimpleKeyN sk = SimpleKeyN.of(1, 2, 3, "999", "888");
            assertEquals(
                hashCode(sk),
                sk.hashCode()
            );
        }
    }

    @Test
    public void testSimpleKey2() {
        {
            // Test get
            assertEquals(1, SimpleKey2.of(1, 2).get(0));
            assertEquals(1, (Integer) SimpleKey2.of(1, 2).getAs(0));
            assertEquals(1, SimpleKey2.of(1, 2).first());
            assertEquals(1, (Integer) SimpleKey2.of(1, 2).firstAs());
            assertEquals(2, SimpleKey2.of(1, 2).get(1));
            assertEquals(2, (Integer) SimpleKey2.of(1, 2).getAs(1));
            assertEquals(2, SimpleKey2.of(1, 2).second());
            assertEquals(2, (Integer) SimpleKey2.of(1, 2).secondAs());
            assertThrows(IndexOutOfBoundsException.class, () -> SimpleKey2.of(1, 2).get(2));
            assertThrows(IndexOutOfBoundsException.class, () -> SimpleKey2.of(1, 2).getAs(2));
            assertThrows(ClassCastException.class, () -> {
                long l = SimpleKey2.of(1, 2).getAs(0);
                System.out.println(l);
            });
            assertEquals(2, SimpleKey2.of(1, 2).size());
        }
        {
            // Test toString
            assertEquals(
                "k:[1, 2]",
                SimpleKey2.of(1, 2).toString()
            );
        }
        {
            // Test equality
            assertEquals(SimpleKey2.of(1, 2), SimpleKey2.of(1, 2));
            assertNotEquals(SimpleKey2.of(1, 2), SimpleKey2.of(2, 1));
            assertNotEquals(SimpleKey2.of(1, 2), SimpleKey2.of(1, 3));
            assertFalse(SimpleKey2.of(1, 2).equals(1));
            SimpleKey2 sk = SimpleKey2.of(1, 2);
            assertTrue(sk.equals(sk));
        }
        {
            // Test hashCode
            SimpleKey2 sk = SimpleKey2.of(1, 2);
            assertEquals(
                hashCode(sk),
                sk.hashCode()
            );
        }
    }

    @Test
    public void testSimpleKey3() {
        {
            // Test get
            assertEquals(1, SimpleKey3.of(1, 2, 3).get(0));
            assertEquals(1, (Integer) SimpleKey3.of(1, 2, 3).getAs(0));
            assertEquals(1, SimpleKey3.of(1, 2, 3).first());
            assertEquals(1, (Integer) SimpleKey3.of(1, 2, 3).firstAs());
            assertEquals(2, SimpleKey3.of(1, 2, 3).get(1));
            assertEquals(2, (Integer) SimpleKey3.of(1, 2, 3).getAs(1));
            assertEquals(2, SimpleKey3.of(1, 2, 3).second());
            assertEquals(2, (Integer) SimpleKey3.of(1, 2, 3).secondAs());
            assertEquals(3, SimpleKey3.of(1, 2, 3).get(2));
            assertEquals(3, (Integer) SimpleKey3.of(1, 2, 3).getAs(2));
            assertEquals(3, SimpleKey3.of(1, 2, 3).third());
            assertEquals(3, (Integer) SimpleKey3.of(1, 2, 3).thirdAs());
            assertThrows(IndexOutOfBoundsException.class, () -> SimpleKey3.of(1, 2, 3).get(3));
            assertThrows(IndexOutOfBoundsException.class, () -> SimpleKey3.of(1, 2, 3).getAs(3));
            assertThrows(ClassCastException.class, () -> {
                long l = SimpleKey3.of(1, 2, 3).getAs(0);
                System.out.println(l);
            });
            assertEquals(3, SimpleKey3.of(1, 2, 3).size());
        }
        {
            // Test toString
            assertEquals(
                "k:[1, 2, 3]",
                SimpleKey3.of(1, 2, 3).toString()
            );
        }
        {
            // Test equality
            assertEquals(SimpleKey3.of(1, 2, 3), SimpleKey3.of(1, 2, 3));
            assertNotEquals(SimpleKey3.of(1, 2, 3), SimpleKey3.of(2, 2, 3));
            assertNotEquals(SimpleKey3.of(1, 2, 3), SimpleKey3.of(1, 1, 3));
            assertNotEquals(SimpleKey3.of(1, 2, 3), SimpleKey3.of(1, 2, 1));
            assertFalse(SimpleKey3.of(1, 2, 3).equals(1));
            SimpleKey3 sk = SimpleKey3.of(1, 2, 3);
            assertTrue(sk.equals(sk));
        }
        {
            // Test hashCode
            SimpleKey3 sk = SimpleKey3.of(1, 2, 3);
            assertEquals(
                hashCode(sk),
                sk.hashCode()
            );
        }
    }

    @Test
    public void testSimpleKey() {
        SimpleKey sn2 = SimpleKeyN.of(1, 2);
        SimpleKey sn3 = SimpleKeyN.of(1, 2, 3);
        SimpleKey sk2 = SimpleKey2.of(1, 2);
        SimpleKey sk3 = SimpleKey3.of(1, 2, 3);
        assertEquals(sn2, sk2);
        assertEquals(sn3, sk3);
        assertEquals(sk2, sn2);
        assertEquals(sk3, sn3);
        assertNotEquals(sn2, sk3);
        assertNotEquals(sn2, SimpleKey2.of(1, 3));
    }

    private int hashCode(SimpleKey k) {
        int result = 0;
        for (Object e : k.elements()) {
            result = 31 * result + Objects.hashCode(e);
        }
        return result;
    }
}
