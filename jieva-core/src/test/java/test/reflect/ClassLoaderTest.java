package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.reflect.BytesClassLoader;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;

public class ClassLoaderTest {

    @Test
    public void testLoadClass() throws Exception {
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                A.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = JieIO.read(in);
            Class<?> cls = loader.loadClass(A.class.getName(), bytes);
            assertNotEquals(cls, A.class);
            assertEquals(cls.getName(), A.class.getName());
            assertSame(
                loader.loadClass(A.class.getName(), ByteBuffer.wrap(bytes)),
                cls
            );
            in.close();
        }
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                A.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = JieIO.read(in);
            Class<?> cls = loader.loadClass(A.class.getName(), ByteBuffer.wrap(bytes));
            assertNotEquals(cls, A.class);
            assertEquals(cls.getName(), A.class.getName());
            assertSame(
                loader.loadClass(A.class.getName(), bytes),
                cls
            );
            in.close();
        }
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                A.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = JieIO.read(in);
            Class<?> cls = loader.loadClass(null, bytes);
            assertNotEquals(cls, A.class);
            assertEquals(cls.getName(), A.class.getName());
            assertSame(
                loader.loadClass(A.class.getName(), ByteBuffer.wrap(bytes)),
                cls
            );
            in.close();
        }
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                A.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = JieIO.read(in);
            Class<?> cls = loader.loadClass(null, ByteBuffer.wrap(bytes));
            assertNotEquals(cls, A.class);
            assertEquals(cls.getName(), A.class.getName());
            assertSame(
                loader.loadClass(A.class.getName(), bytes),
                cls
            );
            in.close();
        }
    }

    public static class A {}
}
