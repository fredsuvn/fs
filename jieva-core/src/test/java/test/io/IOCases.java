package test.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class IOCases {

    public void testInputStream(InputStream in, byte[] source, boolean available) throws Exception {
        byte[] dest = new byte[source.length];
        assertEquals(in.read(dest, 0, 0), 0);

        //test read()
        if (source.length > 0) {
            assertEquals((byte) in.read(), source[0]);
            if (source.length > 1) {
                assertEquals((byte) in.read(), source[1]);
                if (source.length > 2) {
                    assertEquals((byte) in.read(), source[2]);
                }
            }
        }

        // test read(byte[], int, int)
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 3, 13));


        if (available) {
            assertEquals(in.available(), source.length - 13);
        } else {
            assertTrue(in.available() <= source.length - 13 && in.available() >= 0);
        }
        if (in.markSupported()) {
            expectThrows(IOException.class, in::reset);
            in.mark(999);
            assertEquals(in.read(dest, 3, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
            in.reset();
        }
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(source.length), source.length - 23);
        assertEquals(in.read(), -1);
        assertEquals(in.read(dest, 3, 10), -1);
        assertEquals(in.skip(source.length), 0);

        in.close();
    }
}
