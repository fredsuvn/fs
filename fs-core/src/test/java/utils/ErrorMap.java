package utils;

import space.sunqian.annotation.Nonnull;

import java.util.AbstractMap;
import java.util.Set;

public class ErrorMap<K, V> extends AbstractMap<K, V> {
    @Override
    public @Nonnull Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
