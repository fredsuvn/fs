package tests;

import org.junit.jupiter.api.Test;
import internal.test.ReadOps;
import internal.test.TestIOException;
import internal.test.TestInputStream;
import internal.test.TestReader;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecialReaderTest {

    @Test
    public void testReadBytesOptions() throws Exception {
        testReadBytes();
        testReadByte();
        testSkipBytes();
        testAvailable();

        // others
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.markSupported(), in.markSupported());
        test.mark(0);
        test.reset();
        test.close();
        test.setNextOperation(ReadOps.THROW);
        assertThrows(TestIOException.class, () -> test.mark(0));
        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, test::reset);
        test.setNextOperation(ReadOps.THROW, 2);
        assertThrows(IOException.class, test::close);
        assertThrows(IOException.class, test::close);
        test.close();
        test.setNextOperation(ReadOps.THROW, 2);
        test.setNextOperation(ReadOps.READ_NORMAL);
        test.close();
        boolean ms = test.markSupported();
        test.markSupported(false);
        assertFalse(test.markSupported());
        test.markSupported(true);
        assertTrue(test.markSupported());
        test.markSupported(null);
        assertEquals(ms, test.markSupported());
    }

    public void testReadBytes() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        byte[] dest = new byte[6];
        test.read(dest);
        assertArrayEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextOperation(ReadOps.READ_NORMAL);
        test.read(dest);
        assertArrayEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.read(dest), 0);
        assertNotEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.read(dest), -1);
        assertNotEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.read(dest));
        in.reset();
        Arrays.fill(dest, (byte) 9);
    }

    public void testReadByte() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextOperation(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.read());
        in.reset();
    }

    public void testSkipBytes() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextOperation(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.skip(6));
        in.reset();
    }

    public void testAvailable() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.available(), 6);
        in.reset();

        test.setNextOperation(ReadOps.READ_NORMAL);
        assertEquals(test.available(), 6);
        in.reset();

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.available(), 0);
        in.reset();

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.available(), 0);
        in.reset();

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.available());
        in.reset();
    }

    @Test
    public void testReadCharsOptions() throws Exception {
        testReadChars();
        testReadChar();
        testSkipChars();
        testAvailable();

        // others
        CharArrayReader in = new CharArrayReader(new char[0]);
        TestReader test = new TestReader(in);
        assertEquals(test.markSupported(), in.markSupported());
        test.mark(0);
        test.reset();
        test.close();
        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.mark(0));
        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, test::reset);
        test.setNextOperation(ReadOps.THROW, 2);
        assertThrows(IOException.class, test::close);
        assertThrows(IOException.class, test::close);
        test.close();
        test.setNextOperation(ReadOps.THROW, 2);
        test.setNextOperation(ReadOps.READ_NORMAL);
        test.close();
        boolean ms = test.markSupported();
        test.markSupported(false);
        assertFalse(test.markSupported());
        test.markSupported(true);
        assertTrue(test.markSupported());
        test.markSupported(null);
        assertEquals(ms, test.markSupported());
    }

    public void testReadChars() throws Exception {
        char[] chars = new char[]{1, 2, 3, 4, 5, 6};
        CharArrayReader in = new CharArrayReader(chars);
        TestReader test = new TestReader(in);
        char[] dest = new char[6];
        test.read(dest);
        assertArrayEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextOperation(ReadOps.READ_NORMAL);
        test.read(dest);
        assertArrayEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.read(dest), 0);
        assertNotEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.read(dest), -1);
        assertNotEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.read(dest));
        in.reset();
        Arrays.fill(dest, (char) 9);
    }

    public void testReadChar() throws Exception {
        char[] chars = new char[]{1, 2, 3, 4, 5, 6};
        CharArrayReader in = new CharArrayReader(chars);
        TestReader test = new TestReader(in);
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextOperation(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.read());
        in.reset();
    }

    public void testSkipChars() throws Exception {
        char[] chars = new char[]{1, 2, 3, 4, 5, 6};
        CharArrayReader in = new CharArrayReader(chars);
        TestReader test = new TestReader(in);
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextOperation(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextOperation(ReadOps.READ_ZERO);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextOperation(ReadOps.REACH_END);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextOperation(ReadOps.THROW);
        assertThrows(IOException.class, () -> test.skip(6));
        in.reset();
    }
}
