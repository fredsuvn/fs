package tests.function;

import org.junit.jupiter.api.Test;
import space.sunqian.common.function.callable.BooleanCallable;
import space.sunqian.common.function.callable.DoubleCallable;
import space.sunqian.common.function.callable.IntCallable;
import space.sunqian.common.function.callable.LongCallable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {

    @Test
    public void testCallable() throws Exception {
        {
            BooleanCallable call = () -> true;
            assertEquals(call.callAsBoolean(), call.call());
        }
        {
            IntCallable call = () -> 66;
            assertEquals(call.callAsInt(), call.call());
        }
        {
            LongCallable call = () -> 66;
            assertEquals(call.callAsLong(), call.call());
        }
        {
            DoubleCallable call = () -> 66;
            assertEquals(call.callAsDouble(), call.call());
        }
    }
}
