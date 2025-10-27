package tests.base;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.CheckKit;
import space.sunqian.common.base.exception.UnreachablePointException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckTest {

    @Test
    public void testCheckOffLen() {
        {
            // int
            CheckKit.checkOffLen(0, 0, 0);
            CheckKit.checkOffLen(0, 0, 1);
            CheckKit.checkOffLen(0, 1, 1);
            CheckKit.checkOffLen(0, 1, 2);
            CheckKit.checkOffLen(1, 2, 3);
            CheckKit.checkOffLen(1, 2, 4);
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(-1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(0, -1, 1));
        }
        {
            // long
            CheckKit.checkOffLen(0L, 0, 0);
            CheckKit.checkOffLen(0L, 0, 1);
            CheckKit.checkOffLen(0L, 1, 1);
            CheckKit.checkOffLen(0L, 1, 2);
            CheckKit.checkOffLen(1L, 2, 3);
            CheckKit.checkOffLen(1L, 2, 4);
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(-1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkOffLen(0L, -1, 1));
        }
    }

    @Test
    public void testCheckStartEnd() {
        {
            // int
            CheckKit.checkStartEnd(0, 0, 0);
            CheckKit.checkStartEnd(0, 0, 1);
            CheckKit.checkStartEnd(0, 1, 1);
            CheckKit.checkStartEnd(0, 1, 2);
            CheckKit.checkStartEnd(1, 2, 2);
            CheckKit.checkStartEnd(1, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(1, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(-1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(0, -1, 1));
        }
        {
            // long
            CheckKit.checkStartEnd(0L, 0, 0);
            CheckKit.checkStartEnd(0L, 0, 1);
            CheckKit.checkStartEnd(0L, 1, 1);
            CheckKit.checkStartEnd(0L, 1, 2);
            CheckKit.checkStartEnd(1L, 2, 2);
            CheckKit.checkStartEnd(1L, 2, 3);
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(1L, 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(-1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> CheckKit.checkStartEnd(0L, -1, 1));
        }
    }

    @Test
    public void testCheckExpr() {
        CheckKit.checkArgument(1 > 0, "1 > 0");
        CheckKit.checkState(1 > 0, "1 > 0");
        CheckKit.checkSupported(1 > 0, "1 > 0");
        CheckKit.checkElement(1 > 0, "1 > 0");
        CheckKit.checkReachable(1 > 0, "1 > 0");
        assertThrows(IllegalArgumentException.class, () -> CheckKit.checkArgument(1 < 0, "1 < 0"));
        assertThrows(IllegalStateException.class, () -> CheckKit.checkState(1 < 0, "1 < 0"));
        assertThrows(UnsupportedOperationException.class, () -> CheckKit.checkSupported(1 < 0, "1 < 0"));
        assertThrows(NoSuchElementException.class, () -> CheckKit.checkElement(1 < 0, "1 < 0"));
        assertThrows(UnreachablePointException.class, () -> CheckKit.checkReachable(1 < 0, "1 < 0"));
    }

    @Test
    public void testInBounds() {
        {
            // int
            CheckKit.checkInBounds(0, 0, 1);
            CheckKit.checkInBounds(1, 0, 2);
            CheckKit.checkInBounds(1, 1, 2);
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(1, 0, 1));
            // int range
            CheckKit.checkInBounds(0, 0, 0, 0);
            CheckKit.checkInBounds(0, 0, 0, 1);
            CheckKit.checkInBounds(0, 1, 0, 2);
            CheckKit.checkInBounds(1, 1, 0, 2);
            CheckKit.checkInBounds(1, 2, 0, 2);
            CheckKit.checkInBounds(1, 2, 0, 3);
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0, 1, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0, 1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(1, 2, 2, 3));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(2, 1, 0, 3));
        }
        {
            // long
            CheckKit.checkInBounds(0L, 0, 1);
            CheckKit.checkInBounds(1L, 0, 2);
            CheckKit.checkInBounds(1L, 1, 2);
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0L, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(1L, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0L, 1, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(1L, 0, 1));
            // long range
            CheckKit.checkInBounds(0L, 0, 0, 0);
            CheckKit.checkInBounds(0L, 0, 0, 1);
            CheckKit.checkInBounds(0L, 1, 0, 2);
            CheckKit.checkInBounds(1L, 1, 0, 2);
            CheckKit.checkInBounds(1L, 2, 0, 2);
            CheckKit.checkInBounds(1L, 2, 0, 3);
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0L, 1, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(0L, 1, 1, 1));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(1L, 2, 2, 3));
            assertThrows(IndexOutOfBoundsException.class, () ->
                CheckKit.checkInBounds(2L, 1, 0, 3));
        }
    }
}
