package tests.benchmarks;

import internal.api.AspectApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AspectTest {

    @Test
    public void testAspectWithDifferentImplementations() throws Exception {
        testAspectImplementation("fs-asm");
        // testAspectImplementation("byte-buddy");
        testAspectImplementation("direct");
    }

    private void testAspectImplementation(String aspectType) throws Exception {
        AspectApi api = AspectApi.createApi(aspectType);

        // Test with primitive parameters
        assertEquals("4hello", api.withPrimitive(1, 2, "hello"));

        // Test with non-primitive parameters
        assertEquals("22hello", api.withoutPrimitive(1, 2L, "hello"));
    }
}
