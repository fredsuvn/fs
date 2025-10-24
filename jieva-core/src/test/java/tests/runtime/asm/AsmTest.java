package tests.runtime.asm;

import org.testng.annotations.Test;
import space.sunqian.common.third.asm.AsmKit;

import static org.testng.Assert.assertTrue;

public class AsmTest {

    @Test
    public void testAsm() {
        assertTrue(AsmKit.isAvailable());
    }
}
