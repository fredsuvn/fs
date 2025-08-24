package tests.runtime.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.runtime.reflect.BytesClassLoader;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

public class ClassLoaderTest {

    @Test
    public void testLoadClass() throws Exception {
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(LA.class.getName(), bytes);
            assertNotEquals(cls, LA.class);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes)),
                cls
            );
            in.close();
        }
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes));
            assertNotEquals(cls, LA.class);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), bytes),
                cls
            );
            in.close();
        }
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(null, bytes);
            assertNotEquals(cls, LA.class);
            assertEquals(cls.getName(), LA.class.getName());
            assertSame(
                loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes)),
                cls
            );
            in.close();
        }
        {
            BytesClassLoader loader = new BytesClassLoader();
            InputStream in = ClassLoader.getSystemResourceAsStream(
                LA.class.getName().replace('.', '/') + ".class"
            );
            byte[] bytes = IOKit.read(in);
            Class<?> cls = loader.loadClass(null, ByteBuffer.wrap(bytes));
            assertNotEquals(cls, LA.class);
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
        BytesClassLoader loader1 = new BytesClassLoader();
        InputStream in1 = ClassLoader.getSystemResourceAsStream(
            LA.class.getName().replace('.', '/') + ".class"
        );
        byte[] bytes1 = IOKit.read(in1);
        Class<?> cls1 = loader1.loadClass(LA.class.getName(), bytes1);
        in1.close();
        BytesClassLoader loader2 = new BytesClassLoader();
        InputStream in2 = ClassLoader.getSystemResourceAsStream(
            LA.class.getName().replace('.', '/') + ".class"
        );
        byte[] bytes2 = IOKit.read(in2);
        Class<?> cls2 = loader2.loadClass(LA.class.getName(), bytes2);
        in2.close();
        assertEquals(cls1.getName(), cls2.getName());
        assertEquals(cls1.getName(), LA.class.getName());
        assertNotEquals(cls1, LA.class);
        assertNotEquals(cls2, LA.class);
        assertNotEquals(cls1, cls2);
        Object o1 = cls1.getConstructor().newInstance();
        Object o2 = cls2.getConstructor().newInstance();
        assertFalse(o1 instanceof LA);
        assertFalse(o2 instanceof LA);
        BytesClassLoader loader3 = new BytesClassLoader();
        InputStream in3 = ClassLoader.getSystemResourceAsStream("reflect/LAC");
        byte[] bytes3 = IOKit.read(in3);
        Class<?> cls3 = loader3.loadClass(LA.class.getName(), bytes3);
        in1.close();
        assertTrue(LA.class.isAssignableFrom(cls3));
        LA la = new LA();
        Object lac = cls3.getConstructor().newInstance();
        assertTrue(lac instanceof LA);
        String test = "tests";
        assertEquals(la.compute(test), test + test);
        assertEquals(((LA) lac).compute(test), test);
    }
}
