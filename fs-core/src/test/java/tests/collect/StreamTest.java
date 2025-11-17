package tests.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.collect.StreamKit;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StreamTest {

    @Test
    public void testStream() {
        assertEquals(
            StreamKit.stream(1, 2, 3).collect(Collectors.toList()),
            Arrays.asList(1, 2, 3)
        );
        assertEquals(
            StreamKit.stream(Arrays.asList(1, 2, 3)).collect(Collectors.toList()),
            Arrays.asList(1, 2, 3)
        );
        assertEquals(
            StreamKit.stream(() -> Arrays.asList(1, 2, 3).iterator()).collect(Collectors.toList()),
            Arrays.asList(1, 2, 3)
        );
        assertEquals(StreamKit.stream().collect(Collectors.toList()), Collections.emptyList());
        assertEquals(StreamKit.stream(Collections::emptyIterator).collect(Collectors.toList()), Collections.emptyList());
    }

    @Test
    public void testSupplier() {
        {
            // Object
            Supplier<String> supplier = StreamKit.toSupplier(StreamKit.stream("1", "2", "3"));
            assertEquals("1", supplier.get());
            assertEquals("2", supplier.get());
            assertEquals("3", supplier.get());
            assertThrows(NoSuchElementException.class, supplier::get);
        }
        {
            // int
            IntSupplier supplier = StreamKit.toSupplier(IntStream.of(1, 2, 3));
            assertEquals(1, supplier.getAsInt());
            assertEquals(2, supplier.getAsInt());
            assertEquals(3, supplier.getAsInt());
            assertThrows(NoSuchElementException.class, supplier::getAsInt);
        }
        {
            // long
            LongSupplier supplier = StreamKit.toSupplier(LongStream.of(1, 2, 3));
            assertEquals(1, supplier.getAsLong());
            assertEquals(2, supplier.getAsLong());
            assertEquals(3, supplier.getAsLong());
            assertThrows(NoSuchElementException.class, supplier::getAsLong);
        }
        {
            // double
            DoubleSupplier supplier = StreamKit.toSupplier(DoubleStream.of(1, 2, 3));
            assertEquals(1.0, supplier.getAsDouble());
            assertEquals(2.0, supplier.getAsDouble());
            assertEquals(3.0, supplier.getAsDouble());
            assertThrows(NoSuchElementException.class, supplier::getAsDouble);
        }
    }
}
