package xyz.sunqian.test;

import xyz.sunqian.annotations.Nonnull;

import java.util.HashMap;
import java.util.Map;

final class ThreadLocals {

    private static final @Nonnull ThreadLocal<Map<Key, Object>> localMap = new ThreadLocal<Map<Key, Object>>() {
        @Override
        protected @Nonnull Map<Key, Object> initialValue() {
            return new HashMap<>();
        }
    };

    static void set(@Nonnull Key key, @Nonnull Object value) {
        localMap.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    static <T> T get(@Nonnull Key key) {
        return (T) localMap.get().get(key);
    }

    static void remove(@Nonnull Key key) {
        localMap.get().remove(key);
    }

    enum Key {

        PRINTER,

        ;
    }
}
