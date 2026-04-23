package tests.core.base.system;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.system.OSKit;

public class OSTest implements TestPrint {

    @Test
    public void testOSCheck() {
        testWindowsCheck();
        testLinuxCheck();
        testMacCheck();
        testBSDChecks();
        testUnixChecks();
        testZOSCheck();
    }

    private void testWindowsCheck() {
        printFor("OSKit.isWindows", OSKit.isWindows());
    }

    private void testLinuxCheck() {
        printFor("OSKit.isLinux", OSKit.isLinux());
    }

    private void testMacCheck() {
        printFor("OSKit.isMac", OSKit.isMac());
    }

    private void testBSDChecks() {
        printFor("OSKit.isFreeBSD", OSKit.isFreeBSD());
        printFor("OSKit.isOpenBSD", OSKit.isOpenBSD());
        printFor("OSKit.isNetBSD", OSKit.isNetBSD());
    }

    private void testUnixChecks() {
        printFor("OSKit.isAix", OSKit.isAix());
        printFor("OSKit.isHpUx", OSKit.isHpUx());
        printFor("OSKit.isSolaris", OSKit.isSolaris());
    }

    private void testZOSCheck() {
        printFor("OSKit.isZOS", OSKit.isZOS());
    }
}
