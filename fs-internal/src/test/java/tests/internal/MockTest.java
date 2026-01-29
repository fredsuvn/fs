package tests.internal;

import internal.test.Mocker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MockTest {

    @Test
    public void testMock() throws Exception {
        assertEquals(0, Mocker.mock(int.class));
        assertEquals(0, Mocker.mock(Integer.class));
        assertEquals(0L, Mocker.mock(long.class));
        assertEquals(0L, Mocker.mock(Long.class));
        assertEquals(0.0f, Mocker.mock(float.class));
        assertEquals(0.0f, Mocker.mock(Float.class));
        assertEquals(0.0d, Mocker.mock(double.class));
        assertEquals(0.0d, Mocker.mock(Double.class));
        assertEquals((byte) 0, Mocker.mock(byte.class));
        assertEquals((byte) 0, Mocker.mock(Byte.class));
        assertEquals((char) 0, Mocker.mock(char.class));
        assertEquals((char) 0, Mocker.mock(Character.class));
        assertEquals((short) 0, Mocker.mock(short.class));
        assertEquals((short) 0, Mocker.mock(Short.class));
        assertEquals(false, Mocker.mock(boolean.class));
        assertEquals(false, Mocker.mock(Boolean.class));
        assertNull(Mocker.mock(void.class));
        assertInstanceOf(Object.class, Mocker.mock(Object.class));
        assertInstanceOf(String.class, Mocker.mock(String.class));
        assertInstanceOf(int[].class, Mocker.mock(int[].class));
        assertInstanceOf(BigDecimal.class, Mocker.mock(BigDecimal.class));
        assertInstanceOf(BigInteger.class, Mocker.mock(BigInteger.class));
        assertNull(Mocker.mock(E1.class));
        assertEquals(E2.E, Mocker.mock(E2.class));
        assertNull(Mocker.mock(C.class));
    }

    public enum E1 {}

    public enum E2 {E}

    public static final class C {}
}
