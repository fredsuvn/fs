package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.ValueClass;

import java.util.Objects;

/**
 * Span consists of a {@code startIndex} inclusive and a {@code endIndex} exclusive, represents a range of a string,
 * list or other range-able object. The {@link #equals(Object)} and {@link #hashCode()} are overridden to compare the
 * {@code startIndex} and {@code endIndex}.
 *
 * @author sunqian
 */
@ValueClass
@Immutable
public final class Span {

    /**
     * Returns an instance of {@link Span} with the given start index and end index.
     *
     * @param startIndex the given start index
     * @param endIndex   the given end index
     * @return an instance of {@link Span} with the given start index and end index
     */
    public static @Nonnull Span of(int startIndex, int endIndex) {
        return new Span(startIndex, endIndex);
    }

    private final int startIndex;
    private final int endIndex;

    private Span(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Returns the start index, inclusive.
     *
     * @return the start index, inclusive
     */
    public int startIndex() {
        return startIndex;
    }

    /**
     * Returns the end index, exclusive.
     *
     * @return the end index, exclusive
     */
    public int endIndex() {
        return endIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Span)) {
            return false;
        }
        Span span = (Span) o;
        return startIndex == span.startIndex && endIndex == span.endIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startIndex, endIndex);
    }

    @Override
    public String toString() {
        return "[" + startIndex + ", " + endIndex + ")";
    }
}
