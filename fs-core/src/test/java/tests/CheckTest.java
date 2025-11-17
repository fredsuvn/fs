package tests;

import org.junit.jupiter.api.Test;
import space.sunqian.common.Check;
import space.sunqian.common.base.exception.UnreachablePointException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckTest {

    @Test
    public void testCheckOffLen() {
        {
            // int
            Check.checkOffLen(0, 0, 0);
            Check.checkOffLen(0, 0, 1);
            Check.checkOffLen(0, 1, 1);
            Check.checkOffLen(0, 1, 2);
            Check.checkOffLen(1, 2, 3);
            Check.checkOffLen(1, 2, 4);
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(-1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(0, -1, 1));
        }
        {
            // long
            Check.checkOffLen(0L, 0, 0);
            Check.checkOffLen(0L, 0, 1);
            Check.checkOffLen(0L, 1, 1);
            Check.checkOffLen(0L, 1, 2);
            Check.checkOffLen(1L, 2, 3);
            Check.checkOffLen(1L, 2, 4);
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(-1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkOffLen(0L, -1, 1));
        }
    }

    @Test
    public void testCheckStartEnd() {
        {
            // int
            Check.checkStartEnd(0, 0, 0);
            Check.checkStartEnd(0, 0, 1);
            Check.checkStartEnd(0, 1, 1);
            Check.checkStartEnd(0, 1, 2);
            Check.checkStartEnd(1, 2, 2);
            Check.checkStartEnd(1, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(1, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(-1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(0, -1, 1));
        }
        {
            // long
            Check.checkStartEnd(0L, 0, 0);
            Check.checkStartEnd(0L, 0, 1);
            Check.checkStartEnd(0L, 1, 1);
            Check.checkStartEnd(0L, 1, 2);
            Check.checkStartEnd(1L, 2, 2);
            Check.checkStartEnd(1L, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(1L, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(-1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Check.checkStartEnd(0L, -1, 1));
        }
    }

    @Test
    public void testCheckExpr() {
        Check.checkArgument(1 > 0, "1 > 0");
        Check.checkState(1 > 0, "1 > 0");
        Check.checkSupported(1 > 0, "1 > 0");
        Check.checkElement(1 > 0, "1 > 0");
        Check.checkReachable(1 > 0, "1 > 0");
        assertThrows(IllegalArgumentException.class, () -> Check.checkArgument(1 < 0, "1 < 0"));
        assertThrows(IllegalStateException.class, () -> Check.checkState(1 < 0, "1 < 0"));
        assertThrows(UnsupportedOperationException.class, () -> Check.checkSupported(1 < 0, "1 < 0"));
        assertThrows(NoSuchElementException.class, () -> Check.checkElement(1 < 0, "1 < 0"));
        assertThrows(UnreachablePointException.class, () -> Check.checkReachable(1 < 0, "1 < 0"));
    }

    @Test
    public void testInBounds() {
        {
            // int
            Check.checkInBounds(0, 0, 1);
            Check.checkInBounds(1, 0, 2);
            Check.checkInBounds(1, 1, 2);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(1, 0, 1));
            // int range
            Check.checkInBounds(0, 0, 0, 0);
            Check.checkInBounds(0, 0, 0, 1);
            Check.checkInBounds(0, 1, 0, 2);
            Check.checkInBounds(1, 1, 0, 2);
            Check.checkInBounds(1, 2, 0, 2);
            Check.checkInBounds(1, 2, 0, 3);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0, 1, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0, 1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(1, 2, 2, 3));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(2, 1, 0, 3));
        }
        {
            // long
            Check.checkInBounds(0L, 0, 1);
            Check.checkInBounds(1L, 0, 2);
            Check.checkInBounds(1L, 1, 2);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0L, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(1L, 0, 1));
            // long range
            Check.checkInBounds(0L, 0, 0, 0);
            Check.checkInBounds(0L, 0, 0, 1);
            Check.checkInBounds(0L, 1, 0, 2);
            Check.checkInBounds(1L, 1, 0, 2);
            Check.checkInBounds(1L, 2, 0, 2);
            Check.checkInBounds(1L, 2, 0, 3);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0L, 1, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(0L, 1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(1L, 2, 2, 3));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Check.checkInBounds(2L, 1, 0, 3));
        }
    }
}
