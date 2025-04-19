package test;

import org.testng.annotations.Test;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;
import xyz.sunqian.test.TestReader;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.expectThrows;

public class ReadTest {

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
    }

    public void testReadBytes() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        byte[] dest = new byte[6];
        test.read(dest);
        assertEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextReadOption(ReadOps.READ_NORMAL);
        test.read(dest);
        assertEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.read(dest), 0);
        assertNotEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.read(dest), -1);
        assertNotEquals(dest, bytes);
        in.reset();
        Arrays.fill(dest, (byte) 9);

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.read(dest));
        in.reset();
        Arrays.fill(dest, (byte) 9);
    }

    public void testReadByte() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextReadOption(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.read());
        in.reset();
    }

    public void testSkipBytes() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextReadOption(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.skip(6));
        in.reset();
    }

    public void testAvailable() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6};
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        TestInputStream test = new TestInputStream(in);
        assertEquals(test.available(), 6);
        in.reset();

        test.setNextReadOption(ReadOps.READ_NORMAL);
        assertEquals(test.available(), 6);
        in.reset();

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.available(), 0);
        in.reset();

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.available(), 0);
        in.reset();

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.available());
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
    }

    public void testReadChars() throws Exception {
        char[] chars = new char[]{1, 2, 3, 4, 5, 6};
        CharArrayReader in = new CharArrayReader(chars);
        TestReader test = new TestReader(in);
        char[] dest = new char[6];
        test.read(dest);
        assertEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextReadOption(ReadOps.READ_NORMAL);
        test.read(dest);
        assertEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.read(dest), 0);
        assertNotEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.read(dest), -1);
        assertNotEquals(dest, chars);
        in.reset();
        Arrays.fill(dest, (char) 9);

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.read(dest));
        in.reset();
        Arrays.fill(dest, (char) 9);
    }

    public void testReadChar() throws Exception {
        char[] chars = new char[]{1, 2, 3, 4, 5, 6};
        CharArrayReader in = new CharArrayReader(chars);
        TestReader test = new TestReader(in);
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextReadOption(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.read(), 1);
        in.reset();

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.read(), -1);
        in.reset();

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.read());
        in.reset();
    }

    public void testSkipChars() throws Exception {
        char[] chars = new char[]{1, 2, 3, 4, 5, 6};
        CharArrayReader in = new CharArrayReader(chars);
        TestReader test = new TestReader(in);
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextReadOption(ReadOps.READ_NORMAL);
        ;
        assertEquals(test.skip(6), 6);
        in.reset();

        test.setNextReadOption(ReadOps.READ_ZERO);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextReadOption(ReadOps.REACH_END);
        assertEquals(test.skip(6), 0);
        in.reset();

        test.setNextReadOption(ReadOps.THROW);
        expectThrows(IOException.class, () -> test.skip(6));
        in.reset();
    }
}
