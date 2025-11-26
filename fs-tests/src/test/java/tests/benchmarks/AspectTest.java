package tests.benchmarks;

import internal.tests.api.AspectApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AspectTest {

    @Test
    public void testAspect() throws Exception {
        testAspect("fs-asm");
        testAspect("direct");
    }

    public void testAspect(String aspectType) throws Exception {
        assertEquals(
            "4hello",
            AspectApi.createAspect(aspectType).withPrimitive(1, 2, "hello")
        );
        assertEquals(
            "22hello",
            AspectApi.createAspect(aspectType).withoutPrimitive(1, 2L, "hello")
        );
    }
}
