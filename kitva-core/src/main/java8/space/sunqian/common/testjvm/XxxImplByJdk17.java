package space.sunqian.common.testjvm;

import space.sunqian.common.base.system.JvmKit;

public class XxxImplByJdk17 {

    public static void showVersion() {
        if (JvmKit.javaMajorVersion() <= 8) {
            System.out.println("8888888888888888");
        } else {
            System.out.println("1717171717171717");
        }
    }
}
