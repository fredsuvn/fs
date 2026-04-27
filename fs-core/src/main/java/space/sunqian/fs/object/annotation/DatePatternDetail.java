package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.ConvertOption;

import java.time.ZoneId;

/**
 * The detail info of the {@link DatePattern} annotation.
 *
 * @author sunqian
 */
public final class DatePatternDetail implements AnnotationDetail<DatePattern> {

    private final @Nonnull DatePattern pattern;
    private final @Nonnull ZoneId zoneId;
    private final @Nonnull DateFormatter formatter;
    private final @Nonnull Option<?, ?> option;

    /**
     * Constructs with the specified instance of the {@link DatePattern} annotation.
     *
     * @param pattern the specified instance of the {@link DatePattern} annotation
     */
    public DatePatternDetail(@Nonnull DatePattern pattern) {
        this.pattern = pattern;
        this.zoneId = pattern.zoneId().isEmpty() ? ZoneId.systemDefault() : ZoneId.of(pattern.zoneId());
        this.formatter = DateFormatter.ofPattern(pattern.value(), zoneId);
        this.option = Option.of(ConvertOption.DATE_FORMATTER, formatter);
    }

    /**
     * Returns the instance of the {@link DatePattern} annotation.
     *
     * @return the instance of the {@link DatePattern} annotation
     */
    @Override
    public @Nonnull DatePattern annotation() {
        return pattern;
    }

    /**
     * Returns the zone ID parsed from the {@link DatePattern} annotation.
     *
     * @return the zone ID parsed from the {@link DatePattern} annotation
     */
    public @Nonnull ZoneId zoneId() {
        return zoneId;
    }

    /**
     * Returns the formatter parsed from the {@link DatePattern} annotation.
     *
     * @return the formatter parsed from the {@link DatePattern} annotation
     */
    public @Nonnull DateFormatter formatter() {
        return formatter;
    }

    /**
     * Returns an {@link Option} instance of which key is {@link ConvertOption#DATE_FORMATTER}, and value is
     * {@link #formatter()}.
     *
     * @return an {@link Option} instance of which key is {@link ConvertOption#DATE_FORMATTER}, and value is
     * {@link #formatter()}
     */
    public @Nonnull Option<?, ?> option() {
        return option;
    }
}
