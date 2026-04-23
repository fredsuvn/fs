package tests.core.base.number;

import internal.utils.DataGen;
import internal.utils.ErrorNumber;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.number.NumException;
import space.sunqian.fs.base.number.NumFormatter;
import space.sunqian.fs.base.number.NumKit;

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
        assertEquals((byte) -123, NumKit.toNumber("-123", byte.class));
        assertEquals((short) -123L, NumKit.toNumber("-123", short.class));
        assertEquals((char) 123, NumKit.toNumber("123", char.class));
        assertEquals(123, NumKit.toNumber("123", int.class));
        assertEquals(123L, NumKit.toNumber("123", long.class));
        assertEquals(123f, NumKit.toNumber("123", float.class));
        assertEquals(123.0, NumKit.toNumber("123", double.class));

        // Test wrapper types
        assertEquals((byte) -123, NumKit.toNumber("-123", Byte.class));
        assertEquals((short) -123L, NumKit.toNumber("-123", Short.class));
        assertEquals((char) 123, NumKit.toNumber("123", Character.class));
        assertEquals(123, NumKit.toNumber("123", Integer.class));
        assertEquals(123L, NumKit.toNumber("123", Long.class));
        assertEquals(123f, NumKit.toNumber("123", Float.class));
        assertEquals(123.0, NumKit.toNumber("123", Double.class));

        // Test big number types
        assertEquals(new BigInteger("123"), NumKit.toNumber("123", BigInteger.class));
        assertEquals(new BigDecimal("123"), NumKit.toNumber("123", BigDecimal.class));

        // Test error cases
        assertThrows(NumException.class, () -> NumKit.toNumber("kkk", Integer.class));
        assertThrows(NumException.class, () -> NumKit.toNumber("kkk", String.class));
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
        assertEquals(123, NumKit.toNumber(123, Number.class));

        // Test error case with anonymous Number
        assertThrows(NumException.class, () -> NumKit.toNumber(new Number() {
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
        assertEquals((byte) 123, NumKit.toNumber(number, byte.class));
        assertEquals((short) 123L, NumKit.toNumber(number, short.class));
        assertEquals((char) 123, NumKit.toNumber(number, char.class));
        assertEquals(123, NumKit.toNumber(number, int.class));
        assertEquals(123L, NumKit.toNumber(number, long.class));
        assertEquals(123f, NumKit.toNumber(number, float.class));
        assertEquals(123.0, NumKit.toNumber(number, double.class));

        // Test conversion to wrapper types
        assertEquals((byte) 123, NumKit.toNumber(number, Byte.class));
        assertEquals((short) 123L, NumKit.toNumber(number, Short.class));
        assertEquals((char) 123, NumKit.toNumber(number, Character.class));
        assertEquals(123, NumKit.toNumber(number, Integer.class));
        assertEquals(123L, NumKit.toNumber(number, Long.class));
        assertEquals(123f, NumKit.toNumber(number, Float.class));
        assertEquals(123.0, NumKit.toNumber(number, Double.class));

        // Test conversion to big number types
        if (!(number instanceof Float || number instanceof Double)) {
            assertEquals(NumKit.toNumber(number, BigInteger.class), new BigInteger(number.toString()));
        }
        assertEquals(NumKit.toNumber(number, BigDecimal.class), new BigDecimal(number.toString()));
    }

    @Test
    public void testSpecifiedFormatter() {
        NumFormatter formatter = NumFormatter.ofPattern(NumKit.DEFAULT_PATTERN);

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
        NumFormatter formatter = NumFormatter.common();

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
        assertEquals(123, NumKit.toNumber("123"));
        assertEquals(123456789, NumKit.toNumber("123456789"));
        assertEquals(12345678910L, NumKit.toNumber("12345678910"));
        assertEquals(123456789123456789L, NumKit.toNumber("123456789123456789"));
        assertEquals(new BigInteger("1234567891234567891"), NumKit.toNumber("1234567891234567891"));
        assertEquals(new BigDecimal("1.1"), NumKit.toNumber("1.1"));
        assertEquals(new BigDecimal("1.1e12"), NumKit.toNumber("1.1e12"));
        assertEquals(new BigDecimal("2e12"), NumKit.toNumber("2e12"));
        assertEquals(new BigDecimal("2E12"), NumKit.toNumber("2E12"));
    }

    private void testPositiveNumbersWithPlusSign() {
        assertEquals(123, NumKit.toNumber("+123"));
        assertEquals(123456789, NumKit.toNumber("+123456789"));
        assertEquals(12345678910L, NumKit.toNumber("+12345678910"));
        assertEquals(123456789123456789L, NumKit.toNumber("+123456789123456789"));
        assertEquals(new BigInteger("1234567891234567891"), NumKit.toNumber("+1234567891234567891"));
        assertEquals(new BigDecimal("1.1"), NumKit.toNumber("+1.1"));
        assertEquals(new BigDecimal("1.1e12"), NumKit.toNumber("+1.1e12"));
        assertEquals(new BigDecimal("2e12"), NumKit.toNumber("+2e12"));
        assertEquals(new BigDecimal("2E12"), NumKit.toNumber("+2E12"));
    }

    private void testNegativeNumbers() {
        assertEquals(-123, NumKit.toNumber("-123"));
        assertEquals(-123456789, NumKit.toNumber("-123456789"));
        assertEquals(-12345678910L, NumKit.toNumber("-12345678910"));
        assertEquals(-123456789123456789L, NumKit.toNumber("-123456789123456789"));
        assertEquals(new BigInteger("-1234567891234567891"), NumKit.toNumber("-1234567891234567891"));
        assertEquals(new BigDecimal("-1.1"), NumKit.toNumber("-1.1"));
        assertEquals(new BigDecimal("-1.1e12"), NumKit.toNumber("-1.1e12"));
        assertEquals(new BigDecimal("-2e12"), NumKit.toNumber("-2e12"));
        assertEquals(new BigDecimal("-2E12"), NumKit.toNumber("-2E12"));
    }

    private void testCharSequenceErrorCases() {
        assertThrows(NumException.class, () -> NumKit.toNumber("+"));
        assertThrows(NumException.class, () -> NumKit.toNumber("-"));
        assertThrows(NumException.class, () -> NumKit.toNumber(""));
        assertThrows(NumException.class, () -> NumKit.toNumber("0x123"));
    }

    @Test
    public void testNumException() {
        // Test NumException constructors
        assertThrows(NumException.class, () -> {throw new NumException();});
        assertThrows(NumException.class, () -> {throw new NumException("");});
        assertThrows(NumException.class, () -> {throw new NumException("", new RuntimeException());});
        assertThrows(NumException.class, () -> {throw new NumException(new RuntimeException());});
    }
}
