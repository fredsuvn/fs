package xyz.sunqian.common.base.option;

import xyz.sunqian.annotations.Nullable;

final class OptionBack {

    static Option<?, ?>[] EMPTY_OPTIONS = new Option<?, ?>[0];

    static <K, V> Option<K, V> of(K key, V value) {
        return new OptionImpl<>(key, value);
    }

    private static final class OptionImpl<K, V> implements Option<K, V> {

        private final K key;
        private final @Nullable V value;

        private OptionImpl(K key, @Nullable V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public @Nullable V value() {
            return value;
        }
    }
}
