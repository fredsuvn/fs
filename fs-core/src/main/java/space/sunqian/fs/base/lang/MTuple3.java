package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.util.Objects;

/**
 * Represents a mutable three-element tuple: {@code [t0, t1, t2]}. A new instance can be created using
 * {@link #of(Object, Object, Object)} or {@link #newTuple()}, and each element can be {@code null}.
 * <p>
 * This class is mutable, and the immutable version is {@link Tuple3}.
 *
 * @param <T0> the type of the first element
 * @param <T1> the type of the second element
 * @param <T2> the type of the third element
 * @author sunqian
 */
public final class MTuple3<T0, T1, T2> implements Cloneable {

    /**
     * Creates a new mutable three-element tuple with the given elements.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @param <T2> the type of the third element
     * @param t0   the first element
     * @param t1   the second element
     * @param t2   the third element
     * @return a new mutable three-element tuple
     */
    public static <T0, T1, T2> @Nonnull MTuple3<T0, T1, T2> of(T0 t0, T1 t1, T2 t2) {
        return new MTuple3<>(t0, t1, t2);
    }

    /**
     * Creates a new mutable empty three-element tuple, and each element is {@code null}.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @param <T2> the type of the third element
     * @return a new mutable empty three-element tuple
     */
    public static <T0, T1, T2> @Nonnull MTuple3<T0, T1, T2> newTuple() {
        return new MTuple3<>();
    }

    private T0 t0;
    private T1 t1;
    private T2 t2;

    private MTuple3(T0 t0, T1 t1, T2 t2) {
        this.t0 = t0;
        this.t1 = t1;
        this.t2 = t2;
    }

    private MTuple3() {
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
    public @Nonnull MTuple3<T0, T1, T2> set0(T0 t0) {
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
    public @Nonnull MTuple3<T0, T1, T2> set1(T1 t1) {
        this.t1 = t1;
        return this;
    }

    /**
     * Returns the third element.
     *
     * @return the third element
     */
    public T2 get2() {
        return t2;
    }

    /**
     * Sets the third element.
     *
     * @param t2 the third element
     * @return this tuple itself
     */
    public @Nonnull MTuple3<T0, T1, T2> set2(T2 t2) {
        this.t2 = t2;
        return this;
    }

    /**
     * Returns {@code true} if the given object is an instance of {@code MTuple3} and the elements at corresponding
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
        if (!(o instanceof MTuple3<?, ?, ?>)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        MTuple3<?, ?, ?> other = (MTuple3<?, ?, ?>) o;
        return Objects.equals(t0, other.t0) && Objects.equals(t1, other.t1) && Objects.equals(t2, other.t2);
    }

    /**
     * Returns the hash code value for this tuple. The hash code is computed as follows:
     * <pre>
     *     Objects.hashCode(t0) * 31 + Objects.hashCode(t1) * 31 + Objects.hashCode(t2)
     * </pre>
     *
     * @return the hash code value for this tuple
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(t0) * 31 + Objects.hashCode(t1) * 31 + Objects.hashCode(t2);
    }

    /**
     * Returns a string representation of this tuple.
     * <p>
     * The string representation has the form {@code [t0, t1, t2]}, where {@code t0}, {@code t1}, and {@code t2} are the
     * first, second, and third elements, respectively.
     *
     * @return the string representation of this tuple
     */
    @Override
    public String toString() {
        return "[" + t0 + ", " + t1 + ", " + t2 + "]";
    }

    /**
     * Clones this tuple.
     *
     * @return a clone of this tuple
     */
    public @Nonnull MTuple3<T0, T1, T2> clone() {
        MTuple3<T0, T1, T2> clone = Fs.uncheck(() -> Fs.as(super.clone()), CloneException::new);
        clone.t0 = t0;
        clone.t1 = t1;
        clone.t2 = t2;
        return clone;
    }

    /**
     * Returns an immutable copy of this tuple with its current elements.
     *
     * @return an immutable copy of this tuple with its current elements
     */
    public @Nonnull Tuple3<T0, T1, T2> immutable() {
        return Tuple3.of(t0, t1, t2);
    }
}
