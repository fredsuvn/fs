package tests.core.base.number;

import internal.annotations.J17Also;
import internal.utils.DataGen;
import internal.utils.ErrorNumber;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.number.NumberException;
import space.sunqian.fs.base.number.NumberFormatter;
import space.sunqian.fs.base.number.NumberKit;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NumberTest implements DataGen, TestPrint {

    @Test
    public void testStringToNumber() {
        // Test primitive types
        assertEquals((byte) -123, NumberKit.toNumber("-123", byte.class));
        assertEquals((short) -123L, NumberKit.toNumber("-123", short.class));
        assertEquals((char) 123, NumberKit.toNumber("123", char.class));
        assertEquals(123, NumberKit.toNumber("123", int.class));
        assertEquals(123L, NumberKit.toNumber("123", long.class));
        assertEquals(123f, NumberKit.toNumber("123", float.class));
        assertEquals(123.0, NumberKit.toNumber("123", double.class));

        // Test wrapper types
        assertEquals((byte) -123, NumberKit.toNumber("-123", Byte.class));
        assertEquals((short) -123L, NumberKit.toNumber("-123", Short.class));
        assertEquals((char) 123, NumberKit.toNumber("123", Character.class));
        assertEquals(123, NumberKit.toNumber("123", Integer.class));
        assertEquals(123L, NumberKit.toNumber("123", Long.class));
        assertEquals(123f, NumberKit.toNumber("123", Float.class));
        assertEquals(123.0, NumberKit.toNumber("123", Double.class));

        // Test big number types
        assertEquals(new BigInteger("123"), NumberKit.toNumber("123", BigInteger.class));
        assertEquals(new BigDecimal("123"), NumberKit.toNumber("123", BigDecimal.class));

        // Test error cases
        assertThrows(NumberException.class, () -> NumberKit.toNumber("kkk", Integer.class));
        assertThrows(NumberException.class, () -> NumberKit.toNumber("kkk", String.class));
    }

    @Test
    public void testNumberToNumber() {
        // Test with different number types
        testNumberToNumber((byte) 123);
        testNumberToNumber((short) 123);
        testNumberToNumber(123);
        testNumberToNumber(123L);
        testNumberToNumber(123f);
        testNumberToNumber(123d);
        testNumberToNumber(new BigInteger("123"));
        testNumberToNumber(new BigDecimal("123"));

        // Test with Number class
        assertEquals(123, NumberKit.toNumber(123, Number.class));

        // Test error case with anonymous Number
        assertThrows(NumberException.class, () -> NumberKit.toNumber(new Number() {
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
    }

    private void testNumberToNumber(Number number) {
        // Test conversion to primitive types
        assertEquals((byte) 123, NumberKit.toNumber(number, byte.class));
        assertEquals((short) 123L, NumberKit.toNumber(number, short.class));
        assertEquals((char) 123, NumberKit.toNumber(number, char.class));
        assertEquals(123, NumberKit.toNumber(number, int.class));
        assertEquals(123L, NumberKit.toNumber(number, long.class));
        assertEquals(123f, NumberKit.toNumber(number, float.class));
        assertEquals(123.0, NumberKit.toNumber(number, double.class));

        // Test conversion to wrapper types
        assertEquals((byte) 123, NumberKit.toNumber(number, Byte.class));
        assertEquals((short) 123L, NumberKit.toNumber(number, Short.class));
        assertEquals((char) 123, NumberKit.toNumber(number, Character.class));
        assertEquals(123, NumberKit.toNumber(number, Integer.class));
        assertEquals(123L, NumberKit.toNumber(number, Long.class));
        assertEquals(123f, NumberKit.toNumber(number, Float.class));
        assertEquals(123.0, NumberKit.toNumber(number, Double.class));

        // Test conversion to big number types
        if (!(number instanceof Float || number instanceof Double)) {
            assertEquals(NumberKit.toNumber(number, BigInteger.class), new BigInteger(number.toString()));
        }
        assertEquals(NumberKit.toNumber(number, BigDecimal.class), new BigDecimal(number.toString()));
    }

    @Test
    public void testSpecifiedFormatter() {
        NumberFormatter formatter = NumberFormatter.ofPattern(NumberKit.DEFAULT_PATTERN);

        // Test parsing Double
        Double number = formatter.parseSafe("123.123456", Double.class);
        assertNotNull(number);
        assertEquals(123.123456, number);

        // Test formatting Double
        assertEquals("123.12", formatter.format(number));
        assertEquals("123.12", formatter.formatSafe(number));

        // Test parsing BigDecimal
        BigDecimal decimal = formatter.parseSafe("123.123456", BigDecimal.class);
        assertNotNull(decimal);
        assertEquals(new BigDecimal("123.123456"), decimal);

        // Test formatting BigDecimal
        assertEquals("123.12", formatter.format(decimal));
        assertEquals("123.12", formatter.formatSafe(decimal));

        // Test error cases
        assertNull(formatter.parseSafe("123.123", String.class));
        assertNull(formatter.formatSafe(null));
        assertNull(formatter.parseSafe("XXXXX", int.class));
        assertNull(formatter.parseSafe(null, int.class));
        assertNull(formatter.formatSafe(new ErrorNumber()));
    }

    @Test
    public void testCommonFormatter() {
        NumberFormatter formatter = NumberFormatter.common();

        // Test parsing large BigDecimal
        BigDecimal number = formatter.parse(
            "123.123456123456123456123456123456123456123456123456", BigDecimal.class
        );
        assertNotNull(number);
        assertEquals(
            new BigDecimal("123.123456123456123456123456123456123456123456123456"),
            number
        );

        // Test formatting large BigDecimal
        assertEquals(
            "123.123456123456123456123456123456123456123456123456",
            formatter.format(number)
        );
    }

    @J17Also
    @Test
    public void testCharSequenceToNumber() {
        // Test positive numbers
        testPositiveNumbers();

        // Test positive numbers with plus sign
        testPositiveNumbersWithPlusSign();

        // Test negative numbers
        testNegativeNumbers();

        // Test error cases
        testCharSequenceErrorCases();
    }

    private void testPositiveNumbers() {
        assertEquals(123, NumberKit.toNumber("123"));
        assertEquals(2, NumberKit.toNumber("123", 1, 2));
        assertEquals(123456789, NumberKit.toNumber("123456789"));
        assertEquals(12345678910L, NumberKit.toNumber("12345678910"));
        assertEquals(23456789101L, NumberKit.toNumber("123456789101", 1, 12));
        assertEquals(123456789123456789L, NumberKit.toNumber("123456789123456789"));
        assertEquals(new BigInteger("1234567891234567891"), NumberKit.toNumber("1234567891234567891"));
        assertEquals(new BigDecimal("1.1"), NumberKit.toNumber("1.1"));
        assertEquals(new BigDecimal("2.1"), NumberKit.toNumber("12.1", 1, 4));
        assertEquals(new BigDecimal("1.1e12"), NumberKit.toNumber("1.1e12"));
        assertEquals(new BigDecimal("2e12"), NumberKit.toNumber("2e12"));
        assertEquals(new BigDecimal("2E12"), NumberKit.toNumber("2E12"));
    }

    private void testPositiveNumbersWithPlusSign() {
        assertEquals(123, NumberKit.toNumber("+123"));
        assertEquals(123456789, NumberKit.toNumber("+123456789"));
        assertEquals(12345678910L, NumberKit.toNumber("+12345678910"));
        assertEquals(123456789123456789L, NumberKit.toNumber("+123456789123456789"));
        assertEquals(new BigInteger("1234567891234567891"), NumberKit.toNumber("+1234567891234567891"));
        assertEquals(new BigDecimal("1.1"), NumberKit.toNumber("+1.1"));
        assertEquals(new BigDecimal("1.1e12"), NumberKit.toNumber("+1.1e12"));
        assertEquals(new BigDecimal("2e12"), NumberKit.toNumber("+2e12"));
        assertEquals(new BigDecimal("2E12"), NumberKit.toNumber("+2E12"));
    }

    private void testNegativeNumbers() {
        assertEquals(-123, NumberKit.toNumber("-123"));
        assertEquals(-123456789, NumberKit.toNumber("-123456789"));
        assertEquals(-12345678910L, NumberKit.toNumber("-12345678910"));
        assertEquals(-123456789123456789L, NumberKit.toNumber("-123456789123456789"));
        assertEquals(new BigInteger("-1234567891234567891"), NumberKit.toNumber("-1234567891234567891"));
        assertEquals(new BigDecimal("-1.1"), NumberKit.toNumber("-1.1"));
        assertEquals(new BigDecimal("-1.1e12"), NumberKit.toNumber("-1.1e12"));
        assertEquals(new BigDecimal("-2e12"), NumberKit.toNumber("-2e12"));
        assertEquals(new BigDecimal("-2E12"), NumberKit.toNumber("-2E12"));
    }

    private void testCharSequenceErrorCases() {
        assertThrows(NumberException.class, () -> NumberKit.toNumber("+"));
        assertThrows(NumberException.class, () -> NumberKit.toNumber("-"));
        assertThrows(NumberException.class, () -> NumberKit.toNumber(""));
        assertThrows(NumberException.class, () -> NumberKit.toNumber("0x123"));
        assertThrows(NumberException.class, () -> NumberKit.toNumber("0x123", 1, 2));
    }

    @Test
    public void testNumException() {
        // Test NumException constructors
        assertThrows(NumberException.class, () -> {throw new NumberException();});
        assertThrows(NumberException.class, () -> {throw new NumberException("");});
        assertThrows(NumberException.class, () -> {throw new NumberException("", new RuntimeException());});
        assertThrows(NumberException.class, () -> {throw new NumberException(new RuntimeException());});
    }
}
