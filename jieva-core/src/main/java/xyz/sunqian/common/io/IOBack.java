package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;

final class IOBack {

    static CharSequence nonNullChars(@Nullable CharSequence csq) {
        return csq == null ? "null" : csq;
    }
}
