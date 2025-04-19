package test.base;

import org.testng.annotations.Test;

import static org.testng.Assert.expectThrows;
import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;
import static xyz.sunqian.common.base.JieCheck.checkStartEnd;

public class CheckTest {

    @Test
    public void testOffsetLength() {
        // int
        checkOffsetLength(10, 0, 10);
        checkOffsetLength(10, 0, 0);
        checkOffsetLength(10, 0, 9);
        checkOffsetLength(10, 1, 5);
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10, 0, 11);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10, 5, 6);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10, -1, 5);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10, 1, -6);
        });
        // long
        checkOffsetLength(10L, 0, 10);
        checkOffsetLength(10L, 0, 0);
        checkOffsetLength(10L, 0, 9);
        checkOffsetLength(10L, 1, 5);
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10L, 0, 11);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10L, 5, 6);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10L, -1, 5);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkOffsetLength(10L, 1, -6);
        });
    }

    @Test
    public void testStartEnd() {
        // int
        checkStartEnd(10, 0, 10);
        checkStartEnd(10, 0, 0);
        checkStartEnd(10, 0, 9);
        checkStartEnd(10, 1, 5);
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10, 0, 11);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10, 5, 16);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10, -1, 5);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10, 1, -6);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10, 5, 4);
        });
        // long
        checkStartEnd(10L, 0, 10);
        checkStartEnd(10L, 0, 0);
        checkStartEnd(10L, 0, 9);
        checkStartEnd(10L, 1, 5);
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10L, 0, 11);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10L, 5, 16);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10L, -1, 5);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10L, 1, -6);
        });
        expectThrows(IndexOutOfBoundsException.class, () -> {
            checkStartEnd(10L, 5, 4);
        });
    }
}
