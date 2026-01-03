package tests.dynamic;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.dynamic.DynamicException;
import space.sunqian.fs.dynamic.DynamicKit;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamicTest {

    @Test
    public void testBytecode() {
        InputStream in = ClassLoader.getSystemResourceAsStream(
            LA.class.getName().replace('.', '/') + ".class"
        );
        byte[] laBytes = IOKit.read(in);
        assertArrayEquals(laBytes, DynamicKit.bytecode(LA.class));
        {
            // ClassNotFoundException
            assertThrows(DynamicException.class, () -> DynamicKit.bytecode(Object.class));
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
