package tests;

import internal.test.J17Also;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.FsLoader;
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
            {
                XService service8 = FsLoader.loadImplByJvm(XService.class, 8);
                assertEquals("XService.J8", service8.doSomething());
                XService service9 = FsLoader.loadImplByJvm(XService.class, 9);
                assertEquals("XService.J8", service9.doSomething());
                XService service17 = FsLoader.loadImplByJvm(XService.class, 17);
                assertEquals("XService.J8", service17.doSomething());
            }
            {
                YService service8 = FsLoader.loadImplByJvm(YService.class, 8);
                assertEquals("YService.J8", service8.doSomething());
                YService service9 = FsLoader.loadImplByJvm(YService.class, 9);
                assertEquals("YService.J8", service9.doSomething());
                YService service17 = FsLoader.loadImplByJvm(YService.class, 17);
                assertEquals("YService.J8", service17.doSomething());
            }
        } else {
            {
                XService service8 = FsLoader.loadImplByJvm(XService.class, 8);
                assertEquals("XService.J8", service8.doSomething());
                YService service9 = FsLoader.loadImplByJvm(YService.class, 9);
                assertEquals("YService.J8", service9.doSomething());
                XService service17 = FsLoader.loadImplByJvm(XService.class, 17);
                assertEquals("XService.J17", service17.doSomething());
            }
            {
                YService service8 = FsLoader.loadImplByJvm(YService.class, 8);
                assertEquals("YService.J8", service8.doSomething());
                YService service9 = FsLoader.loadImplByJvm(YService.class, 9);
                assertEquals("YService.J8", service9.doSomething());
                YService service17 = FsLoader.loadImplByJvm(YService.class, 17);
                assertEquals("YService.J8", service17.doSomething());
            }
        }
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
        assertEquals(
            String.class,
            FsLoader.loadClassByDependent("java.lang.String", "java.lang.String")
        );
        assertNull(FsLoader.loadClassByDependent("java.lang.String", "666"));
    }

    @Test
    public void testLoadInstanceByDependent() {
        assertEquals(
            String.class,
            FsLoader.supplyByDependent(() -> String.class, "java.lang.String")
        );
        assertNull(FsLoader.supplyByDependent(() -> "java.lang.String", "666"));
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
