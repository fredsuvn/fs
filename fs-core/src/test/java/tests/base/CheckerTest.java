package tests.base;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.Checker;
import space.sunqian.common.base.exception.UnreachablePointException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckerTest {

    @Test
    public void testCheckOffLen() {
        {
            // int
            Checker.checkOffLen(0, 0, 0);
            Checker.checkOffLen(0, 0, 1);
            Checker.checkOffLen(0, 1, 1);
            Checker.checkOffLen(0, 1, 2);
            Checker.checkOffLen(1, 2, 3);
            Checker.checkOffLen(1, 2, 4);
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(-1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(0, -1, 1));
        }
        {
            // long
            Checker.checkOffLen(0L, 0, 0);
            Checker.checkOffLen(0L, 0, 1);
            Checker.checkOffLen(0L, 1, 1);
            Checker.checkOffLen(0L, 1, 2);
            Checker.checkOffLen(1L, 2, 3);
            Checker.checkOffLen(1L, 2, 4);
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(-1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkOffLen(0L, -1, 1));
        }
    }

    @Test
    public void testCheckStartEnd() {
        {
            // int
            Checker.checkStartEnd(0, 0, 0);
            Checker.checkStartEnd(0, 0, 1);
            Checker.checkStartEnd(0, 1, 1);
            Checker.checkStartEnd(0, 1, 2);
            Checker.checkStartEnd(1, 2, 2);
            Checker.checkStartEnd(1, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(1, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(-1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(0, -1, 1));
        }
        {
            // long
            Checker.checkStartEnd(0L, 0, 0);
            Checker.checkStartEnd(0L, 0, 1);
            Checker.checkStartEnd(0L, 1, 1);
            Checker.checkStartEnd(0L, 1, 2);
            Checker.checkStartEnd(1L, 2, 2);
            Checker.checkStartEnd(1L, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(1L, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(-1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> Checker.checkStartEnd(0L, -1, 1));
        }
    }

    @Test
    public void testCheckExpr() {
        Checker.checkArgument(1 > 0, "1 > 0");
        Checker.checkState(1 > 0, "1 > 0");
        Checker.checkSupported(1 > 0, "1 > 0");
        Checker.checkElement(1 > 0, "1 > 0");
        Checker.checkReachable(1 > 0, "1 > 0");
        assertThrows(IllegalArgumentException.class, () -> Checker.checkArgument(1 < 0, "1 < 0"));
        assertThrows(IllegalStateException.class, () -> Checker.checkState(1 < 0, "1 < 0"));
        assertThrows(UnsupportedOperationException.class, () -> Checker.checkSupported(1 < 0, "1 < 0"));
        assertThrows(NoSuchElementException.class, () -> Checker.checkElement(1 < 0, "1 < 0"));
        assertThrows(UnreachablePointException.class, () -> Checker.checkReachable(1 < 0, "1 < 0"));
    }

    @Test
    public void testInBounds() {
        {
            // int
            Checker.checkInBounds(0, 0, 1);
            Checker.checkInBounds(1, 0, 2);
            Checker.checkInBounds(1, 1, 2);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(1, 0, 1));
            // int range
            Checker.checkInBounds(0, 0, 0, 0);
            Checker.checkInBounds(0, 0, 0, 1);
            Checker.checkInBounds(0, 1, 0, 2);
            Checker.checkInBounds(1, 1, 0, 2);
            Checker.checkInBounds(1, 2, 0, 2);
            Checker.checkInBounds(1, 2, 0, 3);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0, 1, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0, 1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(1, 2, 2, 3));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(2, 1, 0, 3));
        }
        {
            // long
            Checker.checkInBounds(0L, 0, 1);
            Checker.checkInBounds(1L, 0, 2);
            Checker.checkInBounds(1L, 1, 2);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0L, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(1L, 0, 1));
            // long range
            Checker.checkInBounds(0L, 0, 0, 0);
            Checker.checkInBounds(0L, 0, 0, 1);
            Checker.checkInBounds(0L, 1, 0, 2);
            Checker.checkInBounds(1L, 1, 0, 2);
            Checker.checkInBounds(1L, 2, 0, 2);
            Checker.checkInBounds(1L, 2, 0, 3);
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0L, 1, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(0L, 1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(1L, 2, 2, 3));
            assertThrows(IndexOutOfBoundsException.class, () ->
                Checker.checkInBounds(2L, 1, 0, 3));
        }
    }
}
