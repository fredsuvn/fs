package tests.internal;

import internal.utils.Mocker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MockTest {

    @Test
    public void testMock() throws Exception {
        // Test primitive types and wrapper classes
        assertEquals(0, Mocker.mock(int.class));
        assertEquals(0, Mocker.mock(Integer.class));
        assertEquals(0L, Mocker.mock(Long.class));
        assertEquals(0L, Mocker.mock(long.class));
        assertEquals(0.0f, Mocker.mock(Float.class));
        assertEquals(0.0f, Mocker.mock(float.class));
        assertEquals(0.0d, Mocker.mock(Double.class));
        assertEquals(0.0d, Mocker.mock(double.class));
        assertEquals((char) 0, Mocker.mock(char.class));
        assertEquals((char) 0, Mocker.mock(Character.class));
        assertEquals(false, Mocker.mock(boolean.class));
        assertEquals(false, Mocker.mock(Boolean.class));
        assertEquals((byte) 0, Mocker.mock(byte.class));
        assertEquals((byte) 0, Mocker.mock(Byte.class));
        assertEquals((short) 0, Mocker.mock(short.class));
        assertEquals((short) 0, Mocker.mock(Short.class));

        // Test number types
        assertEquals(BigDecimal.ZERO, Mocker.mock(BigDecimal.class));
        assertEquals(BigInteger.ZERO, Mocker.mock(BigInteger.class));

        // Test other types
        assertNull(Mocker.mock(void.class));
        assertInstanceOf(Object.class, Mocker.mock(Object.class));
        assertInstanceOf(String.class, Mocker.mock(String.class));

        // Test array types
        assertInstanceOf(int[].class, Mocker.mock(int[].class));
        assertArrayEquals(new Object[0], Mocker.mock(Object[].class));

        // Test enum types
        assertInstanceOf(TestEnum.class, Mocker.mock(TestEnum.class));
        // Test empty enum type
        assertNull(Mocker.mock(EmptyEnum.class));

        // Test collection types
        assertEquals(Collections.emptyList(), Mocker.mock(Collection.class));

        // Test functional interfaces
        assertArrayEquals(new Object[0], (Object[]) Mocker.mock(IntFunction.class).apply(0));

        // Test final classes
        assertNull(Mocker.mock(C.class));
    }

    public enum TestEnum {
        VALUE1, VALUE2
    }

    public enum EmptyEnum {
    }

    public static final class C {}
}
