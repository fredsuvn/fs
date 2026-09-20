package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.util.Objects;

/**
 * Represents a mutable two-element tuple: {@code [t0, t1]}. A new instance can be created using
 * {@link #of(Object, Object)} or {@link #newTuple()}, and each element can be {@code null}.
 * <p>
 * This class is mutable, and the immutable version is {@link Tuple2}.
 *
 * @param <T0> the type of the first element
 * @param <T1> the type of the second element
 * @author sunqian
 */
public final class MTuple2<T0, T1> implements Cloneable {

    /**
     * Creates a new mutable two-element tuple with the given elements.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @param t0   the first element
     * @param t1   the second element
     * @return a new two-element tuple
     */
    public static <T0, T1> @Nonnull MTuple2<T0, T1> of(T0 t0, T1 t1) {
        return new MTuple2<>(t0, t1);
    }

    /**
     * Creates a new mutable empty two-element tuple, and each element is {@code null}.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @return a new empty two-element tuple
     */
    public static <T0, T1> @Nonnull MTuple2<T0, T1> newTuple() {
        return new MTuple2<>();
    }

    private T0 t0;
    private T1 t1;

    private MTuple2(T0 t0, T1 t1) {
        this.t0 = t0;
        this.t1 = t1;
    }

    private MTuple2() {
    }

    /**
     * Returns the first element.
     *
     * @return the first element
     */
    public T0 get0() {
        return t0;
    }

    /**
     * Sets the first element.
     *
     * @param t0 the first element
     * @return this tuple itself
     */
    public @Nonnull MTuple2<T0, T1> set0(T0 t0) {
        this.t0 = t0;
        return this;
    }

    /**
     * Returns the second element.
     *
     * @return the second element
     */
    public T1 get1() {
        return t1;
    }

    /**
     * Sets the second element.
     *
     * @param t1 the second element
     * @return this tuple itself
     */
    public @Nonnull MTuple2<T0, T1> set1(T1 t1) {
        this.t1 = t1;
        return this;
    }

    /**
     * Returns {@code true} if the given object is an instance of {@code MTuple2} and the elements at corresponding
     * positions are equal according to {@link Objects#equals(Object, Object)}. Otherwise, returns {@code false}.
     *
     * @param o the given object to be compared
     * @return whether the given object is equal to this tuple
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MTuple2<?, ?>)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        MTuple2<?, ?> other = (MTuple2<?, ?>) o;
        return Objects.equals(t0, other.t0) && Objects.equals(t1, other.t1);
    }

    /**
     * Returns the hash code value for this tuple. The hash code is computed as follows:
     * <pre>
     *     Objects.hashCode(t0) * 31 + Objects.hashCode(t1)
     * </pre>
     *
     * @return the hash code value for this tuple
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(t0) * 31 + Objects.hashCode(t1);
    }

    /**
     * Returns a string representation of this tuple.
     * <p>
     * The string representation has the form {@code [t0, t1]}, where {@code t0} and {@code t1} are the first and second
     * elements, respectively.
     *
     * @return the string representation of this tuple
     */
    @Override
    public String toString() {
        return "[" + t0 + ", " + t1 + "]";
    }

    /**
     * Clones this tuple.
     *
     * @return a clone of this tuple
     */
    public @Nonnull MTuple2<T0, T1> clone() {
        MTuple2<T0, T1> clone = Fs.uncheck(() -> Fs.as(super.clone()), CloneException::new);
        clone.t0 = t0;
        clone.t1 = t1;
        return clone;
    }

    /**
     * Returns an immutable copy of this tuple with its current elements.
     *
     * @return an immutable copy of this tuple with its current elements
     */
    public @Nonnull Tuple2<T0, T1> immutable() {
        return Tuple2.of(t0, t1);
    }
}
