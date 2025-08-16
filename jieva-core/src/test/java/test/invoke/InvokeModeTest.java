package test.invoke;

import org.testng.annotations.Test;
import xyz.sunqian.common.invoke.InvocationBy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

public class InvokeModeTest {

    @Test
    public void testRecommended() throws Exception {
        Constructor<?>[] cs = Cls.class.getDeclaredConstructors();
        for (Constructor<?> c : cs) {
            assertEquals(
                InvocationBy.recommended(c),
                c.getParameterCount() == 0 ? InvocationBy.METHOD_HANDLE : InvocationBy.REFLECTION
            );
        }
        Method[] ms = Cls.class.getDeclaredMethods();
        for (Method m : ms) {
            assertEquals(
                InvocationBy.recommended(m),
                m.getParameterCount() == 0 ? InvocationBy.METHOD_HANDLE : InvocationBy.REFLECTION
            );
        }
    }

    private static class Cls {

        public Cls() {
        }

        public Cls(
            int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9,
            int a10, int a11, int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19
        ) {
        }

        public void mm() {
        }

        public void mm(
            int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9,
            int a10, int a11, int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19
        ) {
        }
    }
}
