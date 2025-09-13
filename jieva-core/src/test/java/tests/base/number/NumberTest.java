package tests.base.number;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.number.NumberKit;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.PrintTest;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class NumberTest implements DataTest, PrintTest {

    @Test
    public void testStringToNumber() {
        assertEquals(NumberKit.toNumber("-123", Byte.class), (byte) -123);
        assertEquals(NumberKit.toNumber("-123", Short.class), (short) -123L);
        assertEquals(NumberKit.toNumber("123", Character.class), (char) 123);
        assertEquals(NumberKit.toNumber("123", Integer.class), 123);
        assertEquals(NumberKit.toNumber("123", Long.class), 123L);
        assertEquals(NumberKit.toNumber("123", Float.class), 123f);
        assertEquals(NumberKit.toNumber("123", Double.class), 123.0);
        assertEquals(NumberKit.toNumber("123", BigInteger.class), new BigInteger("123"));
        assertEquals(NumberKit.toNumber("123", BigDecimal.class), new BigDecimal("123"));
        expectThrows(NumberFormatException.class, () -> NumberKit.toNumber("kkk", Integer.class));
        expectThrows(UnsupportedOperationException.class, () -> NumberKit.toNumber("kkk", String.class));
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
        expectThrows(NumberFormatException.class, () -> NumberKit.toNumber(new Number() {
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
        expectThrows(UnsupportedOperationException.class, () -> NumberKit.toNumber(123, Number.class));
    }

    public void testNumberToNumber(Number number) {
        assertEquals(NumberKit.toNumber(number, Byte.class), (byte) 123);
        assertEquals(NumberKit.toNumber(number, Short.class), (short) 123L);
        assertEquals(NumberKit.toNumber(number, Character.class), (char) 123);
        assertEquals(NumberKit.toNumber(number, Integer.class), 123);
        assertEquals(NumberKit.toNumber(number, Long.class), 123L);
        assertEquals(NumberKit.toNumber(number, Float.class), 123f);
        assertEquals(NumberKit.toNumber(number, Double.class), 123.0);
        if (!(number instanceof Float || number instanceof Double)) {
            assertEquals(NumberKit.toNumber(number, BigInteger.class), new BigInteger(number.toString()));
        }
        assertEquals(NumberKit.toNumber(number, BigDecimal.class), new BigDecimal(number.toString()));
    }
}
