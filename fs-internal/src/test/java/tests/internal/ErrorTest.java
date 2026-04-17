package tests.internal;

import internal.utils.ErrorAppender;
import internal.utils.ErrorCharset;
import internal.utils.ErrorList;
import internal.utils.ErrorMap;
import internal.utils.ErrorNumber;
import internal.utils.ErrorOutputStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorTest {

    @Test
    public void testErrorCharset() throws Exception {
        Charset err = ErrorCharset.SINGLETON;
        assertThrows(UnsupportedOperationException.class, () -> "hello".getBytes(err));
        assertThrows(UnsupportedOperationException.class, () -> new String(new byte[10], err));
        assertFalse(err.contains(err));
    }

    @Test
    public void testErrorCollection() throws Exception {
        // Test ErrorList
        assertThrows(UnsupportedOperationException.class, () -> new ErrorList<>().get(1));
        assertThrows(UnsupportedOperationException.class, () -> new ErrorList<>().size());

        // Test ErrorMap
        assertThrows(UnsupportedOperationException.class, () -> new ErrorMap<>().get("key"));
        assertThrows(UnsupportedOperationException.class, () -> new ErrorMap<>().size());
    }

    @Test
    public void testErrorNumber() throws Exception {
        ErrorNumber errorNumber = new ErrorNumber();
        assertThrows(UnsupportedOperationException.class, errorNumber::intValue);
        assertThrows(UnsupportedOperationException.class, errorNumber::longValue);
        assertThrows(UnsupportedOperationException.class, errorNumber::floatValue);
        assertThrows(UnsupportedOperationException.class, errorNumber::doubleValue);
    }

    @Test
    public void testErrorOutputStream() throws Exception {
        OutputStream out = new ErrorOutputStream();
        assertThrows(IOException.class, () -> out.write(new byte[1]));
        assertThrows(IOException.class, () -> out.write(new byte[1], 0, 1));
        assertThrows(IOException.class, () -> out.write(1));
        assertThrows(IOException.class, out::flush);
        assertThrows(IOException.class, out::close);
    }

    @Test
    public void testErrorAppender() throws Exception {
        Appendable out = new ErrorAppender();
        assertThrows(IOException.class, () -> out.append(""));
        assertThrows(IOException.class, () -> out.append("1", 0, 1));
        assertThrows(IOException.class, () -> out.append('c'));
    }
}
