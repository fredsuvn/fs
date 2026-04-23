package space.sunqian.fs.base.string;

import space.sunqian.annotation.Nonnull;

final class NameMapperBack {

    static @Nonnull NameMapper KEEP = name -> name;

    private NameMapperBack() {
    }
}
