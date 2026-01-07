package tests.third.asm;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.third.asm.AsmKit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsmTest implements PrintTest {

    private static final String GEN_CLASS_NAME = "GenBy$"
        +
        Fs.LIB_NAME
        +
        "$"
        +
        Fs.LIB_VERSION
            .replace('.', '_')
            .replace('-', '_')
        +
        "$";

    @Test
    public void testNewClassInternalName() {
        Package pkg = AsmTest.class.getPackage();
        String className = AsmKit.newClassInternalName(pkg);
        printFor("Generated class name: " + className);
        assertTrue(className.startsWith(
            pkg.getName().replace('.', '/') + "/" + GEN_CLASS_NAME
        ));
    }
}
