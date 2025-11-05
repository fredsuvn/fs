package tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import space.sunqian.common.KitVa;
import space.sunqian.common.base.exception.UnknownTypeException;
import space.sunqian.common.base.system.JvmKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KitVaTest {

    @Tag("J17Test")
    @Test
    public void testLoadServiceByJvm() {
        int jvmVersion = JvmKit.javaMajorVersion();
        if (jvmVersion <= 8) {
            XService service8 = KitVa.loadServiceByJvm(XService.class, 8);
            assertEquals("XService.J8", service8.doSomething());
            XService service9 = KitVa.loadServiceByJvm(XService.class, 9);
            assertEquals("XService.J8", service9.doSomething());
            XService service17 = KitVa.loadServiceByJvm(XService.class, 17);
            assertEquals("XService.J8", service17.doSomething());
        } else {
            XService service8 = KitVa.loadServiceByJvm(XService.class, 8);
            assertEquals("XService.J8", service8.doSomething());
            assertThrows(UnknownTypeException.class, () -> KitVa.loadServiceByJvm(XService2.class, 9));
            XService service17 = KitVa.loadServiceByJvm(XService.class, 17);
            assertEquals("XService.J17", service17.doSomething());
        }
        assertThrows(UnknownTypeException.class, () -> KitVa.loadServiceByJvm(XService2.class, 8));
    }

    private interface XService {
        String doSomething();
    }

    private enum XServiceImpl implements XService {
        INST;

        @Override
        public String doSomething() {
            System.out.println("current jvm: " + JvmKit.javaMajorVersion());
            return "XService.J8";
        }
    }

    private enum XServiceImplByJ17 implements XService {
        INST;

        @Override
        public String doSomething() {
            System.out.println("current jvm: " + JvmKit.javaMajorVersion());
            return "XService.J17";
        }
    }

    private interface XService2 {
        String doSomething();
    }
}
