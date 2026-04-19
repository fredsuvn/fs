package tests.core.dynamic;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.dynamic.DynamicClassLoader;
import space.sunqian.fs.dynamic.DynamicException;
import space.sunqian.fs.dynamic.DynamicKit;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamicTest {

    @Test
    public void testBytecode() throws Exception {
        testBytecodeForClass(LA.class);
        testBytecodeForClass(Object.class);
        testBytecodeForProxyClass();
    }

    private void testBytecodeForClass(Class<?> cls) throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream(
            cls.getName().replace('.', '/') + ".class"
        );
        byte[] bytes = IOKit.read(in);
        assertArrayEquals(bytes, DynamicKit.bytecode(cls));
    }

    private void testBytecodeForProxyClass() {
        // ClassNotFoundException
        Class<?> cls = Proxy.getProxyClass(new DynamicClassLoader(), List.class);
        assertThrows(DynamicException.class, () -> DynamicKit.bytecode(cls));
    }

    @Test
    public void testDynamicException() {
        assertThrows(DynamicException.class, () -> {throw new DynamicException();});
        assertThrows(DynamicException.class, () -> {throw new DynamicException("");});
        assertThrows(DynamicException.class, () -> {throw new DynamicException("", new RuntimeException());});
        assertThrows(DynamicException.class, () -> {throw new DynamicException(new RuntimeException());});
    }
}
