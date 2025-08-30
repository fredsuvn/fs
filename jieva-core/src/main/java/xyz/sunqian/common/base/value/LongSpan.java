package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.ValueClass;

import java.util.Objects;

/**
 * LongSpan is {@code long} version of {@link Span}, consists of a {@code startIndex} inclusive and a {@code endIndex}
 * exclusive, represents a range with {@code long} type. The {@link #equals(Object)} and {@link #hashCode()} are
 * overridden to compare the {@code startIndex} and {@code endIndex}.
 *
 * @author sunqian
 */
@ValueClass
@Immutable
public final class LongSpan {

    /**
     * Returns an instance of {@link LongSpan} with the given start index and end index.
     *
     * @param startIndex the given start index
     * @param endIndex   the given end index
     * @return an instance of {@link LongSpan} with the given start index and end index
     */
    public static @Nonnull LongSpan of(long startIndex, long endIndex) {
        return new LongSpan(startIndex, endIndex);
    }

    private final long startIndex;
    private final long endIndex;

    private LongSpan(long startIndex, long endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * Returns the start index, inclusive.
     *
     * @return the start index, inclusive
     */
    public long startIndex() {
        return startIndex;
    }

    /**
     * Returns the end index, exclusive.
     *
     * @return the end index, exclusive
     */
    public long endIndex() {
        return endIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LongSpan)) {
            return false;
        }
        LongSpan span = (LongSpan) o;
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
