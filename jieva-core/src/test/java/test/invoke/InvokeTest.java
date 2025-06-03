package test.invoke;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.JieStream;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.invoke.InvocationException;
import xyz.sunqian.common.invoke.InvocationMode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class InvokeTest {

    @Test
    public void testAsm() throws Exception {
        // AsmBack.test();
    }

    @Test
    public void testInvoke() throws Exception {
        Object a = Invocable.of(A.class.getConstructor()).invoke(null);
        assertTrue(a instanceof A);
        assertEquals(
            Invocable.of(A.class.getMethod("instanceMethod", String.class)).invoke(a, "aaa"),
            "aaa"
        );
        for (InvocationMode mode : InvocationMode.values()) {
            testInvokeConstructor(mode);
            testInvokeMethod(mode);
        }
    }

    private void testInvokeConstructor(InvocationMode mode) throws Exception {
        Constructor<?> pub = A.class.getConstructor();
        assertTrue(Invocable.of(pub, mode).invoke(null) instanceof A);
        Constructor<?> pri = A.class.getDeclaredConstructor(int.class);
        expectThrows(InvocationException.class, () -> Invocable.of(pri, mode).invoke(null));
        Constructor<?> err = A.class.getDeclaredConstructor(long.class);
        expectThrows(InvocationException.class, () -> Invocable.of(err, mode).invoke(null));
        try {
            Invocable.of(err, mode);
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
    }

    private void testInvokeMethod(InvocationMode mode) throws Exception {
        A a = new A();
        // instance
        Method pub = A.class.getMethod("instanceMethod", String.class);
        assertEquals(Invocable.of(pub, mode).invoke(a, "aaa"), a.instanceMethod("aaa"));
        expectThrows(InvocationException.class, () -> Invocable.of(pub, mode).invoke(null, "aaa"));
        Method pri = A.class.getDeclaredMethod("instancePrivateMethod", String.class);
        expectThrows(InvocationException.class, () -> Invocable.of(pri, mode).invoke(a, "aaa"));
        Method err = A.class.getDeclaredMethod("instanceThrowMethod", String.class);
        expectThrows(InvocationException.class, () -> Invocable.of(err, mode).invoke(a, "aaa"));
        try {
            Invocable.of(err, mode).invoke(a, "aaa");
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
        // static
        Method pubStatic = A.class.getMethod("staticMethod", String.class);
        assertEquals(Invocable.of(pubStatic, mode).invoke(null, "aaa"), A.staticMethod("aaa"));
        Method priStatic = A.class.getDeclaredMethod("staticPrivateMethod", String.class);
        expectThrows(InvocationException.class, () -> Invocable.of(priStatic, mode).invoke(null, "aaa"));
        Method errStatic = A.class.getDeclaredMethod("staticThrowMethod", String.class);
        expectThrows(InvocationException.class, () -> Invocable.of(errStatic, mode).invoke(null, "aaa"));
        try {
            Invocable.of(errStatic, mode).invoke(null, "aaa");
        } catch (InvocationException e) {
            assertTrue(e.getCause() instanceof InvokeTestException);
        }
    }

    @Test
    public void testForMethodHandle() throws Exception {
        MethodHandle handle1 = MethodHandles.lookup().unreflectConstructor(A.class.getConstructor());
        Object a = Invocable.of(handle1, true).invoke(null);
        assertTrue(a instanceof A);
        MethodHandle handle2 = MethodHandles.lookup().unreflect(A.class.getMethod("instanceMethod", String.class));
        assertEquals(
            Invocable.of(handle2, false).invoke(a, "aaa"),
            "aaa"
        );
    }

    @Test
    public void testLotsOfMethods() throws Exception {
        // static
        List<Method> staticMethods = JieStream.stream(LotsOfMethods.class.getMethods())
            .filter(method -> Modifier.isStatic(method.getModifiers()))
            .collect(Collectors.toList());
        for (Method method : staticMethods) {
            Invocable invocable = Invocable.of(method, InvocationMode.METHOD_HANDLE);
            assertEquals(
                invocable.invoke(null, buildArgsForLotsOfMethods(method)),
                method.invoke(null, buildArgsForLotsOfMethods(method))
            );
        }
        // instance
        List<Method> instanceMethods = JieStream.stream(LotsOfMethods.class.getMethods())
            .filter(method ->
                !Modifier.isStatic(method.getModifiers())
                    && method.getDeclaringClass().equals(LotsOfMethods.class))
            .collect(Collectors.toList());
        LotsOfMethods inst = new LotsOfMethods();
        for (Method method : instanceMethods) {
            Invocable invocable = Invocable.of(method, InvocationMode.METHOD_HANDLE);
            assertEquals(
                invocable.invoke(inst, buildArgsForLotsOfMethods(method)),
                method.invoke(inst, buildArgsForLotsOfMethods(method))
            );
        }
    }

    private Object[] buildArgsForLotsOfMethods(Method method) {
        Object[] args = new Object[method.getParameterCount()];
        Arrays.fill(args, "6");
        return args;
    }
}


