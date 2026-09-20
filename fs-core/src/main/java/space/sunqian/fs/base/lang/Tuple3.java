package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.util.Objects;

/**
 * Represents a three-element tuple: {@code [t0, t1, t2]}. A new instance can be created using
 * {@link #of(Object, Object, Object)}, and each element can be {@code null}.
 * <p>
 * This class is structurally immutable, but the elements themselves may still be mutable. The mutable version is
 * {@link MTuple3}.
 *
 * @param <T0> the type of the first element
 * @param <T1> the type of the second element
 * @param <T2> the type of the third element
 * @author sunqian
 */
@Immutable
public final class Tuple3<T0, T1, T2> {

    /**
     * Creates a new three-element tuple with the given elements.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @param <T2> the type of the third element
     * @param t0   the first element
     * @param t1   the second element
     * @param t2   the third element
     * @return a new three-element tuple
     */
    public static <T0, T1, T2> @Nonnull Tuple3<T0, T1, T2> of(T0 t0, T1 t1, T2 t2) {
        return new Tuple3<>(t0, t1, t2);
    }

    private final T0 t0;
    private final T1 t1;
    private final T2 t2;

    private Tuple3(T0 t0, T1 t1, T2 t2) {
        this.t0 = t0;
        this.t1 = t1;
        this.t2 = t2;
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
     * Returns {@code true} if the given object is an instance of {@code Tuple3} and the elements at corresponding
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
        if (!(o instanceof Tuple3<?, ?, ?>)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) o;
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
}
