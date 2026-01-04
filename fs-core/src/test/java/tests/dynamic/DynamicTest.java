package tests.dynamic;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.system.ResKit;
import space.sunqian.fs.dynamic.DynamicClassLoader;
import space.sunqian.fs.dynamic.DynamicException;
import space.sunqian.fs.dynamic.DynamicKit;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamicTest {

    @Test
    public void testBytecode() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream(
            LA.class.getName().replace('.', '/') + ".class"
        );
        byte[] laBytes = IOKit.read(in);
        assertArrayEquals(laBytes, DynamicKit.bytecode(LA.class));
        URL objectUrl = ResKit.findResource(Object.class.getName().replace('.', '/') + ".class");
        byte[] objectBytes = IOKit.read(objectUrl.openStream());
        assertArrayEquals(objectBytes, DynamicKit.bytecode(Object.class));
        {
            // ClassNotFoundException
            Class<?> cls = Proxy.getProxyClass(new DynamicClassLoader(), List.class);
            assertThrows(DynamicException.class, () -> DynamicKit.bytecode(cls));
        }
    }

    @Test
    public void testExceptions() {
        {
            // DynamicException
            assertThrows(DynamicException.class, () -> {
                throw new DynamicException();
            });
            assertThrows(DynamicException.class, () -> {
                throw new DynamicException("");
            });
            assertThrows(DynamicException.class, () -> {
                throw new DynamicException("", new RuntimeException());
            });
            assertThrows(DynamicException.class, () -> {
                throw new DynamicException(new RuntimeException());
            });
        }
    }
}
