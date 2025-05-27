package xyz.sunqian.common.base.option;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

final class OptionBack {

    static final @Nonnull Option<?, ?> @Nonnull [] EMPTY_OPTIONS = new Option<?, ?>[0];

    static <K, V> @Nonnull Option<K, V> of(@Nonnull K key, @Nullable V value) {
        return new OptionImpl<>(key, value);
    }

    private static final class OptionImpl<K, V> implements Option<K, V> {

        private final @Nonnull K key;
        private final @Nullable V value;

        private OptionImpl(@Nonnull K key, @Nullable V value) {
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
    }
}
