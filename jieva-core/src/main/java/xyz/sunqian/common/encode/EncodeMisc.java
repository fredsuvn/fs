package xyz.sunqian.common.encode;

final class EncodeMisc {

    static void checkEncodingRemaining(int srcRemaining, int dstRemaining) {
        if (srcRemaining > dstRemaining) {
            throw new EncodingException("Remaining space of destination for encoding is not enough.");
        }
    }

    static void checkDecodingRemaining(int srcRemaining, int dstRemaining) {
        if (srcRemaining > dstRemaining) {
            throw new DecodingException("Remaining space of destination for decoding is not enough.");
        }
    }
}
