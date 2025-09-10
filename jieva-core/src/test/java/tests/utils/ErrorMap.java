package tests.utils;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Set;

public class ErrorMap<K, V> extends AbstractMap<K, V> {
    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
