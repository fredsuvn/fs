package tests;

import org.testng.annotations.Test;
import internal.test.ErrorCharset;

import java.nio.charset.Charset;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.expectThrows;

public class ErrorCharsetTest {

    @Test
    public void testErrorCharset() throws Exception {
        Charset err = ErrorCharset.SINGLETON;
        expectThrows(UnsupportedOperationException.class, () -> "hello".getBytes(err));
        expectThrows(UnsupportedOperationException.class, () -> new String(new byte[10], err));
        assertFalse(err.contains(err));
    }
}
