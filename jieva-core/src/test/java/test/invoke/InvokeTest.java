package test.invoke;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.invoke.AsmBack;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.invoke.InvocationException;
import xyz.sunqian.common.invoke.InvocationMode;
import xyz.sunqian.common.invoke.JieHandle;
import xyz.sunqian.common.reflect.JieClass;
import xyz.sunqian.test.JieTestException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.expectThrows;

public class InvokeTest {

    @Test
    public void testAsm() throws Exception {
        AsmBack.test();
    }

    @Test
    public void testInvoke() throws Exception {
        Constructor<?> constructor = TestObject.class.getDeclaredConstructor();
        // test constructor
        expectThrows(InvocationException.class, () ->
            Invocable.of(constructor).invoke(null));
        expectThrows(InvocationException.class, () ->
            Invocable.of(constructor, InvocationMode.METHOD_HANDLE).invoke(null));
        constructor.setAccessible(true);
        // test methods
        Method[] methods = TestObject.class.getDeclaredMethods();
        for (Method method : methods) {
            expectThrows(InvocationException.class, () ->
                Invocable.of(method).invoke(null));
            expectThrows(InvocationException.class, () ->
                Invocable.of(method, InvocationMode.METHOD_HANDLE).invoke(null));
            method.setAccessible(true);
            if (method.getName().startsWith("sm") || method.getName().startsWith("m")) {
                testInvoke0(constructor, method, InvocationMode.REFLECTION);
                testInvoke0(constructor, method, InvocationMode.METHOD_HANDLE);
            }
        }
    }

    private void testInvoke0(
        Constructor<?> constructor, Method method, InvocationMode mode
    ) throws Exception {
        Invocable constructorCaller = Invocable.of(constructor, mode);
        TestObject tt = (TestObject) constructorCaller.invoke(null);
        assertNotNull(tt);
        String[] args = new String[method.getParameterCount()];
        for (int i = 0; i < args.length; i++) {
            args[i] = "" + i;
        }
        expectThrows(InvocationException.class, () ->
            Invocable.of(method, mode).invoke(null, 1, 2, 3));
        Invocable methodCaller = Invocable.of(method, mode);
        assertNotNull(methodCaller);
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        assertEquals(
            methodCaller.invoke(isStatic ? null : tt, (Object[]) args),
            buildString(method.getName(), args)
        );
        Invocable handle = Invocable.of(MethodHandles.lookup().unreflect(method), isStatic);
        assertNotNull(handle);
        assertEquals(
            handle.invoke(isStatic ? null : tt, (Object[]) args),
            buildString(method.getName(), args)
        );
    }

    private static String buildString(String name, String... args) {
        return name + ": " + String.join(", ", args);
    }

    @Test
    public void testError() throws Exception {
        testError(InvocationMode.REFLECTION);
        testError(InvocationMode.METHOD_HANDLE);
        testHandleError();
    }

    private void testError(InvocationMode mode) throws Exception {
        TestThrow tt = new TestThrow();
        Constructor<?> errorC = JieClass.getConstructor(TestThrow.class, Jie.array(int.class));
        Method errorStatic = JieClass.searchMethod(TestThrow.class, "errorStatic", Jie.array());
        Method error = JieClass.searchMethod(TestThrow.class, "error", Jie.array());
        expectThrows(InvocationException.class, () ->
            Invocable.of(errorC, mode).invoke(1));
        expectThrows(InvocationException.class, () ->
            Invocable.of(errorStatic, mode).invoke(null));
        expectThrows(InvocationException.class, () ->
            Invocable.of(error, mode).invoke(tt));
        expectThrows(JieTestException.class, () ->
            JieHandle.invokeStatic(MethodHandles.lookup().unreflect(errorStatic)));
        expectThrows(JieTestException.class, () ->
            JieHandle.invokeStatic(MethodHandles.lookup().unreflect(error), tt));
        try {
            Invocable.of(errorC, mode).invoke(null, 1);
        } catch (InvocationException e) {
            assertEquals(JieTestException.class, e.getCause().getClass());
        }
        try {
            Invocable.of(errorStatic, mode).invoke(null);
        } catch (InvocationException e) {
            assertEquals(JieTestException.class, e.getCause().getClass());
        }
        try {
            Invocable.of(error, mode).invoke(tt);
        } catch (InvocationException e) {
            assertEquals(JieTestException.class, e.getCause().getClass());
        }
    }

