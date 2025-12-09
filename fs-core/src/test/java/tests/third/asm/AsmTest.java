package tests.third.asm;

import org.junit.jupiter.api.Test;
import space.sunqian.common.third.asm.AsmKit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsmTest {

    @Test
    public void testAsm() {
        assertTrue(AsmKit.isAvailable());
    }
}
