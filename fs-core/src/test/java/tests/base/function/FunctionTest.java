package tests.base.function;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.function.BooleanCallable;
import space.sunqian.fs.base.function.CacheFunction;
import space.sunqian.fs.base.function.DoubleCallable;
import space.sunqian.fs.base.function.IntCallable;
import space.sunqian.fs.base.function.LongCallable;

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

    @Test
    public void testCacheFunction() throws Exception {
        assertEquals(
            666,
            (
                (CacheFunction<Integer, Integer>) (key, loader) ->
                    loader.apply(key)
            ).get(66, k -> 666)
        );
    }
}