    private void testHandleError() throws Exception {
        TestThrow tt = new TestThrow();
        Constructor<?> errorC = JieClass.getConstructor(TestThrow.class, Jie.array(int.class));
        Method errorStatic = JieClass.searchMethod(TestThrow.class, "errorStatic", Jie.array());
        Method error = JieClass.searchMethod(TestThrow.class, "error", Jie.array());
        expectThrows(InvocationException.class, () ->
            Invocable.of(MethodHandles.lookup().unreflectConstructor(errorC), true).invoke(1));
        expectThrows(InvocationException.class, () ->
            Invocable.of(MethodHandles.lookup().unreflect(errorStatic), true).invoke(null));
        expectThrows(InvocationException.class, () ->
            Invocable.of(MethodHandles.lookup().unreflect(error), false).invoke(tt));
        try {
            Invocable.of(MethodHandles.lookup().unreflectConstructor(errorC), true).invoke(null, 1);
        } catch (InvocationException e) {
            assertEquals(JieTestException.class, e.getCause().getClass());
        }
        try {
            Invocable.of(MethodHandles.lookup().unreflect(errorStatic), true).invoke(null);
        } catch (InvocationException e) {
            assertEquals(JieTestException.class, e.getCause().getClass());
        }
        try {
            Invocable.of(MethodHandles.lookup().unreflect(error), false).invoke(tt);
        } catch (InvocationException e) {
            assertEquals(JieTestException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testInvokeSpecial() throws Throwable {
        Method tt = JieClass.searchMethod(TestInter.class, "tt", Jie.array());
        TestChild tc = new TestChild();
        Class<?> caller = tt.getDeclaringClass();
        MethodHandle handle = MethodHandles.lookup().in(caller).unreflectSpecial(tt, caller);
        assertEquals(handle.invoke(tc), "TestInter");
        // assertEquals(Invoker.handle(tt).invoke(tc), tc.tt());
        // assertEquals(Invoker.handle(tt).invoke(tc), "TestChild");
        // assertEquals(Invoker.handle(tt, TestChild.class).invoke(tc), "TestInter");
    }

    public static class TestObject {

        private TestObject() {
        }

        private static String sm0() {
            return buildString("sm0");
        }

        private static String sm1(String s1) {
            return buildString("sm1", s1);
        }

        private static String sm2(String s1, String s2) {
            return buildString("sm2", s1, s2);
        }

        private static String sm3(String s1, String s2, String s3) {
            return buildString("sm3", s1, s2, s3);
        }

        private static String sm4(String s1, String s2, String s3, String s4) {
            return buildString("sm4", s1, s2, s3, s4);
        }

        private static String sm5(String s1, String s2, String s3, String s4, String s5) {
            return buildString("sm5", s1, s2, s3, s4, s5);
        }

        private static String sm6(String s1, String s2, String s3, String s4, String s5, String s6) {
            return buildString("sm6", s1, s2, s3, s4, s5, s6);
        }

        private static String sm7(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
            return buildString("sm7", s1, s2, s3, s4, s5, s6, s7);
        }

        private static String sm8(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {
            return buildString("sm8", s1, s2, s3, s4, s5, s6, s7, s8);
        }

        private static String sm9(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9) {
            return buildString("sm9", s1, s2, s3, s4, s5, s6, s7, s8, s9);
        }

        private static String sm10(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10) {
            return buildString("sm10", s1, s2, s3, s4, s5, s6, s7, s8, s9, s10);
        }

        private static String sm11(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11) {
            return buildString("sm11", s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11);
        }

        private String m0() {
            return buildString("m0");
        }

        private String m1(String s1) {
            return buildString("m1", s1);
        }

        private String m2(String s1, String s2) {
            return buildString("m2", s1, s2);
        }

        private String m3(String s1, String s2, String s3) {
            return buildString("m3", s1, s2, s3);
        }

        private String m4(String s1, String s2, String s3, String s4) {
            return buildString("m4", s1, s2, s3, s4);
        }

        private String m5(String s1, String s2, String s3, String s4, String s5) {
            return buildString("m5", s1, s2, s3, s4, s5);
        }

        private String m6(String s1, String s2, String s3, String s4, String s5, String s6) {
            return buildString("m6", s1, s2, s3, s4, s5, s6);
        }

        private String m7(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
            return buildString("m7", s1, s2, s3, s4, s5, s6, s7);
        }

        private String m8(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {
            return buildString("m8", s1, s2, s3, s4, s5, s6, s7, s8);
        }

        private String m9(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9) {
            return buildString("m9", s1, s2, s3, s4, s5, s6, s7, s8, s9);
        }

        private String m10(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10) {
            return buildString("m10", s1, s2, s3, s4, s5, s6, s7, s8, s9, s10);
        }

        private String m11(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11) {
            return buildString("m11", s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11);
        }
    }

    public static class TestThrow {

        public static void errorStatic() {
            throw new JieTestException();
        }

        public TestThrow() {
        }

        public TestThrow(int i) {
            throw new JieTestException();
        }

        public void error() {
            throw new JieTestException();
        }
    }

    public interface TestInter {

        default String tt() {
            return "TestInter";
        }
    }

    public static class TestChild implements TestInter {

        @Override
        public String tt() {
            return "TestChild";
        }
    }
}


