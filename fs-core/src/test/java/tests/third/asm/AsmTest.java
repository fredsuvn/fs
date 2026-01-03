package tests.third.asm;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.third.asm.AsmKit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsmTest implements PrintTest {

    @Test
    public void testNewClassInternalName() {
        Package pkg = AsmTest.class.getPackage();
        String className = AsmKit.newClassInternalName(pkg);
        printFor("Generated class name: " + className);
        assertTrue(className.startsWith(
            pkg.getName().replace('.', '/') + "/GenBy" +
                "$" + Fs.LIB_NAME +
                "$V" + Fs.LIB_VERSION.replace('.', '_') +
                "$C"
        ));
    }
}
