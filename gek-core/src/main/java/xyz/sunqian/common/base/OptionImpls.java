package xyz.sunqian.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.sunqian.annotations.Nullable;

final class OptionImpls {

    static Option<?, ?>[] EMPTY_OPTIONS = new Option<?, ?>[0];

    static <K, V> Option<K, V> of(K key, V value) {
        return new OptionImpl<>(key, value);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class OptionImpl<K, V> implements Option<K, V> {
        private final K key;
        private final @Nullable V value;
    }
}
