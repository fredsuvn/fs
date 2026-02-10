package space.sunqian.fs.base.option;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.Objects;

final class OptionImpl<K, V> implements Option<K, V> {

    static final @Nonnull Option<?, ?> @Nonnull [] EMPTY_OPTIONS = new Option<?, ?>[0];

    private final @Nonnull K key;
    private final @Nullable V value;

    OptionImpl(@Nonnull K key, @Nullable V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public @Nonnull K key() {
        return key;
    }

    @Override
    public @Nullable V value() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Option)) {
            return false;
        }
        Option<?, ?> other = (Option<?, ?>) object;
        return Objects.equals(key, other.key()) && Objects.equals(value, other.value());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public @Nonnull String toString() {
        return "[" + key + ": " + value + "]";
    }
}
