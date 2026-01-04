package tests.dynamic;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.dynamic.DynamicClassLoader;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicClassLoaderTest {

    @Test
    public void testLoadClass() throws Exception {
        {
            DynamicClassLoader loader = new DynamicClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(LA.class.getName(), bytes);
            assertNotEquals(LA.class, cls);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes)),
                cls
            );
            in.close();
        }
        {
            DynamicClassLoader loader = new DynamicClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes));
            assertNotEquals(LA.class, cls);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), bytes),
                cls
            );
            in.close();
        }
        {
            DynamicClassLoader loader = new DynamicClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(null, bytes);
            assertNotEquals(LA.class, cls);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes)),
                cls
            );
            in.close();
        }
        {
            DynamicClassLoader loader = new DynamicClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(null, ByteBuffer.wrap(bytes));
            assertNotEquals(LA.class, cls);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), bytes),
                cls
            );
            in.close();
        }
    }

    @Test
    public void testLoadedClass() throws Exception {
        DynamicClassLoader loader1 = new DynamicClassLoader();
        InputStream in1 = ClassLoader.getSystemResourceAsStream(
            LA.class.getName().replace('.', '/') + ".class"
        );
        byte[] bytes1 = IOKit.read(in1);
        Class<?> cls1 = loader1.loadClass(LA.class.getName(), bytes1);
        in1.close();
        DynamicClassLoader loader2 = new DynamicClassLoader();
        InputStream in2 = ClassLoader.getSystemResourceAsStream(
            LA.class.getName().replace('.', '/') + ".class"
        );
        byte[] bytes2 = IOKit.read(in2);
        Class<?> cls2 = loader2.loadClass(LA.class.getName(), bytes2);
        in2.close();
        assertEquals(cls1.getName(), cls2.getName());
        assertEquals(cls1.getName(), LA.class.getName());
        assertNotEquals(LA.class, cls1);
        assertNotEquals(LA.class, cls2);
        assertNotEquals(cls1, cls2);
        Object o1 = cls1.getConstructor().newInstance();
        Object o2 = cls2.getConstructor().newInstance();
        assertFalse(o1 instanceof LA);
        assertFalse(o2 instanceof LA);
        DynamicClassLoader loader3 = new DynamicClassLoader();
        InputStream in3 = ClassLoader.getSystemResourceAsStream("dynamic/LAC");
        byte[] bytes3 = IOKit.read(in3);
        Class<?> cls3 = loader3.loadClass(LA.class.getName(), bytes3);
        in3.close();
        assertTrue(LA.class.isAssignableFrom(cls3));
        LA la = new LA();
        Object lac = cls3.getConstructor().newInstance();
        assertTrue(lac instanceof LA);
        String test = "tests";
        assertEquals(test + test, la.compute(test));
        assertEquals(test, ((LA) lac).compute(test));
    }
}
