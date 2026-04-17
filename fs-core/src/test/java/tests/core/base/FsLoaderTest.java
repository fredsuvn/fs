package tests.core.base;

import internal.annotations.J17Also;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.base.exception.UnknownTypeException;
import space.sunqian.fs.base.system.JvmKit;
import space.sunqian.fs.object.schema.handlers.RecordSchemaHandler;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FsLoaderTest {

    @J17Also
    @Test
    public void testLoadImplByJvm() {
        int jvmVersion = JvmKit.javaMajorVersion();
        if (jvmVersion <= 8) {
            testLoadImplByJvmForJ8();
        } else {
            testLoadImplByJvmForJ9Plus();
        }
        testLoadImplByJvmWithUnknownService();
    }

    private void testLoadImplByJvmForJ8() {
        // Test XService for J8
        XService service8 = FsLoader.loadImplByJvm(XService.class, 8);
        assertEquals("XService.J8", service8.doSomething());
        XService service9 = FsLoader.loadImplByJvm(XService.class, 9);
        assertEquals("XService.J8", service9.doSomething());
        XService service17 = FsLoader.loadImplByJvm(XService.class, 17);
        assertEquals("XService.J8", service17.doSomething());

        // Test YService for J8
        YService yService8 = FsLoader.loadImplByJvm(YService.class, 8);
        assertEquals("YService.J8", yService8.doSomething());
        YService yService9 = FsLoader.loadImplByJvm(YService.class, 9);
        assertEquals("YService.J8", yService9.doSomething());
        YService yService17 = FsLoader.loadImplByJvm(YService.class, 17);
        assertEquals("YService.J8", yService17.doSomething());
    }

    private void testLoadImplByJvmForJ9Plus() {
        // Test XService for J9+
        XService service8 = FsLoader.loadImplByJvm(XService.class, 8);
        assertEquals("XService.J8", service8.doSomething());
        XService service17 = FsLoader.loadImplByJvm(XService.class, 17);
        assertEquals("XService.J17", service17.doSomething());

        // Test YService for J9+
        YService yService8 = FsLoader.loadImplByJvm(YService.class, 8);
        assertEquals("YService.J8", yService8.doSomething());
        YService yService9 = FsLoader.loadImplByJvm(YService.class, 9);
        assertEquals("YService.J8", yService9.doSomething());
        YService yService17 = FsLoader.loadImplByJvm(YService.class, 17);
        assertEquals("YService.J8", yService17.doSomething());
    }

    private void testLoadImplByJvmWithUnknownService() {
        assertThrows(UnknownTypeException.class, () -> FsLoader.loadImplByJvm(XXService.class, 8));
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

    private interface YService {
        String doSomething();
    }

    private enum YServiceImpl implements YService {
        INST;

        @Override
        public String doSomething() {
            System.out.println("current jvm: " + JvmKit.javaMajorVersion());
            return "YService.J8";
        }
    }

    private interface XXService {
        String doSomething();
    }

    @Test
    public void testLoadClassByDependent() {
        testLoadClassByDependentWithExistingClass();
        testLoadClassByDependentWithNonExistingClass();
    }

    private void testLoadClassByDependentWithExistingClass() {
        assertEquals(
            String.class,
            FsLoader.loadClassByDependent("java.lang.String", "java.lang.String")
        );
    }

    private void testLoadClassByDependentWithNonExistingClass() {
        assertNull(FsLoader.loadClassByDependent("java.lang.String", "666"));
    }

    @Test
    public void testLoadInstanceByDependent() {
        testLoadInstanceByDependentWithExistingClass();
        testLoadInstanceByDependentWithNonExistingClass();
        testLoadInstanceByDependentWithRecordSchemaHandler();
    }

    private void testLoadInstanceByDependentWithExistingClass() {
        assertEquals(
            String.class,
            FsLoader.supplyByDependent(() -> String.class, "java.lang.String")
        );
    }

    private void testLoadInstanceByDependentWithNonExistingClass() {
        assertNull(FsLoader.supplyByDependent(() -> "java.lang.String", "666"));
    }

    private void testLoadInstanceByDependentWithRecordSchemaHandler() {
        assertNull(FsLoader.supplyByDependent(
            RecordSchemaHandler::getInstance,
            RecordSchemaHandler.class.getName() + "ImplByJ16"
        ));
    }

    @Test
    public void testLoadInstances() {
        assertEquals(
            Arrays.asList("", "123"),
            FsLoader.loadInstances(String.class, null, NoLoad.class, "123")
        );
    }

    private static class NoLoad {
        private NoLoad() {
        }
    }
}
