package xyz.sunqian.common.io;

import xyz.sunqian.common.base.CheckKit;

final class IOChecker {

    static void checkOffLen(int range, int off, int len) throws IndexOutOfBoundsException {
        CheckKit.checkOffsetLength(range, off, len);
    }

    static void checkStartEnd(int range, int start, int end) throws IndexOutOfBoundsException {
        CheckKit.checkStartEnd(range, start, end);
    }

    static void checkBufSize(int bufSize) throws IllegalArgumentException {
        CheckKit.checkArgument(bufSize > 0, "bufSize must > 0.");
    }

    static void checkSize(int size) throws IllegalArgumentException {
        CheckKit.checkArgument(size > 0, "size must > 0.");
    }

    static void checkLen(int len) throws IllegalArgumentException {
        CheckKit.checkArgument(len >= 0, "len must >= 0.");
    }

    static void checkLen(long len) throws IllegalArgumentException {
        CheckKit.checkArgument(len >= 0, "len must >= 0.");
    }

    static void checkSkip(long skip) throws IllegalArgumentException {
        CheckKit.checkArgument(skip >= 0, "skip value must >= 0.");
    }

    static void checkLimit(long limit) throws IllegalArgumentException {
        CheckKit.checkArgument(limit >= 0, "limit must >= 0.");
    }

    static void checkReadLimit(long readLimit) throws IllegalArgumentException {
        CheckKit.checkArgument(readLimit >= 0, "readLimit must >= 0.");
    }

    static void checkReadBlockSize(long readBlockSize) throws IllegalArgumentException {
        CheckKit.checkArgument(readBlockSize > 0, "readBlockSize must > 0.");
    }

    static void checkSeek(long seek) throws IllegalArgumentException {
        CheckKit.checkArgument(seek >= 0, "seek must >= 0.");
    }

    static void checkCapacity(int capacity) throws IllegalArgumentException {
        CheckKit.checkArgument(capacity >= 0, "capacity must >= 0");
    }
}
