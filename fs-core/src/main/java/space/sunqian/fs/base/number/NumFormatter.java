package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Supplier;

/**
 * Formatter for number. The implementation of this interface is immutable and thread-safe.
 *
 * @author sunqian
 */
public interface NumFormatter {

    /**
     * Returns a common {@link NumFormatter} which uses {@link Number#toString()} to format the number and
     * {@link NumKit#toNumber(CharSequence, Class)} to parse the string. Note the returned instance is singleton.
     *
     * @return a common {@link NumFormatter} which uses {@link Number#toString()} to format the number and
     * {@link NumKit#toNumber(CharSequence, Class)} to parse the string
     */
    static @Nonnull NumFormatter common() {
        return NumFormatterImpl.Common.INST;
    }

    /**
     * Returns a new {@link NumFormatter} with the given number format supplier.
     *
     * @param format the number format supplier
     * @return a new {@link NumFormatter} with the given number format supplier
     */
    static @Nonnull NumFormatter of(@Nonnull Supplier<? extends @Nonnull NumberFormat> format) {
        return new NumFormatterImpl(format);
    }

    /**
     * Returns a new {@link NumFormatter} with the given number format pattern.
     * <p>
     * By default, this method uses {@link DecimalFormat} and {@link ThreadLocal} to support multi-threading.
     *
     * @param pattern the number format pattern
     * @return a new {@link NumFormatter} with the given number format pattern
     */
    static @Nonnull NumFormatter of(@Nonnull String pattern) {
        ThreadLocal<DecimalFormat> format = ThreadLocal.withInitial(() -> new DecimalFormat(pattern));
        return of(format::get);
    }

    /**
     * Formats the given number.
     *
     * @param num the given number to format
     * @return the formatted string
     * @throws NumException if any error occurs
     */
    @Nonnull
    String format(@Nonnull Number num) throws NumException;

    /**
     * Formats the given number. If the given number is {@code null}, or an exception thrown during formating, returns
     * {@code null}.
     *
     * @param num the given number to format, can be {@code null}
     * @return the formatted string, or {@code null} if the given number is {@code null} or an exception thrown
     * @throws NumException if any error occurs
     */
    default @Nullable String formatSafe(@Nullable Number num) throws NumException {
        if (num == null) {
            return null;
        }
        try {
            return format(num);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses the given number string to an instance of the specified number type.
     *
     * @param numStr  the given number string to parse
     * @param numType the specified number type
     * @param <T>     the number type
     * @return the parsed number instance
     * @throws NumException if any error occurs
     */
    <T> @Nonnull T parse(@Nonnull CharSequence numStr, @Nonnull Class<T> numType) throws NumException;

    /**
     * Parses the given number string to an instance of the specified number type. If the given number string is
     * {@code null}, or an exception thrown during parsing, returns {@code null}.
     *
     * @param numStr  the given number string to parse, can be {@code null}
     * @param numType the specified number type
     * @param <T>     the number type
     * @return the parsed number instance, or {@code null} if the given number string is {@code null} or an exception
     * thrown
     * @throws NumException if any error occurs
     */
    default <T> @Nullable T parseSafe(
        @Nullable CharSequence numStr, @Nonnull Class<T> numType
    ) throws NumException {
        if (numStr == null) {
            return null;
        }
        try {
            return parse(numStr, numType);
        } catch (Exception e) {
            return null;
        }
    }
}
