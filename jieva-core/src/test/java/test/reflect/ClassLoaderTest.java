package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.reflect.BytesClassLoader;

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

    @Test
    public void testLoadedClass() throws Exception {
        BytesClassLoader loader1 = new BytesClassLoader();
        InputStream in1 = ClassLoader.getSystemResourceAsStream(
            A.class.getName().replace('.', '/') + ".class"
        );
        byte[] bytes1 = JieIO.read(in1);
        Class<?> cls1 = loader1.loadClass(A.class.getName(), bytes1);
        in1.close();
        BytesClassLoader loader2 = new BytesClassLoader();
        InputStream in2 = ClassLoader.getSystemResourceAsStream(
            A.class.getName().replace('.', '/') + ".class"
        );
        byte[] bytes2 = JieIO.read(in2);
        Class<?> cls2 = loader2.loadClass(A.class.getName(), bytes2);
        in2.close();
        assertEquals(cls1.getName(), cls2.getName());
        assertEquals(cls1.getName(), A.class.getName());
        assertNotEquals(cls1, A.class);
        assertNotEquals(cls2, A.class);
        assertNotEquals(cls1, cls2);
        Object o1 = cls1.getConstructor().newInstance();
        Object o2 = cls2.getConstructor().newInstance();
        assertFalse(o1 instanceof A);
        assertFalse(o2 instanceof A);
        BytesClassLoader loader3 = new BytesClassLoader();
        InputStream in3 = ClassLoader.getSystemResourceAsStream("reflect/AC.bytes");
        byte[] bytes3 = JieIO.read(in3);
        Class<?> cls3 = loader3.loadClass(A.class.getName(), bytes3);
        in1.close();
        assertTrue(A.class.isAssignableFrom(cls3));
        A a = new A();
        A ac = (A) cls3.getConstructor().newInstance();
        System.out.println(ac.a());
        assertEquals(a.a(), 666);
        //assertEquals(ac.a(), a.a() * 2);
    }

    public static class A {

        int a() {
            return 666;
        }
    }
}
