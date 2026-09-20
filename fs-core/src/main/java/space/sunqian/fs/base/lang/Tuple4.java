package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.util.Objects;

/**
 * Represents a four-element tuple: {@code [t0, t1, t2, t3]}. A new instance can be created using
 * {@link #of(Object, Object, Object, Object)}, and each element can be {@code null}.
 * <p>
 * This class is structurally immutable, but the elements themselves may still be mutable. The mutable version is
 * {@link MTuple4}.
 *
 * @param <T0> the type of the first element
 * @param <T1> the type of the second element
 * @param <T2> the type of the third element
 * @param <T3> the type of the fourth element
 * @author sunqian
 */
@Immutable
public final class Tuple4<T0, T1, T2, T3> {

    /**
     * Creates a new four-element tuple with the given elements.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @param <T2> the type of the third element
     * @param <T3> the type of the fourth element
     * @param t0   the first element
     * @param t1   the second element
     * @param t2   the third element
     * @param t3   the fourth element
     * @return a new four-element tuple
     */
    public static <T0, T1, T2, T3> @Nonnull Tuple4<T0, T1, T2, T3> of(T0 t0, T1 t1, T2 t2, T3 t3) {
        return new Tuple4<>(t0, t1, t2, t3);
    }

    private final T0 t0;
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;

    private Tuple4(T0 t0, T1 t1, T2 t2, T3 t3) {
        this.t0 = t0;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
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
     * Returns the second element.
     *
     * @return the second element
     */
    public T1 get1() {
        return t1;
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
     * Returns the fourth element.
     *
     * @return the fourth element
     */
    public T3 get3() {
        return t3;
    }

    /**
     * Returns {@code true} if the given object is an instance of {@code Tuple4} and the elements at corresponding
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
        if (!(o instanceof Tuple4<?, ?, ?, ?>)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        Tuple4<?, ?, ?, ?> other = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(t0, other.t0)
            && Objects.equals(t1, other.t1)
            && Objects.equals(t2, other.t2)
            && Objects.equals(t3, other.t3);
    }

    /**
     * Returns the hash code value for this tuple. The hash code is computed as follows:
     * <pre>
     *     Objects.hashCode(t0) * 31 + Objects.hashCode(t1) * 31 + Objects.hashCode(t2) * 31 + Objects.hashCode(t3)
     * </pre>
     *
     * @return the hash code value for this tuple
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(t0) * 31 + Objects.hashCode(t1) * 31 + Objects.hashCode(t2) * 31 + Objects.hashCode(t3);
    }

    /**
     * Returns a string representation of this tuple.
     * <p>
     * The string representation has the form {@code [t0, t1, t2, t3]}, where {@code t0}, {@code t1}, {@code t2}, and
     * {@code t3} are the first, second, third, and fourth elements, respectively.
     *
     * @return the string representation of this tuple
     */
    @Override
    public String toString() {
        return "[" + t0 + ", " + t1 + ", " + t2 + ", " + t3 + "]";
    }
}
