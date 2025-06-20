package test;

import org.testng.annotations.Test;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ErrorOutputStream;

import java.io.IOException;
import java.io.OutputStream;

import static org.testng.Assert.expectThrows;

public class ErrorOutputTest {

    @Test
    public void testErrorOutputStream() throws Exception {
        OutputStream out = new ErrorOutputStream();
        expectThrows(IOException.class, () -> out.write(new byte[1]));
        expectThrows(IOException.class, () -> out.write(new byte[1], 0, 1));
        expectThrows(IOException.class, () -> out.write(1));
        expectThrows(IOException.class, out::flush);
        expectThrows(IOException.class, out::close);
    }

    @Test
    public void testErrorAppender() throws Exception {
        Appendable out = new ErrorAppender();
        expectThrows(IOException.class, () -> out.append(""));
        expectThrows(IOException.class, () -> out.append("1", 0, 1));
        expectThrows(IOException.class, () -> out.append('c'));
    }
}
