package test.reflect;

import org.objectweb.asm.MethodVisitor;
import org.testng.annotations.Test;
import test.JieTestException;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.reflect.NotPrimitiveException;
import xyz.fslabo.common.reflect.proxy.*;
import xyz.fslabo.test.JieTest;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class ProxyTest {

    @Test
    public void testAsmProxyProvider() throws Exception {
        ClassO inst = new ClassO(ClassP.DEFAULT_I * 2);
        TestHandler testHandler = new TestHandler(inst);
        ClassP proxy = JieProxy.asm(Jie.list(ClassP.class, Inter1.class, Inter2.class), testHandler);
        testAsm(proxy, "Inter1-proxy", true);

        ClassOO inst2 = new ClassOO(ClassP.DEFAULT_I * 2);
        TestHandler testHandler2 = new TestHandler(inst2);
        ClassP proxy2 = JieProxy.asm(Jie.list(ClassPP.class, Inter1.class, Inter2.class), testHandler2);
        testAsm(proxy2, "Inter2-proxy", false);

        Object proxyP = JieProxy.asm(Jie.list(ClassP.class), testHandler);
        testClass1((ClassP) proxyP);
        Inter1<?> proxyI1 = JieProxy.asm(Jie.list(Inter1.class), testHandler);
        testInter1(proxyI1, true);

        Object proxyI12 = JieProxy.asm(Jie.list(Inter1.class, Inter2.class), testHandler);
        testInter1((Inter1<?>) proxyI12, true);
        testInter2((Inter2<?>) proxyI12, true);
        ClassP proxyAsm = new AsmProxyProvider().newProxyInstance(
            getClass().getClassLoader(), Jie.list(ClassP.class, Inter1.class, Inter2.class), testHandler);
        testAsm(proxyAsm, "Inter1-proxy", true);

        expectThrows(ProxyException.class, () -> JieProxy.asm(null, testHandler));
        expectThrows(ProxyException.class, () -> JieProxy.asm(Jie.list(ClassP.class, ClassP.class), testHandler));
        expectThrows(ProxyException.class, () -> JieProxy.asm(Jie.list(ClassO.class), testHandler));

        MethodProxyHandler superHandle = new MethodProxyHandler() {
            @Override
            public boolean proxy(Method method) {
                return Objects.equals(method.getName(), "ppi_String");
            }

            @Override
            public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
                return invoker.invokeSuper(args);
            }
        };
        Inter2<?> ppi1 = JieProxy.asm(Jie.list(Inter1.class, Inter2.class), superHandle);
        assertEquals(ppi1.ppi_String(), "Inter1");
        Inter1<?> ppi2 = JieProxy.asm(Jie.list(Inter2.class, Inter1.class), superHandle);
        assertEquals(ppi2.ppi_String(), "Inter2");

        Object op = JieProxy.asm(Jie.list(Object.class), new MethodProxyHandler() {
            @Override
            public boolean proxy(Method method) {
                return true;
            }

            @Override
            public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
                if (method.getName().equals("equals")) {
                    return true;
                }
                if (method.getName().equals("hashCode")) {
                    return 666;
                }
                if (method.getName().equals("toString")) {
                    return "666";
                }
                return invoker.invokeSuper(args);
            }
        });
        assertEquals(op.equals("anyone"), true);
        assertEquals(op.hashCode(), 666);
        assertEquals(op.toString(), "666");

        // exception
        AsmProxyProvider asmProxyProvider = new AsmProxyProvider();
        Method method = AsmProxyProvider.class.getDeclaredMethod(
            "visitLoadPrimitiveParamAsObject", MethodVisitor.class, Class.class, int.class);
        JieTest.testThrow(NotPrimitiveException.class, method, asmProxyProvider, null, Object.class, 1);
        method = AsmProxyProvider.class.getDeclaredMethod(
            "visitObjectCastPrimitive", MethodVisitor.class, Class.class, boolean.class);
        JieTest.testThrow(NotPrimitiveException.class, method, asmProxyProvider, null, Object.class, true);
        method = AsmProxyProvider.class.getDeclaredMethod(
            "returnPrimitiveCastObject", MethodVisitor.class, Class.class);
        JieTest.testThrow(NotPrimitiveException.class, method, asmProxyProvider, null, Object.class);
    }

    private void testAsm(ClassP proxy, String ppi_String, boolean absError) {
        testClass1(proxy);
        testInter1((Inter1<?>) proxy, absError);
        testInter2((Inter2<?>) proxy, absError);
        Inter1<?> i1 = (Inter1<?>) proxy;
        assertEquals(i1.ppi_String(), ppi_String);
        assertEquals(i1.ppi_String(), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(i1.ppi1t_String(), "Inter1_t-proxy");
        assertEquals(i1.ppi_String(), TestHandler.superStack.get(0) + "-proxy");
        Inter2<?> i2 = (Inter2<?>) proxy;
        assertEquals(i2.ppi_String(), ppi_String);
        assertEquals(i2.ppi_String(), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(i2.ppi2t_Integer(), 8);
        assertEquals(i2.ppi2t_Integer(), (Integer) TestHandler.superStack.get(0) + 1);
    }

    private void testClass1(ClassP proxy) {
        assertEquals(proxy.ppc_boolean(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_byte(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_short(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_char(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_int(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_long(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_float(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_double(), TestHandler.superStack.get(0));
        assertEquals(proxy.ppc_String(), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true), true + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2), "" + true + 2 + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3), "" + true + 2 + 3 + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4'), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4'), "" + true + 2 + 3 + '4' + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5), "" + true + 2 + 3 + '4' + 5 + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6), "" + true + 2 + 3 + '4' + 5 + 6 + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7), "" + true + 2 + 3 + '4' + 5 + 6 + 7.0 + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8), "" + true + 2 + 3 + '4' + 5 + 6 + 7.0 + 8.0 + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8, "9"), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8, "9"), "" + true + 2 + 3 + '4' + 5 + 6 + 7.0 + 8.0 + "9" + "-proxy");
        proxy.ppc_void();
        assertEquals("ppc_void", TestHandler.superStack.get(0));
        assertEquals("ppc_void", TestHandler.superStack.get(1));
        expectThrows(JieTestException.class, proxy::ppc_Throw);
        assertEquals(ClassP.ppc_static(), "non-proxy");
        assertEquals(proxy.ppc_i(), 3);
        assertEquals(proxy.pp_Proxied("", 2), TestHandler.superStack.get(0));
        assertEquals(proxy.ppp_Proxied("", 2), TestHandler.superStack.get(0));
    }

    private void testInter1(Inter1<?> inter1, boolean absError) {
        if (absError) {
            expectThrows(AbstractMethodError.class, inter1::ppi1_boolean);
            expectThrows(AbstractMethodError.class, () -> inter1.ppi1_double(1, "a"));
        }
        expectThrows(JieTestException.class, inter1::ppi1_Throw);
        assertEquals(inter1.ppi1_String(1, 1, "s"), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(inter1.ppi1_String(1, 1, "s"), 1 + 1.0 + "s" + "-proxy");
        assertEquals(Inter1.ppi1_static(), "non-proxy");
    }

    private void testInter2(Inter2<?> inter2, boolean absError) {
        if (absError) {
            expectThrows(AbstractMethodError.class, inter2::ppi2_boolean);
            expectThrows(AbstractMethodError.class, () -> inter2.ppi2_double(1, "a"));
        }
        expectThrows(JieTestException.class, inter2::ppi2_Throw);
        assertEquals(inter2.ppi2_String(1, 1, "s"), TestHandler.superStack.get(0) + "-proxy");
        assertEquals(inter2.ppi2_String(1, 1, "s"), 1 + 1.0 + "s" + "-proxy");
        assertEquals(Inter2.ppi2_static(), "non-proxy");
    }

    @Test
    public void testJdkProxyProvider() throws Exception {
        Inter12 inst = new Inter12();
        TestHandler testHandler = new TestHandler(inst);
        Object proxy = JieProxy.jdk(null, Jie.list(Inter1.class, Inter2.class), testHandler);
        Inter1<?> i1 = (Inter1<?>) proxy;
        expectThrows(AbstractMethodError.class, i1::ppi1_boolean);
        expectThrows(JieTestException.class, i1::ppi1_Throw);
        expectThrows(ProxyException.class, i1::ppi1_nonProxied);
        Inter2<?> i2 = (Inter2<?>) proxy;
        expectThrows(AbstractMethodError.class, i2::ppi2_boolean);
        expectThrows(JieTestException.class, i2::ppi2_Throw);
        assertEquals(Inter2.ppi2_static(), "non-proxy");

        // test default method
        expectThrows(ProxyException.class, i1::ppi_String);
        assertEquals(Inter1.ppi1_static(), "non-proxy");
        expectThrows(ProxyException.class, i2::ppi_String);
        expectThrows(AbstractMethodError.class, i2::ppi2_nonProxied);

        // test ProxyInvoker
        MethodProxyHandler handler = new MethodProxyHandler() {

            @Override
            public boolean proxy(Method method) {
                return method.getName().startsWith("pp") && !method.getName().endsWith("_nonProxied");
            }

            @Override
            public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
                if (method.getName().endsWith("_boolean")) {
                    return true;
                }
                if (method.getName().endsWith("_double")) {
                    return args[0];
                }
                if (method.getName().endsWith("_String")) {
                    return "proxy";
                }
                if (method.getName().endsWith("_Integer")) {
                    return 6;
                }
                if (method.getName().endsWith("_Proxied")) {
                    return invoker.invoke(inst, args);
                }
                return null;
            }
        };
        Object proxy2 = JieProxy.jdk(getClass().getClassLoader(), Jie.list(Inter1.class, Inter2.class), handler);
        Inter1<?> i11 = (Inter1<?>) proxy2;
        assertEquals(i11.ppi1_boolean(), true);
        assertEquals(i11.ppi1_double(6.6, "a"), 6.6);
        assertEquals(i11.ppi_String(), "proxy");
        assertEquals(i11.ppi1_Proxied("qq", "ww"), "qqww");
        assertEquals(i11.ppi1_Proxied("qq", "ww", "ee"), "qqwwee");
        Inter2<?> i22 = (Inter2<?>) proxy2;
        assertEquals(i22.ppi2_boolean(), true);
        assertEquals(i22.ppi2_double(7.7, "a"), 7.7);
        assertEquals(i22.ppi_String(), "proxy");
        assertEquals(i22.ppi2_Proxied("11", "22"), "1122");
        assertEquals(i22.ppi2_Proxied("11", "22", "33"), "112233");
    }

    public static class TestHandler implements MethodProxyHandler {

        public static final List<Object> superStack = new LinkedList<>();

        private final Object inst;

        public TestHandler(Object inst) {
            this.inst = inst;
        }

        @Override
        public boolean proxy(Method method) {
            return (method.getName().startsWith("pp")
                || method.getName().equals("callSuper")
                || method.getName().equals("callVirtual"))
                && !method.getName().endsWith("nonProxied");
        }

        @Override
        public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
            superStack.clear();
            if (Objects.equals(method.getReturnType(), void.class)) {
                invoker.invoke(inst, args);
                invoker.invokeSuper(args);
                return null;
            }
            if (inst instanceof ClassP && Objects.equals(ClassP.class.getMethod("ppc_i"), method)) {
                Object ppci = ((ClassP) inst).ppc_i();
                Object result1 = invoker.invoke(inst, args);
                Object result2 = invoker.invokeSuper(args);
                assertEquals(ppci, ClassP.DEFAULT_I * 2);
                assertEquals(ppci, result1);
                assertEquals(result2, ClassP.DEFAULT_I);
                return result2;
            }
            Object result1 = invoker.invoke(inst, args);
            Object result2 = invoker.invokeSuper(args);
            assertEquals(result1, result2);
            superStack.add(result2);
            if (method.getName().endsWith("_String")) {
                return result2 + "-proxy";
            }
            if (method.getName().endsWith("_Integer")) {
                return (Integer) result2 + 1;
            }
            return result2;
        }
    }

    public interface Inter1<T> {

        static String ppi1_static() {
            return "non-proxy";
        }

        boolean ppi1_boolean();

        double ppi1_double(double d, String a);

        default String ppi1_String(int i, double d, String s) {
            return i + d + s;
        }

        default String ppi1_String(String s, long l) {
            return s + l;
        }

        default String ppi_String() {
            return "Inter1";
        }

        default T ppi1t_String() {
            return (T) "Inter1_t";
        }

        default void ppi1_Throw() {
            throw new JieTestException();
        }

        default void ppi1_nonProxied() {
        }

        String ppi1_Proxied(String s1, String s2);

        default String ppi1_Proxied(String s1, String s2, String s3) {
            return s1 + s2 + s3;
        }
    }

    public interface Inter2<T> {

        static String ppi2_static() {
            return "non-proxy";
        }

        boolean ppi2_boolean();

        double ppi2_double(double d, String a);

        default String ppi2_String(int i, double d, String s) {
            return i + d + s;
        }

        default String ppi_String() {
            return "Inter2";
        }

        default T ppi2t_Integer() {
            return (T) (new Integer(7));
        }

        default void ppi2_Throw() {
            throw new JieTestException();
        }

        void ppi2_nonProxied();

        String ppi2_Proxied(String s1, String s2);

        default String ppi2_Proxied(String s1, String s2, String s3) {
            return s1 + s2 + s3;
        }
    }

    public static class ClassP {

        static final int DEFAULT_I = 3;

        private final int i;

        public ClassP(int i) {
            this.i = i;
        }

        public ClassP() {
            this.i = DEFAULT_I;
        }

        public int ppc_i() {
            return i;
        }

        public static String ppc_static() {
            return "non-proxy";
        }

        public boolean ppc_boolean() {
            return true;
        }

        public byte ppc_byte() {
            return 1;
        }

        public short ppc_short() {
            return 1;
        }

        public char ppc_char() {
            return 1;
        }

        public int ppc_int() {
            return 1;
        }

        public long ppc_long() {
            return 1;
        }

        public float ppc_float() {
            return 1;
        }

        public double ppc_double() {
            return 1;
        }

        public void ppc_void() {
            TestHandler.superStack.add("ppc_void");
        }

        public String ppc_String() {
            return "1";
        }

        public String ppc_String(boolean a) {
            return "" + a;
        }

        public String ppc_String(boolean a, byte b) {
            return "" + a + b;
        }

        public String ppc_String(boolean a, byte b, short c) {
            return "" + a + b + c;
        }

        public String ppc_String(boolean a, byte b, short c, char d) {
            return "" + a + b + c + d;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e) {
            return "" + a + b + c + d + e;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f) {
            return "" + a + b + c + d + e + f;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g) {
            return "" + a + b + c + d + e + f + g;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g, double h) {
            return "" + a + b + c + d + e + f + g + h;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g, double h, String i) {
            return "" + a + b + c + d + e + f + g + h + i;
        }

        public void ppc_Throw() {
            throw new JieTestException();
        }

        public String pp_Proxied(String s1, long s2) {
            return "ClassP";
        }

        public String ppp_Proxied(String s1, long s2) {
            return "ClassP";
        }

        public Object callSuper(int i, Object obj, Object[] args) throws Throwable {
            switch (i) {
                case 0:
                    return super.hashCode();
                case 1:
                    return super.equals(args[0]);
                case 2:
                    super.wait((Long) args[0], (Integer) args[1]);
                case 3:
                    return super.getClass();
            }
            return null;
        }

        public Object callVirtual(int i, Object obj, Object[] args) {
            switch (i) {
                case 0:
                    return ((ClassP) obj).ppc_boolean();
                case 1:
                    return ((ClassP) obj).ppc_String((Boolean) args[0]);
                case 2:
                    return ((ClassP) obj).ppc_String((Boolean) args[0], (Byte) args[1]);
                case 3:
                    return ((ClassP) obj).hashCode();
            }
            return null;
        }
    }

    public static class Inter12 implements Inter1<String>, Inter2<Integer> {

        @Override
        public boolean ppi1_boolean() {
            return false;
        }

        @Override
        public double ppi1_double(double d, String a) {
            return 66;
        }

        @Override
        public String ppi_String() {
            return "Inter12";
        }

        @Override
        public Integer ppi2t_Integer() {
            return 6;
        }

        @Override
        public void ppi2_nonProxied() {
        }

        @Override
        public String ppi2_Proxied(String s1, String s2) {
            return s1 + s2;
        }

        @Override
        public String ppi1t_String() {
            return "Inter12_t";
        }

        @Override
        public String ppi1_Proxied(String s1, String s2) {
            return s1 + s2;
        }

        @Override
        public boolean ppi2_boolean() {
            return false;
        }

        @Override
        public double ppi2_double(double d, String a) {
            return 88;
        }

        public Object callVirtual(int i, Object obj, Object[] args) {
            switch (i) {
                case 0:
                    return ((Inter1<?>) obj).ppi1_boolean();
                case 1:
                    return ((Inter1<?>) obj).ppi1_double((Double) args[0], (String) args[1]);
                case 2:
                    return ((Inter1<?>) obj).ppi1_String((Integer) args[0], (Double) args[1], (String) args[2]);
                case 3:
                    return ((Inter1<?>) obj).hashCode();
            }
            return null;
        }
    }

    public static class ClassO extends ClassP implements Inter1<String>, Inter2<Integer> {

        public ClassO(int i) {
            super(i);
        }

        @Override
        public boolean ppi1_boolean() {
            return false;
        }

        @Override
        public double ppi1_double(double d, String a) {
            return 0;
        }

        @Override
        public String ppi1_String(int i, double d, String s) {
            return Inter1.super.ppi1_String(i, d, s);
        }

        @Override
        public String ppi_String() {
            return Inter1.super.ppi_String();
        }

        @Override
        public void ppi2_nonProxied() {
        }

        @Override
        public String ppi2_Proxied(String s1, String s2) {
            return s1 + s2;
        }

        @Override
        public String ppi1_Proxied(String s1, String s2) {
            return s1 + s2;
        }

        @Override
        public boolean ppi2_boolean() {
            return false;
        }

        @Override
        public double ppi2_double(double d, String a) {
            return 0;
        }
    }

    public abstract static class ClassPP extends ClassP implements Inter1<String>, Inter2<Integer> {

        public ClassPP(int i) {
            super(i);
        }

        public ClassPP() {
            super();
        }

        // @Override
        // public boolean ppi1_boolean() {
        //     return false;
        // }

        @Override
        public double ppi1_double(double d, String a) {
            return 66;
        }

        @Override
        public String ppi_String() {
            return Inter2.super.ppi_String();
        }

        @Override
        public boolean ppi2_boolean() {
            return false;
        }

        @Override
        public double ppi2_double(double d, String a) {
            return 77;
        }
    }

    public static class ClassOO extends ClassPP {

        public ClassOO(int i) {
            super(i);
        }

        @Override
        public boolean ppi1_boolean() {
            return false;
        }

        @Override
        public String ppi1_Proxied(String s1, String s2) {
            return s1 + s2;
        }

        @Override
        public void ppi2_nonProxied() {
        }

        @Override
        public String ppi2_Proxied(String s1, String s2) {
            return s1 + s2;
        }
    }
}
