package space.sunqian.common.testjvm;

import space.sunqian.common.base.system.JvmKit;

enum TestJvmImplByJ9 implements TestJvmService {

    INST;

    @Override
    public void testJvm() {
        System.out.println("Current JVM version: " + JvmKit.javaMajorVersion());
    }
}
