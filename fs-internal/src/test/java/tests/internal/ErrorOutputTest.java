package tests.internal;

import internal.test.ErrorAppender;
import internal.test.ErrorOutputStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorOutputTest {

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
