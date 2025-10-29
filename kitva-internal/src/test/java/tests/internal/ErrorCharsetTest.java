package tests.internal;

import org.junit.jupiter.api.Test;
import internal.test.ErrorCharset;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorCharsetTest {

    @Test
    public void testErrorCharset() throws Exception {
        Charset err = ErrorCharset.SINGLETON;
        assertThrows(UnsupportedOperationException.class, () -> "hello".getBytes(err));
        assertThrows(UnsupportedOperationException.class, () -> new String(new byte[10], err));
        assertFalse(err.contains(err));
    }
}
