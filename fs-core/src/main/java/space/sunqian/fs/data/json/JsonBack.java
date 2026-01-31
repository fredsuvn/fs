package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nullable;

final class JsonBack {

    static boolean isNullValue(@Nullable Object value) {
        return value == null || value instanceof JsonNull;
    }

    private JsonBack() {
    }
}
