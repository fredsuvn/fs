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

    private static final @Nonnull Span EMPTY = Span.of(0, 0);

    /**
     * Returns an instance of {@link Span} with the given start index and end index.
     *
     * @param startIndex the given start index
     * @param endIndex   the given end index
     * @return an instance of {@link Span} with the given start index and end index
     * @throws IllegalArgumentException if {@code startIndex > endIndex}
     */
    public static @Nonnull Span of(int startIndex, int endIndex) throws IllegalArgumentException {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("startIndex must <= endIndex");
        }
        return new Span(startIndex, endIndex);
    }

    /**
     * Returns an instance of {@link Span} of which {@code startIndex} and {@code endIndex} are both {@code 0}.
     *
     * @return an instance of {@link Span} of which {@code startIndex} and {@code endIndex} are both {@code 0}
     */
    public static @Nonnull Span empty() {
        return EMPTY;
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

    /**
     * Returns {@code true} if {@code startIndex == endIndex}, otherwise {@code false}.
     *
     * @return {@code true} if {@code startIndex == endIndex}, otherwise {@code false}
     */
    public boolean isEmpty() {
        return startIndex == endIndex;
    }

    /**
     * Returns the result of {@code endIndex - startIndex}.
     *
     * @return the result of {@code endIndex - startIndex}
     */
    public int length() {
        return endIndex - startIndex;
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
