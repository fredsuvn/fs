package test.base.system;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.system.OSKit;
import xyz.sunqian.test.PrintTest;

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
