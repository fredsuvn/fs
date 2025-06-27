package xyz.sunqian.common.io;

import xyz.sunqian.common.base.JieCheck;

public class IOChecker {

    static void checkOffLen(int range, int off, int len) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(range, off, len);
    }

    static void checkStartEnd(int range, int start, int end) throws IndexOutOfBoundsException {
        JieCheck.checkStartEnd(range, start, end);
    }

    static void checkBufSize(int bufSize) {
        JieCheck.checkArgument(bufSize > 0, "bufSize must > 0.");
    }

    static void checkSize(int size) {
        JieCheck.checkArgument(size > 0, "size must > 0.");
    }

    static void checkLen(int len) {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
    }

    static void checkLen(long len) {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
    }

    static void checkLimit(long limit) {
        JieCheck.checkArgument(limit >= 0, "limit must >= 0.");
    }

    static void checkSeek(long seek) {
        JieCheck.checkArgument(seek >= 0, "seek must >= 0.");
    }

    static void checkCapacity(int capacity) {
        JieCheck.checkArgument(capacity >= 0, "capacity must >= 0");
    }
}
