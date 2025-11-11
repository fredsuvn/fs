package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.Kit;
import space.sunqian.common.collect.CollectKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectTest {

    @Test
    public void testEmpty() {
        // Testing with null
        assertTrue(CollectKit.isEmpty((Iterable<?>) null));
        assertFalse(CollectKit.isNotEmpty((Iterable<?>) null));
        // Testing with empty collection
        assertTrue(CollectKit.isEmpty(Collections.emptyList()));
        assertFalse(CollectKit.isNotEmpty(Collections.emptyList()));
        // Testing with non-empty collection
        assertFalse(CollectKit.isEmpty(Arrays.asList(1, 2, 3)));
        assertTrue(CollectKit.isNotEmpty(Arrays.asList(1, 2, 3)));
        // Testing with empty iterable
        assertTrue(CollectKit.isEmpty(() -> new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        }));
        assertTrue(CollectKit.isNotEmpty(() -> new Iterator<Object>() {
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
        assertFalse(CollectKit.isEmpty(Collections.singletonList(1)));
        assertTrue(CollectKit.isNotEmpty(Collections.singletonList(1)));
    }

    @Test
    public void testToArray() {
        assertArrayEquals(CollectKit.toArray(Arrays.asList(1, 2, 3)), Arrays.asList(1, 2, 3).toArray());
        assertArrayEquals(CollectKit.toArray(Arrays.asList(1, 2, 3), Integer.class), Arrays.asList(1, 2, 3).toArray(new Integer[3]));
        assertArrayEquals(
            CollectKit.toArray(() -> Kit.as(Arrays.asList(1, 2, 3).iterator())),
            Arrays.asList(1, 2, 3).toArray()
        );
        assertArrayEquals(
            CollectKit.toArray(() -> Kit.as(Arrays.asList(1, 2, 3).iterator()), Integer.class),
            Arrays.asList(1, 2, 3).toArray(new Integer[3])
        );
    }

    @Test
    public void testIterator() {
        {
            // it -> en
            Iterator<Integer> intIt = Arrays.asList(1, 2, 3).iterator();
            Enumeration<Integer> intEnum = CollectKit.asEnumeration(intIt);
            assertTrue(intEnum.hasMoreElements());
            assertEquals(1, intEnum.nextElement());
            assertTrue(intEnum.hasMoreElements());
            assertEquals(2, intEnum.nextElement());
            assertTrue(intEnum.hasMoreElements());
            assertEquals(3, intEnum.nextElement());
            assertFalse(intEnum.hasMoreElements());
            assertThrows(NoSuchElementException.class, intEnum::nextElement);
        }
        {
            // en -> it
            Vector<Integer> vector = new Vector<>();
            vector.add(1);
            vector.add(2);
            vector.add(3);
            Enumeration<Integer> intEnum = vector.elements();
            Iterator<Integer> intIt = CollectKit.asIterator(intEnum);
            assertTrue(intIt.hasNext());
            assertEquals(1, intIt.next());
            assertTrue(intIt.hasNext());
            assertEquals(2, intIt.next());
            assertTrue(intIt.hasNext());
            assertEquals(3, intIt.next());
            assertFalse(intIt.hasNext());
            assertThrows(NoSuchElementException.class, intIt::next);
        }
    }

    @Test
    public void testAddAll() {
        assertEquals(CollectKit.addAll(new ArrayList<>(), 1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(CollectKit.addAll(new ArrayList<>(), Arrays.asList(1, 2, 3)), Arrays.asList(1, 2, 3));
        assertEquals(
            CollectKit.addAll(new ArrayList<>(), () -> Arrays.asList(1, 2, 3).iterator()),
            Arrays.asList(1, 2, 3)
        );
    }
}
