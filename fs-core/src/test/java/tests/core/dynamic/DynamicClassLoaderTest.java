package tests.core.dynamic;

import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nullable;
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
        testLoadClassWithParent(null);
        testLoadClassWithParent(getClass().getClassLoader());
    }

    private void testLoadClassWithParent(@Nullable ClassLoader parent) throws Exception {
        DynamicClassLoader loader = newClassLoader(parent);
        byte[] bytes = loadClassBytes(LA.class);

        // Test with class name and byte array
        Class<?> cls1 = loader.loadClass(LA.class.getName(), bytes);
        assertClassLoadedCorrectly(cls1, LA.class);
        assertSame(loader.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes)), cls1);

        // Test with class name and ByteBuffer
        DynamicClassLoader loader2 = newClassLoader(parent);
        Class<?> cls2 = loader2.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes));
        assertClassLoadedCorrectly(cls2, LA.class);
        assertSame(loader2.loadClass(LA.class.getName(), bytes), cls2);

        // Test with null name and byte array
        DynamicClassLoader loader3 = newClassLoader(parent);
        Class<?> cls3 = loader3.loadClass(null, bytes);
        assertClassLoadedCorrectly(cls3, LA.class);
        assertSame(loader3.loadClass(LA.class.getName(), ByteBuffer.wrap(bytes)), cls3);

        // Test with null name and ByteBuffer
        DynamicClassLoader loader4 = newClassLoader(parent);
        Class<?> cls4 = loader4.loadClass(null, ByteBuffer.wrap(bytes));
        assertClassLoadedCorrectly(cls4, LA.class);
        assertSame(loader4.loadClass(LA.class.getName(), bytes), cls4);
    }

    @Test
    public void testLoadedClass() throws Exception {
        testLoadedClassWithParent(null);
        testLoadedClassWithParent(getClass().getClassLoader());
    }

    private void testLoadedClassWithParent(@Nullable ClassLoader parent) throws Exception {
        // Load class with same name but different class loaders
        Class<?> cls1 = loadClassWithLoader(parent, LA.class);
        Class<?> cls2 = loadClassWithLoader(parent, LA.class);

        // Verify classes are different instances but same name
        assertEquals(cls1.getName(), cls2.getName());
        assertEquals(cls1.getName(), LA.class.getName());
        assertNotEquals(LA.class, cls1);
        assertNotEquals(LA.class, cls2);
        assertNotEquals(cls1, cls2);

        // Verify instances are not of LA type
        Object o1 = cls1.getConstructor().newInstance();
        Object o2 = cls2.getConstructor().newInstance();
        assertFalse(o1 instanceof LA);
        assertFalse(o2 instanceof LA);

        // Test with LAC.binary
        DynamicClassLoader loader3 = newClassLoader(parent);
        InputStream in3 = ClassLoader.getSystemResourceAsStream("dynamic/LAC.binary");
        byte[] bytes3 = IOKit.read(in3);
        Class<?> cls3 = loader3.loadClass(LA.class.getName(), bytes3);
        in3.close();

        // Verify LAC is assignable to LA
        assertTrue(LA.class.isAssignableFrom(cls3));
        LA la = new LA();
        Object lac = cls3.getConstructor().newInstance();
        assertTrue(lac instanceof LA);

        // Test compute method
        String test = "tests";
        assertEquals(test + test, la.compute(test));
        assertEquals(test, ((LA) lac).compute(test));
    }

    private byte[] loadClassBytes(Class<?> cls) throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream(
            cls.getName().replace('.', '/') + ".class"
        );
        byte[] bytes = IOKit.read(in);
        in.close();
        return bytes;
    }

    private Class<?> loadClassWithLoader(@Nullable ClassLoader parent, Class<?> cls) throws Exception {
        DynamicClassLoader loader = newClassLoader(parent);
        byte[] bytes = loadClassBytes(cls);
        Class<?> loadedClass = loader.loadClass(cls.getName(), bytes);
        return loadedClass;
    }

    private void assertClassLoadedCorrectly(Class<?> loadedClass, Class<?> expectedClass) {
        assertNotEquals(expectedClass, loadedClass);
        assertEquals(loadedClass.getName(), expectedClass.getName());
    }

    private DynamicClassLoader newClassLoader(@Nullable ClassLoader parent) {
        return parent == null ? new DynamicClassLoader() : new DynamicClassLoader(parent);
    }
}
