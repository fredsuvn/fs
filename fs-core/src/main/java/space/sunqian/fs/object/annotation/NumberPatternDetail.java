package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.number.NumberFormatter;

/**
 * The detail info of the {@link NumberPattern} annotation.
 *
 * @author sunqian
 */
public final class NumberPatternDetail implements AnnotationDetail<NumberPattern> {

    private final @Nonnull NumberPattern pattern;
    private final @Nonnull NumberFormatter formatter;

    /**
     * Constructs with the specified instance of the {@link NumberPattern} annotation.
     *
     * @param pattern the specified instance of the {@link NumberPattern} annotation
     */
    public NumberPatternDetail(@Nonnull NumberPattern pattern) {
        this.pattern = pattern;
        this.formatter = NumberFormatter.ofPattern(pattern.value());
    }

    /**
     * Returns the instance of the {@link NumberPattern} annotation.
     *
     * @return the instance of the {@link NumberPattern} annotation
     */
    @Override
    public @Nonnull NumberPattern annotation() {
        return pattern;
    }

    /**
     * Returns the formatter parsed from the {@link NumberPattern} annotation.
     *
     * @return the formatter parsed from the {@link NumberPattern} annotation
     */
    public @Nonnull NumberFormatter formatter() {
        return formatter;
    }
}
