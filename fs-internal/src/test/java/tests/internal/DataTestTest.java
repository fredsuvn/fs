package tests.internal;

import org.junit.jupiter.api.Test;
import internal.test.DataTest;
import internal.test.PrintTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataTestTest implements DataTest, PrintTest {

    @Test
    public void testRandom() throws Exception {
        testRandom(10, -22, 33);
        testRandom(100, -64, 22);
        testRandom(100, -33, 44);

        assertThrows(NegativeArraySizeException.class, () -> randomBytes(-1));
        assertThrows(IllegalArgumentException.class, () -> randomBytes(10, (byte) 3, (byte) 2));
        assertThrows(NegativeArraySizeException.class, () -> randomChars(-1));
        assertThrows(IllegalArgumentException.class, () -> randomChars(10, '3', '2'));
    }

    private void testRandom(int size, int from, int to) throws Exception {
        {
            // byte
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
            // char
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
