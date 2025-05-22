package test.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieCollect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CollectTest {

    @Test
    public void testEmpty() {
        // Testing with null
        assertTrue(JieCollect.isEmpty((Iterable<?>) null));
        assertFalse(JieCollect.isNotEmpty((Iterable<?>) null));
        // Testing with empty collection
        assertTrue(JieCollect.isEmpty(Collections.emptyList()));
        assertFalse(JieCollect.isNotEmpty(Collections.emptyList()));
        // Testing with non-empty collection
        assertFalse(JieCollect.isEmpty(Arrays.asList(1, 2, 3)));
        assertTrue(JieCollect.isNotEmpty(Arrays.asList(1, 2, 3)));
        // Testing with empty iterable
        assertTrue(JieCollect.isEmpty(() -> new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        }));
        assertTrue(JieCollect.isNotEmpty(() -> new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Object next() {
                return 1;
            }
        }));
        // Testing with non-empty iterable
        assertFalse(JieCollect.isEmpty(Collections.singletonList(1)));
        assertTrue(JieCollect.isNotEmpty(Collections.singletonList(1)));
        // Testing with empty map
        assertTrue(JieCollect.isEmpty(Collections.emptyMap()));
        assertFalse(JieCollect.isNotEmpty(Collections.emptyMap()));
        // Testing with empty map
        assertTrue(JieCollect.isEmpty((Map<?, ?>) null));
        assertFalse(JieCollect.isNotEmpty((Map<?, ?>) null));
        // Testing with non-empty map
        assertFalse(JieCollect.isEmpty(Collections.singletonMap("key", "value")));
        assertTrue(JieCollect.isNotEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    public void testToArray() {
        assertEquals(JieCollect.toArray(Arrays.asList(1, 2, 3)), Arrays.asList(1, 2, 3).toArray());
        assertEquals(JieCollect.toArray(Arrays.asList(1, 2, 3), Integer.class), Arrays.asList(1, 2, 3).toArray(new Integer[3]));
        assertEquals(
            JieCollect.toArray(() -> Jie.as(Arrays.asList(1, 2, 3).iterator())),
            Arrays.asList(1, 2, 3).toArray()
        );
        assertEquals(
            JieCollect.toArray(() -> Jie.as(Arrays.asList(1, 2, 3).iterator()), Integer.class),
            Arrays.asList(1, 2, 3).toArray(new Integer[3])
        );
    }

    @Test
    public void testIterator() {
        {
            // it -> en
            Iterator<Integer> intIt = Arrays.asList(1, 2, 3).iterator();
            Enumeration<Integer> intEnum = JieCollect.asEnumeration(intIt);
            assertTrue(intEnum.hasMoreElements());
            assertEquals(intEnum.nextElement(), 1);
            assertTrue(intEnum.hasMoreElements());
            assertEquals(intEnum.nextElement(), 2);
            assertTrue(intEnum.hasMoreElements());
            assertEquals(intEnum.nextElement(), 3);
            assertFalse(intEnum.hasMoreElements());
            expectThrows(NoSuchElementException.class, intEnum::nextElement);
        }
        {
            // en -> it
            Vector<Integer> vector = new Vector<>();
            vector.add(1);
            vector.add(2);
            vector.add(3);
            Enumeration<Integer> intEnum = vector.elements();
            Iterator<Integer> intIt = JieCollect.asIterator(intEnum);
            assertTrue(intIt.hasNext());
            assertEquals(intIt.next(), 1);
            assertTrue(intIt.hasNext());
            assertEquals(intIt.next(), 2);
            assertTrue(intIt.hasNext());
            assertEquals(intIt.next(), 3);
            assertFalse(intIt.hasNext());
            expectThrows(NoSuchElementException.class, intIt::next);
        }
    }

    @Test
    public void testAddAll() {
        assertEquals(JieCollect.addAll(new ArrayList<>(), 1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(JieCollect.addAll(new ArrayList<>(), Arrays.asList(1, 2, 3)), Arrays.asList(1, 2, 3));
        assertEquals(
            JieCollect.addAll(new ArrayList<>(), () -> Arrays.asList(1, 2, 3).iterator()),
            Arrays.asList(1, 2, 3)
        );
    }
}
