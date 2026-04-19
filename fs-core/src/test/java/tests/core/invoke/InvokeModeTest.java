package tests.core.invoke;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.invoke.InvocationMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvokeModeTest {

    @Test
    public void testRecommended() throws Exception {
        testRecommendedForConstructors();
        testRecommendedForMethods();
    }

    private void testRecommendedForConstructors() throws Exception {
        Constructor<?>[] constructors = TestClass.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            InvocationMode expectedMode = constructor.getParameterCount() == 0
                ? InvocationMode.METHOD_HANDLE
                : InvocationMode.REFLECTION;
            assertEquals(InvocationMode.recommended(constructor), expectedMode);
        }
    }

    private void testRecommendedForMethods() throws Exception {
        Method[] methods = TestClass.class.getDeclaredMethods();
        for (Method method : methods) {
            InvocationMode expectedMode = method.getParameterCount() == 0
                ? InvocationMode.METHOD_HANDLE
                : InvocationMode.REFLECTION;
            assertEquals(InvocationMode.recommended(method), expectedMode);
        }
    }

    private static class TestClass {

        public TestClass() {
        }

        public TestClass(
            int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9,
            int a10, int a11, int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19
        ) {
        }

        public void methodWithoutParameters() {
        }

        public void methodWithParameters(
            int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9,
            int a10, int a11, int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19
        ) {
        }
    }
}
