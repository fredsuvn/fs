package test.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.StreamKit;

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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

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
            assertEquals(supplier.get(), "1");
            assertEquals(supplier.get(), "2");
            assertEquals(supplier.get(), "3");
            expectThrows(NoSuchElementException.class, supplier::get);
        }
        {
            // int
            IntSupplier supplier = StreamKit.toSupplier(IntStream.of(1, 2, 3));
            assertEquals(supplier.getAsInt(), 1);
            assertEquals(supplier.getAsInt(), 2);
            assertEquals(supplier.getAsInt(), 3);
            expectThrows(NoSuchElementException.class, supplier::getAsInt);
        }
        {
            // long
            LongSupplier supplier = StreamKit.toSupplier(LongStream.of(1, 2, 3));
            assertEquals(supplier.getAsLong(), 1);
            assertEquals(supplier.getAsLong(), 2);
            assertEquals(supplier.getAsLong(), 3);
            expectThrows(NoSuchElementException.class, supplier::getAsLong);
        }
        {
            // double
            DoubleSupplier supplier = StreamKit.toSupplier(DoubleStream.of(1, 2, 3));
            assertEquals(supplier.getAsDouble(), 1.0);
            assertEquals(supplier.getAsDouble(), 2.0);
            assertEquals(supplier.getAsDouble(), 3.0);
            expectThrows(NoSuchElementException.class, supplier::getAsDouble);
        }
    }
}
