package tests.runtime.invoke;

import org.junit.jupiter.api.Test;
import tests.utils.LotsOfMethods;
import space.sunqian.common.Kit;
import space.sunqian.common.runtime.invoke.Invocable;
import space.sunqian.common.runtime.invoke.InvocationException;
import space.sunqian.common.runtime.invoke.InvocationMode;
import space.sunqian.common.runtime.invoke.InvokeKit;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvokeTest {

    @Test
    public void testInvoke() throws Throwable {
        Object a = Invocable.of(A.class.getConstructor()).invoke(null);
        assertTrue(a instanceof A);
        assertEquals(
            "aaa",
            Invocable.of(A.class.getMethod("instanceMethod", String.class)).invoke(a, "aaa")
        );
        for (InvocationMode mode : InvocationMode.values()) {
            testInvokeConstructor(mode);
            testInvokeMethod(mode);
        }
    }

    private void testInvokeConstructor(InvocationMode mode) throws Throwable {
        Constructor<?> pub = A.class.getConstructor();
        assertTrue(Invocable.of(pub, mode).invoke(null) instanceof A);
        assertTrue(Invocable.of(pub, mode).invokeChecked(null) instanceof A);
        Constructor<?> pri = A.class.getDeclaredConstructor(int.class);
        assertThrows(InvocationException.class, () -> Invocable.of(pri, mode).invoke(null, 1));
        Constructor<?> err = A.class.getConstructor(long.class);
        assertThrows(InvocationException.class, () -> Invocable.of(err, mode).invoke(null, 1L));
        try {
            Invocable.of(err, mode).invoke(null, 1L);
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        assertThrows(InvokeTestException.class, () -> Invocable.of(err, mode).invokeChecked(null, 1L));
    }

    private void testInvokeMethod(InvocationMode mode) throws Throwable {
        A a = new A();
        // instance
        Method pub = A.class.getMethod("instanceMethod", String.class);
        assertEquals(Invocable.of(pub, mode).invoke(a, "aaa"), a.instanceMethod("aaa"));
        assertEquals(Invocable.of(pub, mode).invokeChecked(a, "aaa"), a.instanceMethod("aaa"));
        assertThrows(InvocationException.class, () -> Invocable.of(pub, mode).invoke(null, "aaa"));
        Method pri = A.class.getDeclaredMethod("instancePrivateMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(pri, mode).invoke(a, "aaa"));
        Method err = A.class.getMethod("instanceThrowMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(err, mode).invoke(a, "aaa"));
        try {
            Invocable.of(err, mode).invoke(a, "aaa");
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        assertThrows(InvokeTestException.class, () -> Invocable.of(err, mode).invokeChecked(a, "aaa"));
        // static
        Method pubStatic = A.class.getMethod("staticMethod", String.class);
        assertEquals(Invocable.of(pubStatic, mode).invoke(null, "aaa"), A.staticMethod("aaa"));
        Method priStatic = A.class.getDeclaredMethod("staticPrivateMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(priStatic, mode).invoke(null, "aaa"));
        Method errStatic = A.class.getMethod("staticThrowMethod", String.class);
        assertThrows(InvocationException.class, () -> Invocable.of(errStatic, mode).invoke(null, "aaa"));
        try {
            Invocable.of(errStatic, mode).invoke(null, "aaa");
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        assertThrows(InvokeTestException.class, () -> Invocable.of(errStatic, mode).invokeChecked(null, "aaa"));
    }

    @Test
    public void testMethodHandle() throws Throwable {
        MethodHandle handle1 = MethodHandles.lookup().unreflectConstructor(A.class.getConstructor());
        Object a = Invocable.of(handle1, true).invoke(null);
        assertTrue(a instanceof A);
        MethodHandle handle2 = MethodHandles.lookup().unreflect(A.class.getMethod("instanceMethod", String.class));
        assertEquals(
            "aaa",
            Invocable.of(handle2, false).invoke(a, "aaa")
        );
        MethodHandle handle3 = MethodHandles.lookup().unreflect(A.class.getMethod("instanceMethod", String.class));
        assertEquals(
            Invocable.of(handle3, false).invoke(a, "aaa"),
            (String) handle3.invokeExact(new A(), "aaa")
        );
    }

    @Test
    public void testLotsOfMethods() throws Throwable {
        // static
        List<Method> staticMethods = Kit.stream(LotsOfMethods.class.getMethods())
            .filter(method -> method.getName().startsWith("staticMethod"))
            .collect(Collectors.toList());
        for (Method method : staticMethods) {
            Invocable invocable = Invocable.of(method, InvocationMode.METHOD_HANDLE);
            Object[] args = LotsOfMethods.buildArgsForLotsOfMethods(method);
            assertEquals(
                invocable.invoke(null, args),
                method.invoke(null, args)
            );
            MethodHandle handle = MethodHandles.lookup().unreflect(method);
            assertEquals(
                InvokeKit.invokeStatic(handle, args),
                method.invoke(null, args)
            );
            Invocable recommendedInvoker = Invocable.of(method);
            assertEquals(
                recommendedInvoker.invoke(null, args),
                method.invoke(null, args)
            );
        }
        // instance
        List<Method> instanceMethods = Kit.stream(LotsOfMethods.class.getMethods())
            .filter(method -> method.getName().startsWith("instanceMethod"))
            .collect(Collectors.toList());
        LotsOfMethods inst = new LotsOfMethods();
        for (Method method : instanceMethods) {
            Invocable invocable = Invocable.of(method, InvocationMode.METHOD_HANDLE);
            Object[] args = LotsOfMethods.buildArgsForLotsOfMethods(method);
            assertEquals(
                invocable.invoke(inst, args),
                method.invoke(inst, args)
            );
            MethodHandle handle = MethodHandles.lookup().unreflect(method);
            assertEquals(
                InvokeKit.invokeInstance(handle, inst, args),
                method.invoke(inst, args)
            );
            Invocable recommendedInvoker = Invocable.of(method);
            assertEquals(
                recommendedInvoker.invoke(inst, args),
                method.invoke(inst, args)
            );
        }
    }

    @Test
    public void testAsm() throws Exception {
        {
            // Cls
            List<Method> methods = Kit.stream(Cls.class.getMethods())
                .filter(method -> method.getDeclaringClass().equals(Cls.class))
                .collect(Collectors.toList());
            Cls inst = new Cls();
            for (Method method : methods) {
                Invocable invocable = Invocable.of(method, InvocationMode.ASM);
                assertEquals(
                    invocable.invoke(inst, method.getParameterCount() > 0 ? buildArgsForAsm() : new Object[0]),
                    method.invoke(inst, method.getParameterCount() > 0 ? buildArgsForAsm() : new Object[0])
                );
            }
        }
        {
            // Inter
            List<Method> methods = Kit.stream(Inter.class.getMethods())
                .filter(method -> method.getDeclaringClass().equals(Inter.class))
                .collect(Collectors.toList());
            Inter inst = new Inter() {};
            for (Method method : methods) {
                Invocable invocable = Invocable.of(method, InvocationMode.ASM);
                assertEquals(
                    invocable.invoke(inst, method.getParameterCount() > 0 ? buildArgsForAsm() : new Object[0]),
                    method.invoke(inst, method.getParameterCount() > 0 ? buildArgsForAsm() : new Object[0])
                );
            }
            class InterChild implements Inter {
                @Override
                public boolean b2(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
                    return false;
                }
            }
            InterChild ic = new InterChild();
            Method b2 = Inter.class.getMethod(
                "b2", boolean.class, byte.class, short.class, char.class, int.class, long.class, float.class, double.class, String.class);
            Invocable invocable = Invocable.of(b2, InvocationMode.ASM);
            assertEquals(invocable.invoke(ic, buildArgsForAsm()), ic.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
            assertNotEquals(invocable.invoke(inst, buildArgsForAsm()), ic.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        }
        {
            // big parameter count
            Method instanceMethod129 = Kit.stream(LotsOfMethods.class.getMethods())
                .filter(method -> "instanceMethod129".equals(method.getName()))
                .findFirst().get();
            Invocable invocable = Invocable.of(instanceMethod129, InvocationMode.ASM);
            assertEquals(
                invocable.invoke(new LotsOfMethods(), LotsOfMethods.buildArgsForLotsOfMethods(instanceMethod129)),
                instanceMethod129.invoke(new LotsOfMethods(), LotsOfMethods.buildArgsForLotsOfMethods(instanceMethod129))
            );
        }
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


