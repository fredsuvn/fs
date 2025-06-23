package xyz.sunqian.common.io;

final class IOChecker {

    static void checkSize(int size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("The size must be >= 0.");
        }
    }

    static void checkSize(long size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("The size must be >= 0.");
        }
    }
}
