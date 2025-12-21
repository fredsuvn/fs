package tests.base.system;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.system.OSKit;

public class OSTest implements PrintTest {

    @Test
    public void testOSCheck() {
        printFor("OSKit.isWindows", OSKit.isWindows());
        printFor("OSKit.isLinux", OSKit.isLinux());
        printFor("OSKit.isMac", OSKit.isMac());
        printFor("OSKit.isFreeBSD", OSKit.isFreeBSD());
        printFor("OSKit.isOpenBSD", OSKit.isOpenBSD());
        printFor("OSKit.isNetBSD", OSKit.isNetBSD());
        printFor("OSKit.isAix", OSKit.isAix());
        printFor("OSKit.isHpUx", OSKit.isHpUx());
        printFor("OSKit.isSolaris", OSKit.isSolaris());
        printFor("OSKit.isZOS", OSKit.isZOS());
    }
}
