package test.base;

import org.testng.annotations.Test;
import test.Log;
import xyz.sunqian.common.base.JieCheck;

import static org.testng.Assert.*;
import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

public class CheckTest {

    @Test
    public void testOffsetLength() {
        expectThrows(IndexOutOfBoundsException.class, ()->{
            checkOffsetLength(10, 11, 1);
        });
        expectThrows(IndexOutOfBoundsException.class, ()->{
            checkOffsetLength(10, 11, 1);
        });
        expectThrows(IndexOutOfBoundsException.class, ()->{
            checkOffsetLength(10, 11, 1);
        });
    }
}
