package tests.runtime.reflect;

import org.testng.annotations.Test;
import tests.utils.ErrorConstructor;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import xyz.sunqian.common.runtime.reflect.ClassKit;
import xyz.sunqian.test.AssertTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class ClassTest implements AssertTest {

    @Test
    public void testMember() throws Exception {
        // fields:
        Field c1 = Cls1.class.getDeclaredField("c1");
        Field c2 = Cls2.class.getDeclaredField("c2");
        Field c3 = Cls3.class.getDeclaredField("c3");
        Field pc1 = Cls1.class.getDeclaredField("pc1");
        Field pc2 = Cls2.class.getDeclaredField("pc2");
        Field pc3 = Cls3.class.getDeclaredField("pc3");
        Field i0 = Inter0.class.getDeclaredField("i0");
        Field i1 = Inter1.class.getDeclaredField("i1");
        Field i2 = Inter2.class.getDeclaredField("i2");
        Field i3 = Inter3.class.getDeclaredField("i3");
        assertEquals(ClassKit.getField(Cls3.class, "c3"), c3);
        assertEquals(ClassKit.getField(Cls3.class, "pc3"), pc3);
        assertNull(ClassKit.getField(Cls3.class, "pc3", false));
        assertNull(ClassKit.getField(Cls3.class, "x"));
        assertEquals(ClassKit.searchField(Cls3.class, "c1"), c1);
        assertEquals(ClassKit.searchField(Cls3.class, "c2"), c2);
        assertEquals(ClassKit.searchField(Cls3.class, "c3"), c3);
        assertEquals(ClassKit.searchField(Cls3.class, "pc1"), pc1);
        assertEquals(ClassKit.searchField(Cls3.class, "pc2"), pc2);
        assertEquals(ClassKit.searchField(Cls3.class, "pc3"), pc3);
        assertEquals(ClassKit.searchField(Cls3.class, "i0"), i0);
        assertEquals(ClassKit.searchField(Cls3.class, "i1"), i1);
        assertEquals(ClassKit.searchField(Cls3.class, "i2"), i2);
        assertEquals(ClassKit.searchField(Cls3.class, "i3"), i3);
        assertNull(ClassKit.searchField(Cls3.class, "x"));

        // methods:
        Method cm1 = Cls1.class.getDeclaredMethod("cm1");
        Method cm2 = Cls2.class.getDeclaredMethod("cm2");
        Method cm3 = Cls3.class.getDeclaredMethod("cm3");
        Method pcm1 = Cls1.class.getDeclaredMethod("pcm1");
        Method pcm2 = Cls2.class.getDeclaredMethod("pcm2");
        Method pcm3 = Cls3.class.getDeclaredMethod("pcm3");
        Method im0 = Inter0.class.getDeclaredMethod("im0");
        Method im1 = Inter1.class.getDeclaredMethod("im1");
        Method im2 = Inter2.class.getDeclaredMethod("im2");
        Method im3 = Inter3.class.getDeclaredMethod("im3");
        Class<?>[] params = {};
        assertEquals(ClassKit.getMethod(Cls3.class, "cm3", params), cm3);
        assertEquals(ClassKit.getMethod(Cls3.class, "pcm3", params), pcm3);
        assertNull(ClassKit.getMethod(Cls3.class, "pcm3", params, false));
        assertNull(ClassKit.getMethod(Cls3.class, "x", params));
        assertEquals(ClassKit.searchMethod(Cls3.class, "cm1", params), cm1);
        assertEquals(ClassKit.searchMethod(Cls3.class, "cm2", params), cm2);
        assertEquals(ClassKit.searchMethod(Cls3.class, "cm3", params), cm3);
        assertEquals(ClassKit.searchMethod(Cls3.class, "pcm1", params), pcm1);
        assertEquals(ClassKit.searchMethod(Cls3.class, "pcm2", params), pcm2);
        assertEquals(ClassKit.searchMethod(Cls3.class, "pcm3", params), pcm3);
        assertEquals(ClassKit.searchMethod(Cls3.class, "im0", params), im0);
        assertEquals(ClassKit.searchMethod(Cls3.class, "im1", params), im1);
        assertEquals(ClassKit.searchMethod(Cls3.class, "im2", params), im2);
        assertEquals(ClassKit.searchMethod(Cls3.class, "im3", params), im3);
        assertNull(ClassKit.searchMethod(Cls3.class, "x", params));

        // constructors:
        Constructor<?> cl3 = Cls3.class.getConstructor();
        Constructor<?> pcl3 = Cls3.class.getDeclaredConstructor(int.class);
        assertEquals(ClassKit.getConstructor(Cls3.class, params), cl3);
        assertEquals(ClassKit.getConstructor(Cls3.class, Jie.array(int.class)), pcl3);
        assertNull(ClassKit.getConstructor(Cls3.class, Jie.array(int.class), false));
        assertNull(ClassKit.getConstructor(Cls3.class, Jie.array(long.class)));
    }

    @Test
    public void testNewInstance() throws Exception {
        assertEquals(ClassKit.newInstance(String.class.getName()), "");
        assertNull(ClassKit.newInstance("123"));
        assertNull(ClassKit.newInstance(List.class));
        Constructor<?> constructor = String.class.getConstructor();
        assertEquals(ClassKit.newInstance(constructor), "");
        assertNull(ClassKit.newInstance(ErrorConstructor.class.getConstructor()));
    }

    @Test
    public void testArrayClass() throws Exception {
        // class:
        assertEquals(ClassKit.arrayClass(Object.class), Object[].class);
        assertEquals(ClassKit.arrayClass(Object[].class), Object[][].class);
        assertEquals(ClassKit.arrayClass(boolean.class), boolean[].class);
        assertEquals(ClassKit.arrayClass(boolean[].class), boolean[][].class);
        assertEquals(ClassKit.arrayClass(byte.class), byte[].class);
        assertEquals(ClassKit.arrayClass(short.class), short[].class);
        assertEquals(ClassKit.arrayClass(char.class), char[].class);
        assertEquals(ClassKit.arrayClass(int.class), int[].class);
        assertEquals(ClassKit.arrayClass(long.class), long[].class);
        assertEquals(ClassKit.arrayClass(float.class), float[].class);
        assertEquals(ClassKit.arrayClass(double.class), double[].class);
        assertNull(ClassKit.arrayClass(void.class));

        // parameterized types:
        class X {
            List<? extends String> l1 = null;
            List<? extends String>[] l2 = null;
            List<? extends String>[][] l3 = null;
        }
        Type l1Type = X.class.getDeclaredField("l1").getGenericType();
        assertEquals(ClassKit.arrayClass(l1Type), List[].class);
        Type l2Type = X.class.getDeclaredField("l2").getGenericType();
        assertEquals(ClassKit.arrayClass(l2Type), List[][].class);
        Type l3Type = X.class.getDeclaredField("l3").getGenericType();
        assertEquals(ClassKit.arrayClass(l3Type), List[][][].class);

        // component type:
        assertNull(ClassKit.arrayClass(((ParameterizedType) l1Type).getActualTypeArguments()[0]));
    }

    @Test
    public void testWrapper() throws Exception {
        assertEquals(ClassKit.wrapperClass(boolean.class), Boolean.class);
        assertEquals(ClassKit.wrapperClass(byte.class), Byte.class);
        assertEquals(ClassKit.wrapperClass(short.class), Short.class);
        assertEquals(ClassKit.wrapperClass(char.class), Character.class);
        assertEquals(ClassKit.wrapperClass(int.class), Integer.class);
        assertEquals(ClassKit.wrapperClass(long.class), Long.class);
        assertEquals(ClassKit.wrapperClass(float.class), Float.class);
        assertEquals(ClassKit.wrapperClass(double.class), Double.class);
        assertEquals(ClassKit.wrapperClass(void.class), Void.class);
        assertEquals(ClassKit.wrapperClass(Object.class), Object.class);

        // unreachable:
        Method wrapperPrimitive = ClassKit.class.getDeclaredMethod("wrapperPrimitive", Class.class);
        invokeThrows(UnknownPrimitiveTypeException.class, wrapperPrimitive, null, Object.class);
    }

    @Test
    public void testClassExists() {
        assertTrue(ClassKit.classExists(String.class.getName()));
        assertFalse(ClassKit.classExists("123"));
        assertNull(ClassKit.classForName("123", null));
        assertNull(ClassKit.classForName("123", Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testModifies() throws Exception {
        {
            // m1
            assertTrue(ClassKit.isStatic(Mod1.class.getMethod("m1")));
            assertTrue(ClassKit.isPublic(Mod1.class.getMethod("m1")));
            assertFalse(ClassKit.isProtected(Mod1.class.getMethod("m1")));
            assertFalse(ClassKit.isPrivate(Mod1.class.getMethod("m1")));
            assertFalse(ClassKit.isPackagePrivate(Mod1.class.getMethod("m1")));
        }
        {
            // m2
            assertFalse(ClassKit.isStatic(Mod1.class.getMethod("m2")));
            assertTrue(ClassKit.isPublic(Mod1.class.getMethod("m2")));
            assertFalse(ClassKit.isProtected(Mod1.class.getMethod("m2")));
            assertFalse(ClassKit.isPrivate(Mod1.class.getMethod("m2")));
            assertFalse(ClassKit.isPackagePrivate(Mod1.class.getMethod("m2")));
        }
        {
            // m3
            assertFalse(ClassKit.isStatic(Mod1.class.getDeclaredMethod("m3")));
            assertFalse(ClassKit.isPublic(Mod1.class.getDeclaredMethod("m3")));
            assertTrue(ClassKit.isProtected(Mod1.class.getDeclaredMethod("m3")));
            assertFalse(ClassKit.isPrivate(Mod1.class.getDeclaredMethod("m3")));
            assertFalse(ClassKit.isPackagePrivate(Mod1.class.getDeclaredMethod("m3")));
        }
        {
            // m4
            assertFalse(ClassKit.isStatic(Mod1.class.getDeclaredMethod("m4")));
            assertFalse(ClassKit.isPublic(Mod1.class.getDeclaredMethod("m4")));
            assertFalse(ClassKit.isProtected(Mod1.class.getDeclaredMethod("m4")));
            assertTrue(ClassKit.isPrivate(Mod1.class.getDeclaredMethod("m4")));
            assertFalse(ClassKit.isPackagePrivate(Mod1.class.getDeclaredMethod("m4")));
        }
        {
            // m5
            assertFalse(ClassKit.isStatic(Mod1.class.getDeclaredMethod("m5")));
            assertFalse(ClassKit.isPublic(Mod1.class.getDeclaredMethod("m5")));
            assertFalse(ClassKit.isProtected(Mod1.class.getDeclaredMethod("m5")));
            assertFalse(ClassKit.isPrivate(Mod1.class.getDeclaredMethod("m5")));
            assertTrue(ClassKit.isPackagePrivate(Mod1.class.getDeclaredMethod("m5")));
        }
        {
            // Mod1
            assertTrue(ClassKit.isStatic(Mod1.class));
            assertTrue(ClassKit.isPublic(Mod1.class));
            assertFalse(ClassKit.isProtected(Mod1.class));
            assertFalse(ClassKit.isPrivate(Mod1.class));
            assertFalse(ClassKit.isPackagePrivate(Mod1.class));
        }
        {
            // Mod2
            assertFalse(ClassKit.isStatic(Mod2.class));
            assertTrue(ClassKit.isPublic(Mod2.class));
            assertFalse(ClassKit.isProtected(Mod2.class));
            assertFalse(ClassKit.isPrivate(Mod2.class));
            assertFalse(ClassKit.isPackagePrivate(Mod2.class));
        }
        {
            // Mod3
            assertFalse(ClassKit.isStatic(Mod3.class));
            assertFalse(ClassKit.isPublic(Mod3.class));
            assertTrue(ClassKit.isProtected(Mod3.class));
            assertFalse(ClassKit.isPrivate(Mod3.class));
            assertFalse(ClassKit.isPackagePrivate(Mod3.class));
        }
        {
            // Mod4
            assertFalse(ClassKit.isStatic(Mod4.class));
            assertFalse(ClassKit.isPublic(Mod4.class));
            assertFalse(ClassKit.isProtected(Mod4.class));
            assertTrue(ClassKit.isPrivate(Mod4.class));
            assertFalse(ClassKit.isPackagePrivate(Mod4.class));
        }
        {
            // Mod5
            assertFalse(ClassKit.isStatic(Mod5.class));
            assertFalse(ClassKit.isPublic(Mod5.class));
            assertFalse(ClassKit.isProtected(Mod5.class));
            assertFalse(ClassKit.isPrivate(Mod5.class));
            assertTrue(ClassKit.isPackagePrivate(Mod5.class));
        }
    }

    @Test
    public void testOverridable() throws Exception {
        final class A {

            void m1() {
            }

            private void m2() {
            }

            public void m3() {
            }

            final void m4() {
            }
        }
        assertFalse(ClassKit.isOverridable(A.class));
        assertFalse(ClassKit.isOverridable(A.class.getDeclaredMethod("m1")));
        assertFalse(ClassKit.isOverridable(A.class.getDeclaredMethod("m2")));
        assertFalse(ClassKit.isOverridable(A.class.getDeclaredMethod("m3")));
        assertFalse(ClassKit.isOverridable(A.class.getDeclaredMethod("m4")));

        class B {

            void m1() {
            }

            private void m2() {
            }

            public void m3() {
            }

            final void m4() {
            }
        }
        assertTrue(ClassKit.isOverridable(B.class));
        assertTrue(ClassKit.isOverridable(B.class.getDeclaredMethod("m1")));
        assertFalse(ClassKit.isOverridable(B.class.getDeclaredMethod("m2")));
        assertTrue(ClassKit.isOverridable(B.class.getDeclaredMethod("m3")));
        assertFalse(ClassKit.isOverridable(A.class.getDeclaredMethod("m4")));

        assertFalse(ClassKit.isOverridable(Cls3.class.getDeclaredMethod("staticMethod")));
    }

    public interface Inter0 {

        String i0 = "";

        default void im0() {
        }
    }

    public interface Inter1 {

        String i1 = "";

        default void im1() {
        }
    }

    public interface Inter2 extends Inter1, Inter0 {

        String i2 = "";

        default void im2() {
        }
    }

    public interface Inter3 extends Inter2, Inter0 {

        String i3 = "";

        default void im3() {
        }
    }

    public static class Cls1 implements Inter1, Inter0 {

        public String c1;
        private String pc1;

        public void cm1() {
        }

        private void pcm1() {
        }
    }

    public static class Cls2 extends Cls1 implements Inter2, Inter0 {

        public String c2;
        private String pc2;

        public void cm2() {
        }

        private void pcm2() {
        }
    }

    public static class Cls3 extends Cls2 implements Inter3, Inter0 {

        static void staticMethod() {
        }

        public String c3;
        private String pc3;

        public Cls3() {
        }

        private Cls3(int i) {
        }

        public void cm3() {
        }

        private void pcm3() {
        }
    }

    public static class Mod1 {

        public static void m1() {
        }

        public void m2() {
        }

        protected void m3() {
        }

        private void m4() {
        }

        void m5() {
        }
    }

    public class Mod2 {}

    protected class Mod3 {}

    private class Mod4 {}

    class Mod5 {}
}
