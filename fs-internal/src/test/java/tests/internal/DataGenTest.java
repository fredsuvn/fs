package tests.internal;

import internal.utils.DataGen;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataGenTest implements DataGen, TestPrint {

    @Test
    public void testRandom() throws Exception {
        testRandom(10, -22, 33);
        testRandom(100, -64, 22);
        testRandom(100, -33, 44);

        assertThrows(IllegalArgumentException.class, () -> randomChars(10, '3', '2'));
    }

    private void testRandom(int size, int from, int to) throws Exception {
        {
            // Byte
            byte[] array = randomBytes(size);
            printFor("Random bytes", Arrays.toString(array));
            byte start = (byte) from;
            byte end = (byte) to;
            array = randomBytes(size, start, end);
            for (byte e : array) {
                assertTrue(e >= start && e < end);
            }
            printFor("Random bytes", Arrays.toString(array));
        }
        {
            // Char
            char[] array = randomChars(size);
            printFor("Random chars", Arrays.toString(array));
            char start = (char) Math.min(Math.abs(from), Math.abs(to));
            char end = (char) Math.max(Math.abs(from), Math.abs(to));
            array = randomChars(size, start, end);
            for (char e : array) {
                assertTrue(e >= start && e < end);
            }
            printFor("Random chars", Arrays.toString(array));
        }
    }
}
