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
public class NumberKit {

    /**
     * Default format pattern: "#.00".
     */
    public static final @Nonnull String DEFAULT_PATTERN = "#.00";

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
     * @throws NumberException if any error occurs during the conversion
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull CharSequence str, Class<T> numType
    ) throws NumberException {
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
            throw new NumberException(e);
        }
        throw new NumberException("Failed to convert " + str + " to " + numType + ".");
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
     * @throws NumberException if any error occurs during the conversion
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull Number num, Class<T> numType
    ) throws NumberException {
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
                    @SuppressWarnings("PatternVariableCanBeUsed")
                    BigDecimal decimal = (BigDecimal) num;
                    return Fs.as(decimal.toBigInteger());
                }
                return Fs.as(new BigInteger(num.toString()));
            }
            if (BigDecimal.class.equals(numType)) {
                if (num instanceof BigInteger) {
                    @SuppressWarnings("PatternVariableCanBeUsed")
                    BigInteger integer = (BigInteger) num;
                    return Fs.as(new BigDecimal(integer));
                }
                return Fs.as(new BigDecimal(num.toString()));
            }
        } catch (Exception e) {
            throw new NumberException(e);
        }
        throw new NumberException("Failed to convert " + num + " to type: " + numType + ".");
    }

    /**
     * Returns a {@link Number} object parsed from the given char sequence. The actual type of the object may be
     * {@link Integer}, {@link Long}, {@link BigInteger} or {@link BigDecimal}.
     *
     * @param cs the given char sequence
     * @return a {@link Number} object parsed from the given char sequence
     * @throws NumberException if any error occurs during the parsing
     */
    public static @Nonnull Number toNumber(@Nonnull CharSequence cs) throws NumberException {
        return NumberService.INST.toNumber(cs);
    }

    /**
     * Returns a {@link Number} object parsed from the given char sequence from the specified start index inclusive to
     * the specified end index exclusive. The actual type of the object may be {@link Integer}, {@link Long},
     * {@link BigInteger} or {@link BigDecimal}.
     *
     * @param cs    the given char sequence
     * @param start the specified start index, inclusive
     * @param end   the specified end index, exclusive
     * @return a {@link Number} object parsed from the given char sequence
     * @throws NumberException if any error occurs during the parsing
     */
    public static @Nonnull Number toNumber(@Nonnull CharSequence cs, int start, int end) throws NumberException {
        return NumberService.INST.toNumber(cs, start, end);
    }

    private NumberKit() {
    }
}
