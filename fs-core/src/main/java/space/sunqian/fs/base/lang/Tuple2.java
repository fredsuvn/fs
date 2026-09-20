package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.util.Objects;

/**
 * Represents a two-element tuple: {@code [t0, t1]}. A new instance can be created using {@link #of(Object, Object)},
 * and each element can be {@code null}.
 * <p>
 * This class is structurally immutable, but the elements themselves may still be mutable. The mutable version is
 * {@link MTuple2}.
 *
 * @param <T0> the type of the first element
 * @param <T1> the type of the second element
 * @author sunqian
 */
@Immutable
public final class Tuple2<T0, T1> {

    /**
     * Creates a new two-element tuple with the given elements.
     *
     * @param <T0> the type of the first element
     * @param <T1> the type of the second element
     * @param t0   the first element
     * @param t1   the second element
     * @return a new two-element tuple
     */
    public static <T0, T1> @Nonnull Tuple2<T0, T1> of(T0 t0, T1 t1) {
        return new Tuple2<>(t0, t1);
    }

    private final T0 t0;
    private final T1 t1;

    private Tuple2(T0 t0, T1 t1) {
        this.t0 = t0;
        this.t1 = t1;
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
     * Returns {@code true} if the given object is an instance of {@code Tuple2} and the elements at corresponding
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
        if (!(o instanceof Tuple2<?, ?>)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        Tuple2<?, ?> other = (Tuple2<?, ?>) o;
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
}
