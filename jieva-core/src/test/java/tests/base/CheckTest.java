package tests.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.CheckKit;

import static org.testng.Assert.expectThrows;
import static xyz.sunqian.common.base.CheckKit.checkOffLen;
import static xyz.sunqian.common.base.CheckKit.checkStartEnd;

public class CheckTest {

    @Test
    public void testOffsetLength() {
        // // int
        // CheckKit.checkOffLen( 0, 10,10);
        // CheckKit.checkOffLen(10, 0, 0);
        // CheckKit.checkOffLen(10, 0, 9);
        // CheckKit.checkOffLen(10, 1, 5);
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     CheckKit.checkOffLen(10, 0, 11);
        // });
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     CheckKit.checkOffLen(10, 5, 6);
        // });
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     CheckKit.checkOffLen(10, -1, 5);
        // });
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     CheckKit.checkOffLen(10, 1, -6);
        // });
        // // long
        // checkOffLen(10L, 0, 10);
        // checkOffLen(10L, 0, 0);
        // checkOffLen(10L, 0, 9);
        // checkOffLen(10L, 1, 5);
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     checkOffLen(10L, 0, 11);
        // });
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     checkOffLen(10L, 5, 6);
        // });
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     checkOffLen(10L, -1, 5);
        // });
        // expectThrows(IndexOutOfBoundsException.class, () -> {
        //     checkOffLen(10L, 1, -6);
        // });
    }

    // @Test
    // public void testStartEnd() {
    //     // int
    //     checkStartEnd(10, 0, 10);
    //     checkStartEnd(10, 0, 0);
    //     checkStartEnd(10, 0, 9);
    //     checkStartEnd(10, 1, 5);
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10, 0, 11);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10, 5, 16);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10, -1, 5);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10, 1, -6);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10, 5, 4);
    //     });
    //     // long
    //     checkStartEnd(10L, 0, 10);
    //     checkStartEnd(10L, 0, 0);
    //     checkStartEnd(10L, 0, 9);
    //     checkStartEnd(10L, 1, 5);
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10L, 0, 11);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10L, 5, 16);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10L, -1, 5);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10L, 1, -6);
    //     });
    //     expectThrows(IndexOutOfBoundsException.class, () -> {
    //         checkStartEnd(10L, 5, 4);
    //     });
    // }
}
