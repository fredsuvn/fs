package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;

final class JsonBack {

    static @Nonnull JsonFormatter defaultFormatter() {
        return DefaultJsonFormatter.INST;
    }

    private JsonBack() {
    }
}