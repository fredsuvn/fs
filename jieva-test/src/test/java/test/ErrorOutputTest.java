package test;

import org.testng.annotations.Test;
import xyz.sunqian.test.ErrorOutputStream;

import java.io.IOException;
import java.io.OutputStream;

import static org.testng.Assert.expectThrows;

public class ErrorOutputTest {

    @Test
    public void testErrorOutputStream() throws Exception {
        OutputStream out = new ErrorOutputStream();
        expectThrows(IOException.class, () -> out.write(new byte[22]));
        expectThrows(IOException.class, out::flush);
        expectThrows(IOException.class, out::close);
    }
}
