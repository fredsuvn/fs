package xyz.sunqian.common.base.number;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utilities kit for number related.
 *
 * @author sunqian
 */
public class NumberKit {

    /**
     * Converts the given string to the specified number type. Supported number types are:
     * <ul>
     *     <li>{@link Byte}</li>
     *     <li>{@link Short}</li>
     *     <li>{@link Character} (Converts to {@link Integer} first, then to {@link Character})</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link BigInteger}</li>
     *     <li>{@link BigDecimal}</li>
     * </ul>
     *
     * @param str     the given string
     * @param numType the specified number type
     * @param <T>     the number type
     * @return the converted number object
     * @throws NumberFormatException         if the given string can't be converted to the specified number type
     * @throws UnsupportedOperationException if the given number type is not supported
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull CharSequence str, Class<T> numType
    ) throws NumberFormatException, UnsupportedOperationException {
        if (Byte.class.equals(numType)) {
            return Jie.as(Byte.parseByte(str.toString()));
        }
        if (Short.class.equals(numType)) {
            return Jie.as(Short.parseShort(str.toString()));
        }
        if (Character.class.equals(numType)) {
            int i = Integer.parseInt(str.toString());
            char c = (char) i;
            return Jie.as(c);
        }
        if (Integer.class.equals(numType)) {
            return Jie.as(Integer.parseInt(str.toString()));
        }
        if (Long.class.equals(numType)) {
            return Jie.as(Long.parseLong(str.toString()));
        }
        if (Float.class.equals(numType)) {
            return Jie.as(Float.parseFloat(str.toString()));
        }
        if (Double.class.equals(numType)) {
            return Jie.as(Double.parseDouble(str.toString()));
        }
        if (BigInteger.class.equals(numType)) {
            return Jie.as(new BigInteger(str.toString()));
        }
        if (BigDecimal.class.equals(numType)) {
            return Jie.as(new BigDecimal(str.toString()));
        }
        throw new UnsupportedOperationException("Unsupported number type: " + numType);
    }

    /**
     * Converts the given number to the specified other number type. Supported other number types are:
     * <ul>
     *     <li>{@link Byte}</li>
     *     <li>{@link Short}</li>
     *     <li>{@link Character} (Converts to {@link Integer} first, then to {@link Character})</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link BigInteger}</li>
     *     <li>{@link BigDecimal}</li>
     * </ul>
     *
     * @param num     the given number
     * @param numType the specified other number type
     * @param <T>     the number type
     * @return the converted number object
     * @throws NumberFormatException         if the given number can't be converted to the specified number type
     * @throws UnsupportedOperationException if the given number type is not supported
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull Number num, Class<T> numType
    ) throws NumberFormatException, UnsupportedOperationException {
        if (Byte.class.equals(numType)) {
            return Jie.as(num.byteValue());
        }
        if (Short.class.equals(numType)) {
            return Jie.as(num.shortValue());
        }
        if (Character.class.equals(numType)) {
            int i = num.intValue();
            char c = (char) i;
            return Jie.as(c);
        }
        if (Integer.class.equals(numType)) {
            return Jie.as(num.intValue());
        }
        if (Long.class.equals(numType)) {
            return Jie.as(num.longValue());
        }
        if (Float.class.equals(numType)) {
            return Jie.as(num.floatValue());
        }
        if (Double.class.equals(numType)) {
            return Jie.as(num.doubleValue());
        }
        if (BigInteger.class.equals(numType)) {
            if (num instanceof BigInteger) {
                return Jie.as(num);
            }
            if (num instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) num;
                return Jie.as(decimal.toBigInteger());
            }
            return Jie.as(new BigInteger(num.toString()));
        }
        if (BigDecimal.class.equals(numType)) {
            if (num instanceof BigDecimal) {
                return Jie.as(num);
            }
            if (num instanceof BigInteger) {
                BigInteger integer = (BigInteger) num;
                return Jie.as(new BigDecimal(integer));
            }
            return Jie.as(new BigDecimal(num.toString()));
        }
        throw new UnsupportedOperationException("Unsupported number type: " + numType);
    }
}
