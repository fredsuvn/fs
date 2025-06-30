package xyz.sunqian.common.io;

import xyz.sunqian.common.base.JieCheck;

public class IOChecker {

    static void checkOffLen(int range, int off, int len) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(range, off, len);
    }

    static void checkStartEnd(int range, int start, int end) throws IndexOutOfBoundsException {
        JieCheck.checkStartEnd(range, start, end);
    }

    static void checkBufSize(int bufSize) throws IllegalArgumentException {
        JieCheck.checkArgument(bufSize > 0, "bufSize must > 0.");
    }

    static void checkSize(int size) throws IllegalArgumentException {
        JieCheck.checkArgument(size > 0, "size must > 0.");
    }

    static void checkLen(int len) throws IllegalArgumentException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
    }

    static void checkLen(long len) throws IllegalArgumentException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
    }

    static void checkSkip(long skip) throws IllegalArgumentException {
        JieCheck.checkArgument(skip >= 0, "skip value must >= 0.");
    }

    static void checkLimit(long limit) throws IllegalArgumentException {
        JieCheck.checkArgument(limit >= 0, "limit must >= 0.");
    }

    static void checkReadLimit(long readLimit) throws IllegalArgumentException {
        JieCheck.checkArgument(readLimit >= 0, "readLimit must >= 0.");
    }

    static void checkReadBlockSize(long readBlockSize) throws IllegalArgumentException {
        JieCheck.checkArgument(readBlockSize > 0, "readBlockSize must > 0.");
    }

    static void checkSeek(long seek) throws IllegalArgumentException {
        JieCheck.checkArgument(seek >= 0, "seek must >= 0.");
    }

    static void checkCapacity(int capacity) throws IllegalArgumentException {
        JieCheck.checkArgument(capacity >= 0, "capacity must >= 0");
    }
}
