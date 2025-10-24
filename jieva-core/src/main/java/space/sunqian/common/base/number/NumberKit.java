package space.sunqian.common.base.number;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.Kit;

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
     *     <li>{@code byte}, {@link Byte}</li>
     *     <li>{@code short}, {@link Short}</li>
     *     <li>{@code char}, {@link Character} (Converts to {@link Integer} first, then to {@link Character})</li>
     *     <li>{@code int}, {@link Integer}</li>
     *     <li>{@code long}, {@link Long}</li>
     *     <li>{@code float}, {@link Float}</li>
     *     <li>{@code double}, {@link Double}</li>
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
        if (byte.class.equals(numType) || Byte.class.equals(numType)) {
            return Kit.as(Byte.parseByte(str.toString()));
        }
        if (short.class.equals(numType) || Short.class.equals(numType)) {
            return Kit.as(Short.parseShort(str.toString()));
        }
        if (char.class.equals(numType) || Character.class.equals(numType)) {
            int i = Integer.parseInt(str.toString());
            char c = (char) i;
            return Kit.as(c);
        }
        if (int.class.equals(numType) || Integer.class.equals(numType)) {
            return Kit.as(Integer.parseInt(str.toString()));
        }
        if (long.class.equals(numType) || Long.class.equals(numType)) {
            return Kit.as(Long.parseLong(str.toString()));
        }
        if (float.class.equals(numType) || Float.class.equals(numType)) {
            return Kit.as(Float.parseFloat(str.toString()));
        }
        if (double.class.equals(numType) || Double.class.equals(numType)) {
            return Kit.as(Double.parseDouble(str.toString()));
        }
        if (BigInteger.class.equals(numType)) {
            return Kit.as(new BigInteger(str.toString()));
        }
        if (BigDecimal.class.equals(numType)) {
            return Kit.as(new BigDecimal(str.toString()));
        }
        throw new UnsupportedOperationException("Unsupported number type: " + numType);
    }

    /**
     * Converts the given number to the specified other number type. Supported other number types are:
     * <ul>
     *     <li>{@code byte}, {@link Byte}</li>
     *     <li>{@code short}, {@link Short}</li>
     *     <li>{@code char}, {@link Character} (Converts to {@link Integer} first, then to {@link Character})</li>
     *     <li>{@code int}, {@link Integer}</li>
     *     <li>{@code long}, {@link Long}</li>
     *     <li>{@code float}, {@link Float}</li>
     *     <li>{@code double}, {@link Double}</li>
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
        if (byte.class.equals(numType) || Byte.class.equals(numType)) {
            return Kit.as(num.byteValue());
        }
        if (short.class.equals(numType) || Short.class.equals(numType)) {
            return Kit.as(num.shortValue());
        }
        if (char.class.equals(numType) || Character.class.equals(numType)) {
            int i = num.intValue();
            char c = (char) i;
            return Kit.as(c);
        }
        if (int.class.equals(numType) || Integer.class.equals(numType)) {
            return Kit.as(num.intValue());
        }
        if (long.class.equals(numType) || Long.class.equals(numType)) {
            return Kit.as(num.longValue());
        }
        if (float.class.equals(numType) || Float.class.equals(numType)) {
            return Kit.as(num.floatValue());
        }
        if (double.class.equals(numType) || Double.class.equals(numType)) {
            return Kit.as(num.doubleValue());
        }
        if (BigInteger.class.equals(numType)) {
            if (num instanceof BigInteger) {
                return Kit.as(num);
            }
            if (num instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) num;
                return Kit.as(decimal.toBigInteger());
            }
            return Kit.as(new BigInteger(num.toString()));
        }
        if (BigDecimal.class.equals(numType)) {
            if (num instanceof BigDecimal) {
                return Kit.as(num);
            }
            if (num instanceof BigInteger) {
                BigInteger integer = (BigInteger) num;
                return Kit.as(new BigDecimal(integer));
            }
            return Kit.as(new BigDecimal(num.toString()));
        }
        throw new UnsupportedOperationException("Unsupported number type: " + numType);
    }
}
