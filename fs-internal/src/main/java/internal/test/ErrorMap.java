package internal.test;

import space.sunqian.annotation.Nonnull;

import java.util.AbstractMap;
import java.util.Set;

/**
 * A map implementation that always throws {@link UnsupportedOperationException} for all operations.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class ErrorMap<K, V> extends AbstractMap<K, V> {
    @Override
    public @Nonnull Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
