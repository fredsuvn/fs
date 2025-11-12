package tests.utils;

import space.sunqian.annotations.Nonnull;

import java.util.AbstractMap;
import java.util.Set;

public class ErrorMap<K, V> extends AbstractMap<K, V> {
    @Override
    public @Nonnull Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
