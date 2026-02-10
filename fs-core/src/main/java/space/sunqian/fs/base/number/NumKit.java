package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utilities kit for number related.
 *
 * @author sunqian
 */
public class NumKit {

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
     * @throws NumException if any error occurs during the conversion
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull CharSequence str, Class<T> numType
    ) throws NumException {
        try {
            if (byte.class.equals(numType) || Byte.class.equals(numType)) {
                return Fs.as(Byte.parseByte(str.toString()));
            }
            if (short.class.equals(numType) || Short.class.equals(numType)) {
                return Fs.as(Short.parseShort(str.toString()));
            }
            if (char.class.equals(numType) || Character.class.equals(numType)) {
                int i = Integer.parseInt(str.toString());
                char c = (char) i;
                return Fs.as(c);
            }
            if (int.class.equals(numType) || Integer.class.equals(numType)) {
                return Fs.as(Integer.parseInt(str.toString()));
            }
            if (long.class.equals(numType) || Long.class.equals(numType)) {
                return Fs.as(Long.parseLong(str.toString()));
            }
            if (float.class.equals(numType) || Float.class.equals(numType)) {
                return Fs.as(Float.parseFloat(str.toString()));
            }
            if (double.class.equals(numType) || Double.class.equals(numType)) {
                return Fs.as(Double.parseDouble(str.toString()));
            }
            if (BigInteger.class.equals(numType)) {
                return Fs.as(new BigInteger(str.toString()));
            }
            if (BigDecimal.class.equals(numType)) {
                return Fs.as(new BigDecimal(str.toString()));
            }
        } catch (Exception e) {
            throw new NumException(e);
        }
        throw new NumException("Failed to convert " + str + " to " + numType + ".");
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
     * @throws NumException if any error occurs during the conversion
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull Number num, Class<T> numType
    ) throws NumException {
        try {
            if (numType.isAssignableFrom(num.getClass())) {
                return numType.cast(num);
            }
            if (byte.class.equals(numType) || Byte.class.equals(numType)) {
                return Fs.as(num.byteValue());
            }
            if (short.class.equals(numType) || Short.class.equals(numType)) {
                return Fs.as(num.shortValue());
            }
            if (char.class.equals(numType) || Character.class.equals(numType)) {
                int i = num.intValue();
                char c = (char) i;
                return Fs.as(c);
            }
            if (int.class.equals(numType) || Integer.class.equals(numType)) {
                return Fs.as(num.intValue());
            }
            if (long.class.equals(numType) || Long.class.equals(numType)) {
                return Fs.as(num.longValue());
            }
            if (float.class.equals(numType) || Float.class.equals(numType)) {
                return Fs.as(num.floatValue());
            }
            if (double.class.equals(numType) || Double.class.equals(numType)) {
                return Fs.as(num.doubleValue());
            }
            if (BigInteger.class.equals(numType)) {
                if (num instanceof BigDecimal) {
                    BigDecimal decimal = (BigDecimal) num;
                    return Fs.as(decimal.toBigInteger());
                }
                return Fs.as(new BigInteger(num.toString()));
            }
            if (BigDecimal.class.equals(numType)) {
                if (num instanceof BigInteger) {
                    BigInteger integer = (BigInteger) num;
                    return Fs.as(new BigDecimal(integer));
                }
                return Fs.as(new BigDecimal(num.toString()));
            }
        } catch (Exception e) {
            throw new NumException(e);
        }
        throw new NumException("Failed to convert " + num + " to type: " + numType + ".");
    }

    private NumKit() {
    }
}
