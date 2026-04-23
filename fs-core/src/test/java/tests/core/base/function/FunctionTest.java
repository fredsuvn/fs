package tests.core.base.function;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.function.BooleanCallable;
import space.sunqian.fs.base.function.DoubleCallable;
import space.sunqian.fs.base.function.IntCallable;
import space.sunqian.fs.base.function.LongCallable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {

    @Test
    public void testBooleanCallable() throws Exception {
        BooleanCallable call = () -> true;
        assertEquals(call.callAsBoolean(), call.call());
    }

    @Test
    public void testIntCallable() throws Exception {
        IntCallable call = () -> 66;
        assertEquals(call.callAsInt(), call.call());
    }

    @Test
    public void testLongCallable() throws Exception {
        LongCallable call = () -> 66;
        assertEquals(call.callAsLong(), call.call());
    }

    @Test
    public void testDoubleCallable() throws Exception {
        DoubleCallable call = () -> 66;
        assertEquals(call.callAsDouble(), call.call());
    }
}
