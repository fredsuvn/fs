package tests.base.number;

import internal.test.DataTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.number.NumKit;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NumberTest implements DataTest, PrintTest {

    @Test
    public void testStringToNumber() {
        assertEquals((byte) -123, NumKit.toNumber("-123", byte.class));
        assertEquals((short) -123L, NumKit.toNumber("-123", short.class));
        assertEquals((char) 123, NumKit.toNumber("123", char.class));
        assertEquals(123, NumKit.toNumber("123", int.class));
        assertEquals(123L, NumKit.toNumber("123", long.class));
        assertEquals(123f, NumKit.toNumber("123", float.class));
        assertEquals(123.0, NumKit.toNumber("123", double.class));
        assertEquals((byte) -123, NumKit.toNumber("-123", Byte.class));
        assertEquals((short) -123L, NumKit.toNumber("-123", Short.class));
        assertEquals((char) 123, NumKit.toNumber("123", Character.class));
        assertEquals(123, NumKit.toNumber("123", Integer.class));
        assertEquals(123L, NumKit.toNumber("123", Long.class));
        assertEquals(123f, NumKit.toNumber("123", Float.class));
        assertEquals(123.0, NumKit.toNumber("123", Double.class));
        assertEquals(new BigInteger("123"), NumKit.toNumber("123", BigInteger.class));
        assertEquals(new BigDecimal("123"), NumKit.toNumber("123", BigDecimal.class));
        assertThrows(NumberFormatException.class, () -> NumKit.toNumber("kkk", Integer.class));
        assertThrows(UnsupportedOperationException.class, () -> NumKit.toNumber("kkk", String.class));
    }

    @Test
    public void testNumberToNumber() {
        testNumberToNumber((byte) 123);
        testNumberToNumber((short) 123);
        testNumberToNumber(123);
        testNumberToNumber(123L);
        testNumberToNumber(123f);
        testNumberToNumber(123d);
        testNumberToNumber(new BigInteger("123"));
        testNumberToNumber(new BigDecimal("123"));
        assertThrows(NumberFormatException.class, () -> NumKit.toNumber(new Number() {
            @Override
            public int intValue() {
                return 0;
            }

            @Override
            public long longValue() {
                return 0;
            }

            @Override
            public float floatValue() {
                return 0;
            }

            @Override
            public double doubleValue() {
                return 0;
            }

            @Override
            public String toString() {
                return "anonymous Number{}";
            }
        }, BigInteger.class));
        assertThrows(UnsupportedOperationException.class, () -> NumKit.toNumber(123, Number.class));
    }

    public void testNumberToNumber(Number number) {
        assertEquals((byte) 123, NumKit.toNumber(number, byte.class));
        assertEquals((short) 123L, NumKit.toNumber(number, short.class));
        assertEquals((char) 123, NumKit.toNumber(number, char.class));
        assertEquals(123, NumKit.toNumber(number, int.class));
        assertEquals(123L, NumKit.toNumber(number, long.class));
        assertEquals(123f, NumKit.toNumber(number, float.class));
        assertEquals(123.0, NumKit.toNumber(number, double.class));
        assertEquals((byte) 123, NumKit.toNumber(number, Byte.class));
        assertEquals((short) 123L, NumKit.toNumber(number, Short.class));
        assertEquals((char) 123, NumKit.toNumber(number, Character.class));
        assertEquals(123, NumKit.toNumber(number, Integer.class));
        assertEquals(123L, NumKit.toNumber(number, Long.class));
        assertEquals(123f, NumKit.toNumber(number, Float.class));
        assertEquals(123.0, NumKit.toNumber(number, Double.class));
        if (!(number instanceof Float || number instanceof Double)) {
            assertEquals(NumKit.toNumber(number, BigInteger.class), new BigInteger(number.toString()));
        }
        assertEquals(NumKit.toNumber(number, BigDecimal.class), new BigDecimal(number.toString()));
    }
}
