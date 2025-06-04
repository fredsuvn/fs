package test.asm;

import org.testng.annotations.Test;
import xyz.sunqian.common.asm.JieAsm;

import static org.testng.Assert.assertTrue;

public class AsmTest {

    @Test
    public void testAsm() {
        assertTrue(JieAsm.supportsAsm());
    }
}
