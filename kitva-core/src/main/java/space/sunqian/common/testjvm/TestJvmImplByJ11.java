package space.sunqian.common.testjvm;

import space.sunqian.common.base.system.JvmKit;

enum TestJvmImplByJ11 implements TestJvmService {

    INST;

    @Override
    public void testJvm() {
        System.out.println("Current JVM version: " + JvmKit.javaMajorVersion());
    }
}
