package test.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class IOCases {

    // source.length >= 43
    // public static void testInput(InputStream in, byte[] source, boolean available) throws Exception {
    //     byte[] dest = new byte[source.length];
    //     assertEquals(in.read(dest, 0, 0), 0);
    //
    //     // test read(): 3
    //     assertEquals((byte) in.read(), source[0]);
    //     assertEquals((byte) in.read(), source[1]);
    //     assertEquals((byte) in.read(), source[2]);
    //
    //     int hasRead = 3;
    //
    //     // test read(byte[], int, int): 10
    //     assertEquals(in.read(dest, 5, 10), 10);
    //     assertEquals(Arrays.copyOfRange(dest, 5, 15), Arrays.copyOfRange(source, hasRead, hasRead + 10));
    //     hasRead += 10;
    //
    //     // test available()
    //     if (available) {
    //         assertEquals(in.available(), source.length - hasRead);
    //     } else {
    //         assertTrue(in.available() <= source.length - hasRead && in.available() >= 0);
    //     }
    //
    //     // test mark/reset: 10
    //     if (in.markSupported()) {
    //         expectThrows(IOException.class, in::reset);
    //         in.mark(0);
    //         assertEquals(in.read(dest, 3, 10), 10);
    //         assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, hasRead, hasRead + 10));
    //         in.reset();
    //         assertEquals(in.read(dest, 2, 10), 10);
    //         assertEquals(Arrays.copyOfRange(dest, 2, 12), Arrays.copyOfRange(source, hasRead, hasRead + 10));
    //         hasRead += 10;
    //     }
    //
    //     // test skip: 10 + 10
    //     assertEquals(in.skip(0), 0);
    //     assertEquals(in.skip(10), 10);
    //     hasRead += 10;
    //     assertEquals(in.read(dest, 1, 10), 10);
    //     assertEquals(Arrays.copyOfRange(dest, 1, 11), Arrays.copyOfRange(source, hasRead, hasRead + 10));
    //     hasRead += 10;
    //
    //     // test read(byte[]) to end
    //     assertEquals(in.read(dest), source.length - hasRead);
    //     assertEquals(
    //         Arrays.copyOfRange(dest, 0, source.length - hasRead),
    //         Arrays.copyOfRange(source, hasRead, source.length)
    //     );
    //     assertEquals(in.read(), -1);
    //     assertEquals(in.read(dest), -1);
    //
    //     // close
    //     in.close();
    // }

    // source.length >= 43
    public static void testReader(Reader in, char[] source) throws Exception {
        char[] dest = new char[source.length];
        assertEquals(in.read(dest, 0, 0), 0);

        // test read(): 3
        assertEquals((char) in.read(), source[0]);
        assertEquals((char) in.read(), source[1]);
        assertEquals((char) in.read(), source[2]);

        int hasRead = 3;

        // test read(char[], int, int): 10
        assertEquals(in.read(dest, 5, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 5, 15), Arrays.copyOfRange(source, hasRead, hasRead + 10));
        hasRead += 10;

        // test mark/reset: 10
        if (in.markSupported()) {
            expectThrows(IOException.class, in::reset);
            in.mark(0);
            assertEquals(in.read(dest, 3, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, hasRead, hasRead + 10));
            in.reset();
            assertEquals(in.read(dest, 2, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 2, 12), Arrays.copyOfRange(source, hasRead, hasRead + 10));
            hasRead += 10;
        }

        // test skip: 10 + 10
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(10), 10);
        hasRead += 10;
        assertEquals(in.read(dest, 1, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 1, 11), Arrays.copyOfRange(source, hasRead, hasRead + 10));
        hasRead += 10;

        // test read(char[]) to end
        assertEquals(in.read(dest), source.length - hasRead);
        assertEquals(
            Arrays.copyOfRange(dest, 0, source.length - hasRead),
            Arrays.copyOfRange(source, hasRead, source.length)
        );
        assertEquals(in.read(), -1);
        assertEquals(in.read(dest), -1);

        // close
        in.close();
    }
}
