package test.collection;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collection.JieCollection;

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

public class CollectionTest {

    @Test
    public void testEmpty() {
        // Testing with null
        assertTrue(JieCollection.isEmpty((Iterable<?>) null));
        assertFalse(JieCollection.isNotEmpty((Iterable<?>) null));
        // Testing with empty collection
        assertTrue(JieCollection.isEmpty(Collections.emptyList()));
        assertFalse(JieCollection.isNotEmpty(Collections.emptyList()));
        // Testing with non-empty collection
        assertFalse(JieCollection.isEmpty(Arrays.asList(1, 2, 3)));
        assertTrue(JieCollection.isNotEmpty(Arrays.asList(1, 2, 3)));
        // Testing with empty iterable
        assertTrue(JieCollection.isEmpty(() -> new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        }));
        assertTrue(JieCollection.isNotEmpty(() -> new Iterator<Object>() {
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
        assertFalse(JieCollection.isEmpty(Collections.singletonList(1)));
        assertTrue(JieCollection.isNotEmpty(Collections.singletonList(1)));
        // Testing with empty map
        assertTrue(JieCollection.isEmpty(Collections.emptyMap()));
        assertFalse(JieCollection.isNotEmpty(Collections.emptyMap()));
        // Testing with empty map
        assertTrue(JieCollection.isEmpty((Map<?, ?>) null));
        assertFalse(JieCollection.isNotEmpty((Map<?, ?>) null));
        // Testing with non-empty map
        assertFalse(JieCollection.isEmpty(Collections.singletonMap("key", "value")));
        assertTrue(JieCollection.isNotEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    public void testToArray() {
        assertEquals(JieCollection.toArray(Arrays.asList(1, 2, 3)), Arrays.asList(1, 2, 3).toArray());
        assertEquals(JieCollection.toArray(Arrays.asList(1, 2, 3), Integer.class), Arrays.asList(1, 2, 3).toArray(new Integer[3]));
        assertEquals(
            JieCollection.toArray(() -> Jie.as(Arrays.asList(1, 2, 3).iterator())),
            Arrays.asList(1, 2, 3).toArray()
        );
        assertEquals(
            JieCollection.toArray(() -> Jie.as(Arrays.asList(1, 2, 3).iterator()), Integer.class),
            Arrays.asList(1, 2, 3).toArray(new Integer[3])
        );
    }

    @Test
    public void testIterator() {
        {
            // it -> en
            Iterator<Integer> intIt = Arrays.asList(1, 2, 3).iterator();
            Enumeration<Integer> intEnum = JieCollection.asEnumeration(intIt);
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
            Iterator<Integer> intIt = JieCollection.asIterator(intEnum);
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
        assertEquals(JieCollection.addAll(new ArrayList<>(), 1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(JieCollection.addAll(new ArrayList<>(), Arrays.asList(1, 2, 3)), Arrays.asList(1, 2, 3));
        assertEquals(
            JieCollection.addAll(new ArrayList<>(), () -> Arrays.asList(1, 2, 3).iterator()),
            Arrays.asList(1, 2, 3)
        );
    }
}
