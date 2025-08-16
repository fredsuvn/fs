package test.runtime.asm;

import org.testng.annotations.Test;
import xyz.sunqian.common.runtime.asm.AsmKit;

import static org.testng.Assert.assertTrue;

public class AsmTest {

    @Test
    public void testAsm() {
        assertTrue(AsmKit.supportsAsm());
    }
}
