package space.sunqian.common.testjvm;

import space.sunqian.common.base.Kit;
import space.sunqian.common.base.lang.EnumKit;
import space.sunqian.common.base.system.JvmKit;
import space.sunqian.common.runtime.reflect.ClassKit;

interface TestJvmService {

    void testJvm();

    class Back {

        static final TestJvmService INST = loadByCurrentJvm();

        private static TestJvmService loadByCurrentJvm() {
            int majorVersion = JvmKit.javaMajorVersion();
            return majorVersion <= 8 ? new TestJvmImp() : loadByJ17();
        }

        private static TestJvmService loadByJ17() {
            String name = TestJvmService.class.getPackage().getName() + ".TestJvmImplByJ17";
            Class<?> cls = ClassKit.classForName(name, null);
            return EnumKit.findEnum(Kit.as(cls), "INST");
        }
    }
}
