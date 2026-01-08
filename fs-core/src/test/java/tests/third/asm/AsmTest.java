package tests.third.asm;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.third.asm.AsmKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsmTest implements PrintTest {

    private static final String GEN_CLASS_NAME = "GenBy$"
        +
        Fs.LIB_NAME
        +
        "$"
        +
        AsmKit.toGeneratedClassName(Fs.LIB_VERSION)
        +
        "$";

    @Test
    public void testGeneratedClassName() {
        {
            String version = "0.0.1-SNAPSHOT+Build123.456+-/|\\";
            assertEquals(
                "0_0_1_SNAPSHOT_Build123_456_____",
                AsmKit.toGeneratedClassName(version)
            );
        }
        {

            Package pkg = AsmTest.class.getPackage();
            String className = AsmKit.newClassInternalName(pkg);
            printFor("Generated class name: " + className);
            assertTrue(className.startsWith(
                pkg.getName().replace('.', '/') + "/" + GEN_CLASS_NAME
            ));
        }
    }
}
