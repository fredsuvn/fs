package test.function;

import org.testng.annotations.Test;
import xyz.sunqian.common.function.callable.BooleanCallable;
import xyz.sunqian.common.function.callable.DoubleCallable;
import xyz.sunqian.common.function.callable.IntCallable;
import xyz.sunqian.common.function.callable.LongCallable;

import static org.testng.Assert.assertEquals;

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
