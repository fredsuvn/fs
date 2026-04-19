package tests.core.invoke;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.invoke.InvocationException;
import space.sunqian.fs.invoke.InvocationMode;
import space.sunqian.fs.invoke.InvokeKit;
import tests.utils.LotsOfMethods;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvokeTest {

    @Test
    public void testInvoke() throws Throwable {
        Object a = createInstance();
        assertTrue(a instanceof A);
        assertEquals(
            "aaa",
            invokeInstanceMethod(a, "instanceMethod", "aaa")
        );
        for (InvocationMode mode : InvocationMode.values()) {
            testInvokeConstructor(mode);
            testInvokeMethod(mode);
        }
    }

    private Object createInstance() throws Throwable {
        return Invocable.of(A.class.getConstructor()).invoke(null);
    }

    private String invokeInstanceMethod(Object instance, String methodName, String arg) throws Throwable {
        return (String) Invocable.of(A.class.getMethod(methodName, String.class)).invoke(instance, arg);
    }

    private void testInvokeConstructor(InvocationMode mode) throws Throwable {
        Constructor<?> publicConstructor = A.class.getConstructor();
        assertTrue(Invocable.of(publicConstructor, mode).invoke(null) instanceof A);
        assertTrue(Invocable.of(publicConstructor, mode).invokeDirectly(null) instanceof A);

        Constructor<?> privateConstructor = A.class.getDeclaredConstructor(int.class);
        assertThrows(InvocationException.class, () -> Invocable.of(privateConstructor, mode).invoke(null, 1));

        Constructor<?> throwingConstructor = A.class.getConstructor(long.class);
        assertThrows(InvocationException.class, () -> Invocable.of(throwingConstructor, mode).invoke(null, 1L));
        try {
            Invocable.of(throwingConstructor, mode).invoke(null, 1L);
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        assertThrows(InvokeTestException.class, () -> Invocable.of(throwingConstructor, mode).invokeDirectly(null, 1L));
    }

    private void testInvokeMethod(InvocationMode mode) throws Throwable {
        A instance = new A();
        testInstanceMethods(mode, instance);
        testStaticMethods(mode);
    }

    private void testInstanceMethods(InvocationMode mode, A instance) throws Throwable {
        Method publicMethod = A.class.getMethod("instanceMethod", String.class);
        assertEquals(Invocable.of(publicMethod, mode).invoke(instance, "aaa"), instance.instanceMethod("aaa"));
        assertEquals(Invocable.of(publicMethod, mode).invokeDirectly(instance, "aaa"), instance.instanceMethod("aaa"));
        assertThrows(InvocationException.class, () -> Invocable.of(publicMethod, mode).invoke(null, "aaa"));

        Method privateMethod = A.class.getDeclaredMethod("instancePrivateMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(privateMethod, mode).invoke(instance, "aaa"));

        Method throwingMethod = A.class.getMethod("instanceThrowMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(throwingMethod, mode).invoke(instance, "aaa"));
        try {
            Invocable.of(throwingMethod, mode).invoke(instance, "aaa");
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        assertThrows(InvokeTestException.class, () -> Invocable.of(throwingMethod, mode).invokeDirectly(instance, "aaa"));
    }

    private void testStaticMethods(InvocationMode mode) throws Throwable {
        Method publicStaticMethod = A.class.getMethod("staticMethod", String.class);
        assertEquals(Invocable.of(publicStaticMethod, mode).invoke(null, "aaa"), A.staticMethod("aaa"));

        Method privateStaticMethod = A.class.getDeclaredMethod("staticPrivateMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(privateStaticMethod, mode).invoke(null, "aaa"));

        Method throwingStaticMethod = A.class.getMethod("staticThrowMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(throwingStaticMethod, mode).invoke(null, "aaa"));
        try {
            Invocable.of(throwingStaticMethod, mode).invoke(null, "aaa");
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        assertThrows(InvokeTestException.class, () -> Invocable.of(throwingStaticMethod, mode).invokeDirectly(null, "aaa"));
    }

    @Test
    public void testMethodHandle() throws Throwable {
        MethodHandle constructorHandle = MethodHandles.lookup().unreflectConstructor(A.class.getConstructor());
        Object instance = Invocable.of(constructorHandle, true).invoke(null);
        assertTrue(instance instanceof A);

        MethodHandle methodHandle = MethodHandles.lookup().unreflect(A.class.getMethod("instanceMethod", String.class));
        assertEquals(
            "aaa",
            Invocable.of(methodHandle, false).invoke(instance, "aaa")
        );

        MethodHandle exactHandle = MethodHandles.lookup().unreflect(A.class.getMethod("instanceMethod", String.class));
        assertEquals(
            Invocable.of(exactHandle, false).invoke(instance, "aaa"),
            (String) exactHandle.invokeExact(new A(), "aaa")
        );
    }

    @Test
    public void testLotsOfMethods() throws Throwable {
        testStaticMethodsWithDifferentInvocationModes();
        testInstanceMethodsWithDifferentInvocationModes();
    }

    private void testStaticMethodsWithDifferentInvocationModes() throws Throwable {
        List<Method> staticMethods = Fs.stream(LotsOfMethods.class.getMethods())
            .filter(method -> method.getName().startsWith("staticMethod"))
            .collect(Collectors.toList());

        for (Method method : staticMethods) {
            testMethodWithDifferentModes(method, null);
        }
    }

    private void testInstanceMethodsWithDifferentInvocationModes() throws Throwable {
        List<Method> instanceMethods = Fs.stream(LotsOfMethods.class.getMethods())
            .filter(method -> method.getName().startsWith("instanceMethod"))
            .collect(Collectors.toList());

        LotsOfMethods instance = new LotsOfMethods();
        for (Method method : instanceMethods) {
            testMethodWithDifferentModes(method, instance);
        }
    }

    private void testMethodWithDifferentModes(Method method, Object instance) throws Throwable {
        Object[] args = LotsOfMethods.buildArgsForLotsOfMethods(method);

        // Test with METHOD_HANDLE mode
        Invocable methodHandleInvocable = Invocable.of(method, InvocationMode.METHOD_HANDLE);
        assertEquals(
            methodHandleInvocable.invoke(instance, args),
            method.invoke(instance, args)
        );

        // Test with InvokeKit
        MethodHandle handle = MethodHandles.lookup().unreflect(method);
        if (instance == null) {
            assertEquals(
                InvokeKit.invokeStatic(handle, args),
                method.invoke(null, args)
            );
        } else {
            assertEquals(
                InvokeKit.invokeInstance(handle, instance, args),
                method.invoke(instance, args)
            );
        }

        // Test with recommended mode
        Invocable recommendedInvocable = Invocable.of(method);
        assertEquals(
            recommendedInvocable.invoke(instance, args),
            method.invoke(instance, args)
        );
    }

    @Test
    public void testAsm() throws Exception {
        testAsmWithClass();
        testAsmWithInterface();
        testAsmWithBigParameterCount();
    }

    private void testAsmWithClass() throws Exception {
        List<Method> methods = Fs.stream(Cls.class.getMethods())
            .filter(method -> method.getDeclaringClass().equals(Cls.class))
            .collect(Collectors.toList());

        Cls instance = new Cls();
        for (Method method : methods) {
            testAsmMethod(method, instance);
        }
    }

    private void testAsmWithInterface() throws Exception {
        List<Method> methods = Fs.stream(Inter.class.getMethods())
            .filter(method -> method.getDeclaringClass().equals(Inter.class))
            .collect(Collectors.toList());

        Inter instance = new Inter() {};
        for (Method method : methods) {
            testAsmMethod(method, instance);
        }

        testAsmWithInterfaceImplementation();
    }

    private void testAsmWithInterfaceImplementation() throws Exception {
        class InterChild implements Inter {
            @Override
            public boolean b2(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
                return false;
            }
        }

        InterChild childInstance = new InterChild();
        Inter defaultInstance = new Inter() {};
        Method b2Method = Inter.class.getMethod(
            "b2", boolean.class, byte.class, short.class, char.class, int.class, long.class, float.class, double.class, String.class);

        Invocable invocable = Invocable.of(b2Method, InvocationMode.ASM);
        Object[] args = buildArgsForAsm();

        assertEquals(invocable.invoke(childInstance, args), childInstance.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertNotEquals(invocable.invoke(defaultInstance, args), childInstance.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
    }

    private void testAsmWithBigParameterCount() throws Exception {
        Method instanceMethod129 = Fs.stream(LotsOfMethods.class.getMethods())
            .filter(method -> "instanceMethod129".equals(method.getName()))
            .findFirst().get();

        Invocable invocable = Invocable.of(instanceMethod129, InvocationMode.ASM);
        LotsOfMethods instance = new LotsOfMethods();
        Object[] args = LotsOfMethods.buildArgsForLotsOfMethods(instanceMethod129);

        assertEquals(
            invocable.invoke(instance, args),
            instanceMethod129.invoke(instance, args)
        );
    }

    private void testAsmMethod(Method method, Object instance) throws Exception {
        Invocable invocable = Invocable.of(method, InvocationMode.ASM);
        Object[] args = method.getParameterCount() > 0 ? buildArgsForAsm() : new Object[0];

        assertEquals(
            invocable.invoke(instance, args),
            method.invoke(instance, args)
        );
    }

    private Object[] buildArgsForAsm() {
        return new Object[]{
            true,
            (byte) 1,
            (short) 2,
            '3',
            4,
            5L,
            6.0f,
            7.0,
            "8"
        };
    }

    @Test
    public void testEmpty() {
        assertNull(Invocable.empty().invoke(null));
        assertSame(Invocable.empty(), Invocable.empty());
    }

    public static class Cls {

        public void b1() {
        }

        public boolean b2(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return true;
        }

        public byte b3(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 11;
        }

        public short b4(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 22;
        }

        public char b5(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 33;
        }

        public int b6(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 44;
        }

        public long b7(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 55;
        }

        public float b8(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 66;
        }

        public double b9(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 77;
        }

        public String b10(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return "88";
        }

        public <T> T b11(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, T a8) {
            return a8;
        }
    }

    public interface Inter {

        default void b1() {
        }

        default boolean b2(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return true;
        }

        default byte b3(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 11;
        }

        default short b4(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 22;
        }

        default char b5(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 33;
        }

        default int b6(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 44;
        }

        default long b7(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 55;
        }

        default float b8(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 66;
        }

        default double b9(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 77;
        }

        default String b10(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return "88";
        }

        default <T> T b11(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, T a8) {
            return a8;
        }
    }
}


