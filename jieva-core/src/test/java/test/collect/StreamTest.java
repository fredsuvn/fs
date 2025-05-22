package test.collect;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.JieStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class StreamTest {

    @Test
    public void testStream() {
        assertEquals(
            JieStream.stream(1, 2, 3).collect(Collectors.toList()),
            Arrays.asList(1, 2, 3)
        );
        assertEquals(
            JieStream.stream(Arrays.asList(1, 2, 3)).collect(Collectors.toList()),
            Arrays.asList(1, 2, 3)
        );
        assertEquals(
            JieStream.stream(() -> Arrays.asList(1, 2, 3).iterator()).collect(Collectors.toList()),
            Arrays.asList(1, 2, 3)
        );
        assertEquals(JieStream.stream().collect(Collectors.toList()), Collections.emptyList());
        assertEquals(JieStream.stream(Collections::emptyIterator).collect(Collectors.toList()), Collections.emptyList());
    }
}
