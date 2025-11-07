package space.sunqian.common.io;

import space.sunqian.common.base.CheckKit;

final class IOChecker {

    static void checkOffLen(int off, int len, int capacity) throws IndexOutOfBoundsException {
        CheckKit.checkOffLen(off, len, capacity);
    }

    static void checkStartEnd(int start, int end, int capacity) throws IndexOutOfBoundsException {
        CheckKit.checkStartEnd(start, end, capacity);
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

    static EndChecker endChecker() {
        return EndChecker.INST;
    }

    static AvailableChecker availableChecker() {
        return AvailableChecker.INST;
    }

    interface ReadChecker {

        boolean readEnd(int readSize);

        int actualCount(int lastReadSize, int count);

        long actualCount(int lastReadSize, long count);
    }

    enum EndChecker implements ReadChecker {

        INST;

        @Override
        public boolean readEnd(int readSize) {
            return readSize < 0;
        }

        @Override
        public int actualCount(int lastReadSize, int count) {
            return count == 0 ? -1 : count;
        }

        @Override
        public long actualCount(int lastReadSize, long count) {
            return count == 0 ? -1 : count;
        }
    }

    enum AvailableChecker implements ReadChecker {

        INST;

        @Override
        public boolean readEnd(int readSize) {
            return readSize <= 0;
        }

        @Override
        public int actualCount(int lastReadSize, int count) {
            return count == 0 ? (lastReadSize < 0 ? -1 : 0) : count;
        }

        @Override
        public long actualCount(int lastReadSize, long count) {
            return count == 0 ? (lastReadSize < 0 ? -1 : 0) : count;
        }
    }

    private IOChecker() {
    }
}
