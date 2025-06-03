package test.reflect.proxy;

import org.testng.annotations.Test;
import test.utils.LotsOfMethods;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.collect.JieStream;
import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;
import xyz.sunqian.common.reflect.proxy.asm.AsmProxyClassGenerator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class AsmProxyTest {

    @Test
    public void testProxyClass() {
        IntVar counter = IntVar.of(0);
        {
            // InterA
            ProxyClass pc = generateProxy(null, Jie.list(InterA.class), counter);
            InterA a = pc.newInstance();
            testInterA(a, counter);
            counter.set(0);
        }
        {
            // InterB
            ProxyClass pc = generateProxy(null, Jie.list(InterB.class), counter);
            InterB b = pc.newInstance();
            testInterB(b, counter);
            counter.set(0);
        }
        {
            // InterC
            ProxyClass pc = generateProxy(null, Jie.list(InterC.class), counter);
            InterC c1 = pc.newInstance();
            InterC<String> c2 = pc.newInstance();
            testInterC(c1, c2, counter);
            counter.set(0);
        }
        {
            // InterD
            ProxyClass pc1 = generateProxy(null, Jie.list(InterD.class), counter);
            InterD d1 = pc1.newInstance();
            testInterD(d1, new ClsD(), counter);
            counter.set(0);
            ProxyClass pc2 = generateProxy(ClsD.class, Jie.list(), counter);
            InterD d2 = pc2.newInstance();
            testInterD(d2, new ClsD(), counter);
            counter.set(0);
        }
        {
            // ClsA
            ProxyClass pc = generateProxy(ClsA.class, Jie.list(), counter);
            ClsA a = pc.newInstance();
            testClsA(a, counter);
            counter.set(0);
        }
        {
            // ClsB
            ProxyClass pc = generateProxy(ClsB.class, Jie.list(), counter);
            ClsB b1 = pc.newInstance();
            ClsB<String> b2 = pc.newInstance();
            testClsB(b1, b2, counter);
            counter.set(0);
        }
        {
            // ClsC
            ProxyClass pc = generateProxy(ClsC.class, Jie.list(), counter);
            ClsC c = pc.newInstance();
            testClsC(c, counter);
            counter.set(0);
        }
        {
            // ClsA, InterA, InterB, InterC
            ProxyClass pc = generateProxy(ClsA.class, Jie.list(InterA.class, InterB.class, InterC.class), counter);
            Object obj = pc.newInstance();
            testClsA((ClsA) obj, counter);
            counter.set(0);
            testInterA((InterA) obj, counter);
            counter.set(0);
            testInterB((InterB) obj, counter);
            counter.set(0);
            testInterC((InterC) obj, (InterC<String>) obj, counter);
            counter.set(0);
        }
        {
            // SameMethod
            expectThrows(ClassFormatError.class, () ->
                generateProxy(null, Jie.list(SameMethodA.class, SameMethodB.class), counter));
            ProxyClassGenerator generator = new AsmProxyClassGenerator();
            ProxyClass pc = generator.generate(
                null, Jie.list(SameMethodA.class, SameMethodB.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getDeclaringClass().equals(SameMethodA.class);
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        counter.incrementAndGet();
                        return invoker.invokeSuper(proxy, args);
                    }
                }
            );
            SameMethodA obj = pc.newInstance();
            assertFalse(obj.equals(""));
            assertEquals(counter.get(), 0);
            assertEquals(obj.hashCode(), Jie.hashId(obj));
            assertEquals(counter.get(), 0);
            assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
            assertEquals(counter.get(), 0);
            testSS(obj, (SameMethodB) obj, counter);
            assertEquals(obj.ss(111), 111 * 2);
            counter.set(0);
        }
        {
            // test class
            ProxyClass pc = generateProxy(ClsC.class, Jie.list(), counter);
            Class<?> c = pc.getProxyClass();
            Constructor<?>[] constructors = c.getConstructors();
            assertEquals(constructors.length, 1);
            assertEquals(
                Jie.list(constructors[0].getParameterTypes()),
                Jie.list(ProxyMethodHandler.class, Method[].class)
            );
            assertTrue(Modifier.isPublic(constructors[0].getModifiers()));
            counter.set(0);
        }
    }

    @Test
    public void testProxyInvoker() {
        {
            // invokeSuper
            ProxyClassGenerator generator = new AsmProxyClassGenerator();
            IntVar counter = IntVar.of(0);
            ProxyClass pc = generator.generate(
                ClsA.class, Jie.list(SameMethodA.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(SameMethodA.class);
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        counter.getAndIncrement();
                        return invoker.invokeSuper(proxy, args);
                    }
                }
            );
            class ClaAProxy extends ClsA implements SameMethodA {}
            Object asmProxy = pc.newInstance();
            ClaAProxy manProxy = new ClaAProxy();
            assertEquals(counter.get(), 0);
            assertEquals(((ClsA) asmProxy).a1("666"), manProxy.a1("666"));
            assertEquals(counter.get(), 2);
            assertEquals(((SameMethodA) asmProxy).ss(999), manProxy.ss(999));
            assertEquals(counter.get(), 3);
        }
        {
            // invoke
            ProxyClassGenerator generator = new AsmProxyClassGenerator();
            IntVar counter = IntVar.of(0);
            class ClaAProxy extends ClsA implements SameMethodA {
                @Override
                public String a1(String a0) {
                    return super.a1(a0) + "888";
                }
            }
            ClaAProxy manProxy = new ClaAProxy();
            ProxyClass pc = generator.generate(
                ClsA.class, Jie.list(SameMethodA.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(SameMethodA.class);
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        counter.getAndIncrement();
                        return invoker.invoke(manProxy, args);
                    }
                }
            );
            Object asmProxy = pc.newInstance();
            assertEquals(counter.get(), 0);
            assertEquals(((ClsA) asmProxy).a1("666"), manProxy.a1("666"));
            assertEquals(((ClsA) asmProxy).a1("666"), "666888");
            assertEquals(counter.get(), 2);
            assertEquals(((SameMethodA) asmProxy).ss(999), manProxy.ss(999));
            assertEquals(counter.get(), 3);
        }
        {
            // stack overflow
            ProxyClassGenerator generator = new AsmProxyClassGenerator();
            ProxyClass pc = generator.generate(
                ClsA.class, Jie.list(SameMethodA.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(SameMethodA.class);
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invoke(proxy, args);
                    }
                }
            );
            ClsA asmProxy1 = pc.newInstance();
            expectThrows(StackOverflowError.class, () -> asmProxy1.a1(""));
            SameMethodA asmProxy2 = pc.newInstance();
            expectThrows(StackOverflowError.class, () -> asmProxy2.ss(1));
        }
        {
            // throws directly
            ProxyClassGenerator generator = new AsmProxyClassGenerator();
            ProxyClass pc1 = generator.generate(
                ClsA.class, Jie.list(SameMethodA.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(SameMethodA.class);
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        throw new ProxyTestException();
                    }
                }
            );
            ClsA a1 = pc1.newInstance();
            expectThrows(ProxyTestException.class, () -> a1.a1(""));
            SameMethodA s1 = pc1.newInstance();
            expectThrows(ProxyTestException.class, () -> s1.ss(1));
            ProxyClass pc2 = generator.generate(
                ClsA.class, Jie.list(SameMethodA.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(SameMethodA.class);
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invokeSuper(proxy, args);
                    }
                }
            );
            ClsA a2 = pc2.newInstance();
            expectThrows(ProxyTestException.class, () -> a2.throwClsEx());
            SameMethodA s2 = pc1.newInstance();
            expectThrows(ProxyTestException.class, () -> s2.throwInterEx());
        }
        {
            // indirect super: A extends B extends C: A super-> C
            ProxyClassGenerator generator = new AsmProxyClassGenerator();
            ClsD cd = new ClsD();
            ProxyClass pc1 = generator.generate(
                ClsD.class, Jie.list(),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return true;
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invoke(cd, args);
                    }
                }
            );
            ClsD cdp = pc1.newInstance();
            assertEquals(
                cdp.b3(true, (byte) 1, (short) 2, (char) 3, 4, 5, 6, 7, "8"),
                cd.b3(true, (byte) 1, (short) 2, (char) 3, 4, 5, 6, 7, "8")
            );
            ClsE ce = new ClsE();
            ProxyClass pc2 = generator.generate(
                ClsE.class, Jie.list(),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return true;
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invoke(ce, args);
                    }
                }
            );
            ClsE cep = pc2.newInstance();
            assertEquals(
                cep.b3(true, (byte) 1, (short) 2, (char) 3, 4, 5, 6, 7, "8"),
                ce.b3(true, (byte) 1, (short) 2, (char) 3, 4, 5, 6, 7, "8")
            );
            InterE ie = new ClsE();
            ProxyClass pc3 = generator.generate(
                null, Jie.list(InterE.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return true;
                    }

                    @Override
                    public @Nullable Object invoke(
                        @Nonnull Object proxy,
                        @Nonnull Method method,
                        @Nonnull ProxyInvoker invoker,
                        @Nullable Object @Nonnull ... args
                    ) throws Throwable {
                        return invoker.invoke(ie, args);
                    }
                }
            );
            InterE iep = pc3.newInstance();
            assertEquals(
                iep.b3(true, (byte) 1, (short) 2, (char) 3, 4, 5, 6, 7, "8"),
                ie.b3(true, (byte) 1, (short) 2, (char) 3, 4, 5, 6, 7, "8")
            );
        }
    }

    @Test
    public void testBigParameters() throws Exception {
        IntVar counter = IntVar.of(0);
        ProxyClass pc = generateProxy(LotsOfMethods.class, Jie.list(), counter);
        LotsOfMethods proxy = pc.newInstance();
        LotsOfMethods inst = new LotsOfMethods();
        List<Method> methods = JieStream.stream(inst.getClass().getMethods())
            .filter(m -> m.getName().startsWith("instanceMethod") && m.getParameterCount() > 0)
            .collect(Collectors.toList());
        for (Method method : methods) {
            assertEquals(
                method.invoke(proxy, LotsOfMethods.buildArgsForLotsOfMethods(method)),
                method.invoke(inst, LotsOfMethods.buildArgsForLotsOfMethods(method))
            );
        }
        counter.set(0);
    }

    private void testInterA(InterA obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj.hashCode(), Jie.hashId(obj));
        assertEquals(counter.get(), 2);
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(counter.get(), 5);
        expectThrows(AbstractMethodError.class, obj::a1);
        assertEquals(counter.get(), 6);
        expectThrows(AbstractMethodError.class, obj::a2);
        assertEquals(counter.get(), 7);
        expectThrows(AbstractMethodError.class, obj::a3);
        assertEquals(counter.get(), 8);
        expectThrows(AbstractMethodError.class, obj::a4);
        assertEquals(counter.get(), 9);
        expectThrows(AbstractMethodError.class, obj::a5);
        assertEquals(counter.get(), 10);
        expectThrows(AbstractMethodError.class, obj::a6);
        assertEquals(counter.get(), 11);
        expectThrows(AbstractMethodError.class, obj::a7);
        assertEquals(counter.get(), 12);
        expectThrows(AbstractMethodError.class, obj::a8);
        assertEquals(counter.get(), 13);
        expectThrows(AbstractMethodError.class, obj::a9);
        assertEquals(counter.get(), 14);
        expectThrows(AbstractMethodError.class, obj::a10);
        assertEquals(counter.get(), 15);
        expectThrows(AbstractMethodError.class, obj::a11);
        assertEquals(counter.get(), 16);
    }

    private void testInterB(InterB obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj.hashCode(), Jie.hashId(obj));
        assertEquals(counter.get(), 2);
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(counter.get(), 5);
        expectThrows(AbstractMethodError.class, () -> obj.b1(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 6);
        expectThrows(AbstractMethodError.class, () -> obj.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 7);
        expectThrows(AbstractMethodError.class, () -> obj.b3(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 8);
        expectThrows(AbstractMethodError.class, () -> obj.b4(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 9);
        expectThrows(AbstractMethodError.class, () -> obj.b5(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 10);
        expectThrows(AbstractMethodError.class, () -> obj.b6(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 11);
        expectThrows(AbstractMethodError.class, () -> obj.b7(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 12);
        expectThrows(AbstractMethodError.class, () -> obj.b8(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 13);
        expectThrows(AbstractMethodError.class, () -> obj.b9(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 14);
        expectThrows(AbstractMethodError.class, () -> obj.b10(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 15);
        expectThrows(AbstractMethodError.class, () -> obj.b11(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(counter.get(), 16);
    }

    private void testInterD(InterD obj, ClsD cd, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj.hashCode(), Jie.hashId(obj));
        assertEquals(counter.get(), 2);
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(counter.get(), 5);
        assertEquals(
            obj.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 6);
        assertEquals(
            obj.b3(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b3(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 7);
        assertEquals(
            obj.b4(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b4(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 8);
        assertEquals(
            obj.b5(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b5(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 9);
        assertEquals(
            obj.b6(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b6(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 10);
        assertEquals(
            obj.b7(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b7(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 11);
        assertEquals(
            obj.b8(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b8(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 12);
        assertEquals(
            obj.b9(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b9(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 13);
        assertEquals(
            obj.b10(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b10(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 14);
        assertEquals(
            obj.b11(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b11(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(counter.get(), 15);
        obj.b1(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8");
        assertEquals(counter.get(), 16);
    }

    private void testInterC(InterC obj1, InterC<String> obj2, IntVar counter) {
        assertFalse(obj1.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj1.hashCode(), Jie.hashId(obj1));
        assertEquals(counter.get(), 2);
        assertEquals(obj1.toString(), obj1.getClass().getName() + '@' + Integer.toHexString(obj1.hashCode()));
        assertEquals(counter.get(), 5);
        assertEquals(obj1.c1("aaa"), "aaa");
        assertEquals(counter.get(), 6);
        counter.set(0);
        assertFalse(obj2.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj2.hashCode(), Jie.hashId(obj2));
        assertEquals(counter.get(), 2);
        assertEquals(obj2.toString(), obj2.getClass().getName() + '@' + Integer.toHexString(obj2.hashCode()));
        assertEquals(counter.get(), 5);
        String c2Str = obj2.c1("bbb");
        assertEquals(c2Str, "bbb");
        assertEquals(counter.get(), 6);
    }

    private void testClsA(ClsA obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj.hashCode(), Jie.hashId(obj));
        assertEquals(counter.get(), 2);
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(counter.get(), 5);
        assertEquals(obj.a1("aaa"), "aaa");
        assertEquals(counter.get(), 7);// public + protected
    }

    private void testClsB(ClsB obj1, ClsB<String> obj2, IntVar counter) {
        assertFalse(obj1.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj1.hashCode(), Jie.hashId(obj1));
        assertEquals(counter.get(), 2);
        assertEquals(obj1.toString(), obj1.getClass().getName() + '@' + Integer.toHexString(obj1.hashCode()));
        assertEquals(counter.get(), 5);
        assertEquals(obj1.b1("aaa"), "aaa");
        assertEquals(counter.get(), 7);// public + protected
        counter.set(0);
        assertFalse(obj2.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj2.hashCode(), Jie.hashId(obj2));
        assertEquals(counter.get(), 2);
        assertEquals(obj2.toString(), obj2.getClass().getName() + '@' + Integer.toHexString(obj2.hashCode()));
        assertEquals(counter.get(), 5);
        String c2Str = obj2.b1("bbb");
        assertEquals(c2Str, "bbb");
        assertEquals(counter.get(), 7);// public + protected
    }

    private void testClsC(ClsC obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(counter.get(), 1);
        assertEquals(obj.hashCode(), Jie.hashId(obj));
        assertEquals(counter.get(), 2);
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(counter.get(), 5);
        assertEquals(obj.b1("aaa"), "aaa");
        assertEquals(counter.get(), 6);
        ClsB<String> b1 = Jie.as(obj);
        assertEquals(b1.b1("bbb"), "bbb");
        assertEquals(counter.get(), 7);
        ClsB<Object> b2 = Jie.as(obj);
        Object b2Result = b2.b1("bbb");
        assertEquals(b2Result, "bbb");
        assertEquals(counter.get(), 8);
    }

    private void testSS(SameMethodA obj1, SameMethodB obj2, IntVar counter) {
        assertEquals(obj1.ss(66), obj2.ss(66));
        assertEquals(counter.get(), 2);
    }

    private ProxyClass generateProxy(Class<?> superclass, List<Class<?>> interfaces, IntVar counter) {
        ProxyClassGenerator generator = new AsmProxyClassGenerator();
        return generator.generate(
            superclass, interfaces,
            new ProxyMethodHandler() {

                @Override
                public boolean requiresProxy(Method method) {
                    return true;
                }

                @Override
                public @Nullable Object invoke(
                    @Nonnull Object proxy,
                    @Nonnull Method method,
                    @Nonnull ProxyInvoker invoker,
                    @Nullable Object @Nonnull ... args
                ) throws Throwable {
                    counter.incrementAndGet();
                    return invoker.invokeSuper(proxy, args);
                }
            }
        );
    }

    public interface InterA {

        void a1();

        boolean a2();

        byte a3();

        short a4();

        char a5();

        int a6();

        long a7();

        float a8();

        double a9();

        String a10();

        <T> T a11();
    }

    public interface InterB {

        void b1(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        boolean b2(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        byte b3(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        short b4(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        char b5(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        int b6(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        long b7(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        float b8(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        double b9(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        String b10(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8);

        <T> T b11(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, T a8);
    }

    public interface InterC<T> {

        default T c1(T a0) {
            return a0;
        }
    }

    public interface InterD {

        default void b1(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
        }

        default boolean b2(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return true;
        }

        default byte b3(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 1;
        }

        default short b4(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 2;
        }

        default char b5(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 3;
        }

        default int b6(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 4;
        }

        default long b7(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 5;
        }

        default float b8(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 6;
        }

        default double b9(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return 7;
        }

        default String b10(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, String a8) {
            return "8";
        }

        default <T> T b11(boolean a0, byte a1, short a2, char a3, int a4, long a5, float a6, double a7, T a8) {
            return a8;
        }
    }

    public interface InterE extends InterD {
    }

    public static class ClsA {

        public String a1(String a0) {
            return cp(a0);
        }

        protected String cp(String a0) {
            return a0;
        }

        public void throwClsEx() {
            throw new ProxyTestException();
        }
    }

    public static class ClsB<T> {
        public T b1(T a0) {
            return cp(a0);
        }

        protected T cp(T a0) {
            return a0;
        }
    }

    public static class ClsC extends ClsB<String> {
        @Override
        public String b1(String a0) {
            return super.b1(a0);
        }
    }

    public static class ClsD implements InterD {
    }

    public static class ClsE implements InterE {
    }

    public interface SameMethodA {

        default int ss(int i) {
            return i * 2;
        }

        default void throwInterEx() {
            throw new ProxyTestException();
        }
    }

    public interface SameMethodB {
        default int ss(int i) {
            return i * 4;
        }
    }
}
