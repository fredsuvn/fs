package xyz.sunqian.common.io;

final class IOMisc {

    static void checkReadBounds(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
    }

    static void checkReadBounds(char[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
    }

    static void checkReadBounds(CharSequence chars, int off, int len) {
        if (chars == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > chars.length() - off) {
            throw new IndexOutOfBoundsException();
        }
    }
}
