package tests.core.third.asm;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.third.asm.AsmKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsmTest implements TestPrint {

    @Test
    public void testToGeneratedClassName() {
        String version = "0.0.1-SNAPSHOT+Build123.456+-/|\\";
        assertEquals(
            "0_0_1_SNAPSHOT_Build123_456_____",
            AsmKit.toGeneratedClassName(version)
        );
    }

    @Test
    public void testNewClassInternalName() {
        Package pkg = AsmTest.class.getPackage();
        String className = AsmKit.newClassInternalName(pkg);
        printFor("Generated class name: " + className);
        assertEquals(
            pkg.getName().replace('.', '/')
                + "/C" + AsmKit.lastClassCount() + AsmKit.GEN_SUFFIX,
            className
        );
    }
}
