package space.sunqian.common.testjvm;

import space.sunqian.common.base.system.JvmKit;

final class TestJvmImp implements TestJvmService {

    @Override
    public void testJvm() {
        System.out.println("Current JVM version: " + JvmKit.javaMajorVersion());
    }
}
