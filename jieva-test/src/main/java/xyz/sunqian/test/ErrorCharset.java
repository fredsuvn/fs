package xyz.sunqian.test;

import org.jetbrains.annotations.NotNull;
import xyz.sunqian.annotations.Nonnull;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Error charset always throws exception for encoding/decoding operations.
 *
 * @author sunqian
 */
public class ErrorCharset extends Charset {

    /**
     * Returns the singleton instance of this charset.
     */
    public static final @Nonnull Charset SINGLETON = new ErrorCharset("TEST-ERROR", new String[]{});

    private ErrorCharset(@NotNull String canonicalName, String[] aliases) {
        super(canonicalName, aliases);
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        throw new UnsupportedOperationException("Can not decode.");
    }

    @Override
    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException("Can not encode.");
    }
}
