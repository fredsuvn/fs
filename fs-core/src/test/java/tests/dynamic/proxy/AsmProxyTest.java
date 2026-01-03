package tests.dynamic.proxy;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.BooleanVar;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.dynamic.proxy.ProxyHandler;
import space.sunqian.fs.dynamic.proxy.ProxyInvoker;
import space.sunqian.fs.dynamic.proxy.ProxyMaker;
import space.sunqian.fs.dynamic.proxy.ProxySpec;
import space.sunqian.fs.dynamic.proxy.asm.AsmProxyMaker;
import tests.utils.LotsOfMethods;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsmProxyTest implements PrintTest {

    @Test
    public void testProxyClass() {
        IntVar counter = IntVar.of(0);
        {
            // InterA
            ProxySpec pc = generateProxy(null, Fs.list(InterA.class), counter);
            InterA a = pc.newInstance();
            testInterA(a, counter);
            counter.clear();
        }
        {
            // InterB
            ProxySpec pc = generateProxy(null, Fs.list(InterB.class), counter);
            InterB b = pc.newInstance();
            testInterB(b, counter);
            counter.clear();
        }
        {
            // InterC
            ProxySpec pc = generateProxy(null, Fs.list(InterC.class), counter);
            InterC c1 = pc.newInstance();
            InterC<String> c2 = pc.newInstance();
            testInterC(c1, c2, counter);
            counter.clear();
        }
        {
            // InterD
            ProxySpec pc1 = generateProxy(null, Fs.list(InterD.class), counter);
            InterD d1 = pc1.newInstance();
            testInterD(d1, new ClsD(), counter);
            counter.clear();
            ProxySpec pc2 = generateProxy(ClsD.class, Fs.list(), counter);
            InterD d2 = pc2.newInstance();
            testInterD(d2, new ClsD(), counter);
            counter.clear();
        }
        {
            // ClsA
            ProxySpec pc = generateProxy(ClsA.class, Fs.list(), counter);
            ClsA a = pc.newInstance();
            testClsA(a, counter);
            counter.clear();
        }
        {
            // ClsB
            ProxySpec pc = generateProxy(ClsB.class, Fs.list(), counter);
            ClsB b1 = pc.newInstance();
            ClsB<String> b2 = pc.newInstance();
            testClsB(b1, b2, counter);
            counter.clear();
        }
        {
            // ClsC
            ProxySpec pc = generateProxy(ClsC.class, Fs.list(), counter);
            ClsC c = pc.newInstance();
            testClsC(c, counter);
            counter.clear();
        }
        {
            // ClsA, InterA, InterB, InterC
            ProxySpec pc = generateProxy(ClsA.class, Fs.list(InterA.class, InterB.class, InterC.class), counter);
            Object obj = pc.newInstance();
            testClsA((ClsA) obj, counter);
            counter.clear();
            testInterA((InterA) obj, counter);
            counter.clear();
            testInterB((InterB) obj, counter);
            counter.clear();
            testInterC((InterC) obj, (InterC<String>) obj, counter);
            counter.clear();
        }
        {
            // SameMethod
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            BooleanVar isA = BooleanVar.of(false);
            ProxySpec pc = proxyMaker.make(
                null, Fs.list(SameMethodA.class, SameMethodB.class),
                new ProxyHandler() {

                    private boolean encounter = false;

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        if (method.getDeclaringClass().equals(SameMethodA.class)) {
                            if (encounter) {
                                return true;
                            } else {
                                encounter = true;
                            }
                            isA.set(true);
                            return true;
                        }
                        if (method.getDeclaringClass().equals(SameMethodB.class)) {
                            if (encounter) {
                                return true;
                            } else {
                                encounter = true;
                            }
                            isA.set(false);
                            return true;
                        }
                        return false;
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
            SameMethodA sa = pc.newInstance();
            assertFalse(sa.equals(""));
            assertEquals(0, counter.get());
            assertEquals(sa.hashCode(), Fs.id(sa));
            assertEquals(0, counter.get());
            assertEquals(sa.toString(), sa.getClass().getName() + '@' + Integer.toHexString(sa.hashCode()));
            assertEquals(0, counter.get());
            if (isA.get()) {
                assertEquals(66 * 2, sa.ss(66));
            } else {
                assertEquals(66 * 4, sa.ss(66));
            }
            assertEquals(1, counter.get());
            SameMethodA sb = pc.newInstance();
            if (isA.get()) {
                assertEquals(66 * 2, sb.ss(66));
            } else {
                assertEquals(66 * 4, sb.ss(66));
            }
            assertEquals(2, counter.get());
            counter.clear();
        }
        {
            // test class
            ProxySpec pc = generateProxy(ClsC.class, Fs.list(), counter);
            Class<?> c = pc.proxyClass();
            printFor("ASM proxy class: ", c);
            Constructor<?>[] constructors = c.getConstructors();
            assertEquals(1, constructors.length);
            assertEquals(
                Fs.list(constructors[0].getParameterTypes()),
                Fs.list(ProxyHandler.class, Method[].class)
            );
            assertTrue(Modifier.isPublic(constructors[0].getModifiers()));
            counter.clear();
        }
    }

    @Test
    public void testProxyInvoker() {
        {
            // invokeSuper
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            IntVar counter = IntVar.of(0);
            ProxySpec pc = proxyMaker.make(
                ClsA.class, Fs.list(DefaultInter.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(DefaultInter.class);
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
            class ClaAProxy extends ClsA implements DefaultInter {}
            Object asmProxy = pc.newInstance();
            ClaAProxy manProxy = new ClaAProxy();
            assertEquals(0, counter.get());
            assertEquals(((ClsA) asmProxy).a1("666"), manProxy.a1("666"));
            assertEquals(2, counter.get());
            assertEquals(((DefaultInter) asmProxy).defaultInt(999), manProxy.defaultInt(999));
            assertEquals(3, counter.get());
        }
        {
            // invoke
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            IntVar counter = IntVar.of(0);
            class ClaAProxy extends ClsA implements DefaultInter {
                @Override
                public String a1(String a0) {
                    return super.a1(a0) + "888";
                }

                @Override
                public int defaultInt(int i) {
                    return i + 1;
                }
            }
            ClaAProxy manProxy = new ClaAProxy();
            ProxySpec pc = proxyMaker.make(
                ClsA.class, Fs.list(DefaultInter.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(DefaultInter.class);
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
            assertEquals(0, counter.get());
            assertEquals(((ClsA) asmProxy).a1("666"), manProxy.a1("666"));
            assertEquals("666888", ((ClsA) asmProxy).a1("666"));
            assertEquals(2, counter.get());
            assertEquals(((DefaultInter) asmProxy).defaultInt(999), manProxy.defaultInt(999));
            assertEquals(3, counter.get());
            assertEquals(999 + 1, ((DefaultInter) asmProxy).defaultInt(999));
            assertEquals(4, counter.get());
        }
        {
            // stack overflow
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            ProxySpec pc = proxyMaker.make(
                ClsA.class, Fs.list(DefaultInter.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(DefaultInter.class);
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
            assertThrows(StackOverflowError.class, () -> asmProxy1.a1(""));
            DefaultInter asmProxy2 = pc.newInstance();
            assertThrows(StackOverflowError.class, () -> asmProxy2.defaultInt(1));
        }
        {
            // throws directly
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            ProxySpec pc1 = proxyMaker.make(
                ClsA.class, Fs.list(DefaultInter.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(DefaultInter.class);
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
            assertThrows(ProxyTestException.class, () -> a1.a1(""));
            DefaultInter s1 = pc1.newInstance();
            assertThrows(ProxyTestException.class, () -> s1.defaultInt(1));
            ProxySpec pc2 = proxyMaker.make(
                ClsA.class, Fs.list(DefaultInter.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return method.getDeclaringClass().equals(ClsA.class)
                            || method.getDeclaringClass().equals(DefaultInter.class);
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
            assertThrows(ProxyTestException.class, a2::throwClsEx);
            DefaultInter s2 = pc1.newInstance();
            assertThrows(ProxyTestException.class, () -> s2.defaultInt(1));
        }
        {
            // indirect super: A extends B extends C: A super-> C
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            ClsD cd = new ClsD();
            ProxySpec pc1 = proxyMaker.make(
                ClsD.class, Fs.list(),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
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
            ProxySpec pc2 = proxyMaker.make(
                ClsE.class, Fs.list(),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
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
            ProxySpec pc3 = proxyMaker.make(
                null, Fs.list(InterE.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
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
        {
            // invoke filtered
            class F extends ClsA implements InterC {
            }
            ProxyMaker proxyMaker = ProxyMaker.byAsm();
            ProxySpec pc = proxyMaker.make(
                ClsA.class, Fs.list(InterC.class),
                new ProxyHandler() {

                    @Override
                    public boolean needsProxy(@Nonnull Method method) {
                        return !method.getName().startsWith("filtered");
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
            F f = new F();
            ClsA ca = pc.newInstance();
            assertThrows(StackOverflowError.class, () -> ca.a1(""));
            assertEquals(ca.filteredA(), f.filteredA());
            InterC ic = pc.newInstance();
            assertThrows(StackOverflowError.class, () -> ic.c1(""));
            assertEquals(ic.filteredC(), f.filteredC());
        }
    }

    @Test
    public void testBigParameters() throws Exception {
        IntVar counter = IntVar.of(0);
        ProxySpec pc = generateProxy(LotsOfMethods.class, Fs.list(), counter);
        LotsOfMethods proxy = pc.newInstance();
        LotsOfMethods inst = new LotsOfMethods();
        List<Method> methods = Fs.stream(inst.getClass().getMethods())
            .filter(m -> m.getName().startsWith("instanceMethod") && m.getParameterCount() > 0)
            .collect(Collectors.toList());
        for (Method method : methods) {
            assertEquals(
                method.invoke(proxy, LotsOfMethods.buildArgsForLotsOfMethods(method)),
                method.invoke(inst, LotsOfMethods.buildArgsForLotsOfMethods(method))
            );
        }
        counter.clear();
    }

    @Test
    public void testOverpass() {
        ProxyMaker proxyMaker = ProxyMaker.byAsm();
        ProxySpec pc = proxyMaker.make(
            ClsOverpass3.class, Fs.list(InterOverpass3.class),
            new ProxyHandler() {

                @Override
                public boolean needsProxy(@Nonnull Method method) {
                    return true;
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
        ClsOverpass3 pc3 = pc.newInstance();
        ClsOverpass3 c3 = new ClsOverpass3();
        assertEquals(pc3.ooc(), c3.ooc());
        InterOverpass2 pi3 = pc.newInstance();
        InterOverpass2 i3 = new InterOverpass2() {};
        assertEquals(pi3.ooi(), i3.ooi());
    }

    @Test
    public void testException() {
        assertThrows(AsmProxyMaker.AsmProxyException.class, () ->
            ProxyMaker.byAsm().make(null, Fs.list(), null));
    }

    private void testInterA(InterA obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj.hashCode(), Fs.id(obj));
        assertEquals(2, counter.get());
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(5, counter.get());
        assertThrows(AbstractMethodError.class, obj::a1);
        assertEquals(6, counter.get());
        assertThrows(AbstractMethodError.class, obj::a2);
        assertEquals(7, counter.get());
        assertThrows(AbstractMethodError.class, obj::a3);
        assertEquals(8, counter.get());
        assertThrows(AbstractMethodError.class, obj::a4);
        assertEquals(9, counter.get());
        assertThrows(AbstractMethodError.class, obj::a5);
        assertEquals(10, counter.get());
        assertThrows(AbstractMethodError.class, obj::a6);
        assertEquals(11, counter.get());
        assertThrows(AbstractMethodError.class, obj::a7);
        assertEquals(12, counter.get());
        assertThrows(AbstractMethodError.class, obj::a8);
        assertEquals(13, counter.get());
        assertThrows(AbstractMethodError.class, obj::a9);
        assertEquals(14, counter.get());
        assertThrows(AbstractMethodError.class, obj::a10);
        assertEquals(15, counter.get());
        assertThrows(AbstractMethodError.class, obj::a11);
        assertEquals(16, counter.get());
    }

    private void testInterB(InterB obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj.hashCode(), Fs.id(obj));
        assertEquals(2, counter.get());
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(5, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b1(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(6, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(7, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b3(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(8, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b4(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(9, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b5(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(10, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b6(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(11, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b7(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(12, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b8(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(13, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b9(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(14, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b10(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(15, counter.get());
        assertThrows(AbstractMethodError.class, () -> obj.b11(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"));
        assertEquals(16, counter.get());
    }

    private void testInterD(InterD obj, ClsD cd, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj.hashCode(), Fs.id(obj));
        assertEquals(2, counter.get());
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(5, counter.get());
        assertEquals(
            obj.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b2(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(6, counter.get());
        assertEquals(
            obj.b3(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b3(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(7, counter.get());
        assertEquals(
            obj.b4(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b4(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(8, counter.get());
        assertEquals(
            obj.b5(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b5(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(9, counter.get());
        assertEquals(
            obj.b6(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b6(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(10, counter.get());
        assertEquals(
            obj.b7(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b7(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(11, counter.get());
        assertEquals(
            obj.b8(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b8(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(12, counter.get());
        assertEquals(
            obj.b9(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b9(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(13, counter.get());
        assertEquals(
            obj.b10(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b10(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(14, counter.get());
        assertEquals(
            obj.b11(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8"),
            cd.b11(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8")
        );
        assertEquals(15, counter.get());
        obj.b1(true, (byte) 1, (short) 2, '3', 4, 5L, 6.0f, 7.0, "8");
        assertEquals(16, counter.get());
    }

    private void testInterC(InterC obj1, InterC<String> obj2, IntVar counter) {
        assertFalse(obj1.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj1.hashCode(), Fs.id(obj1));
        assertEquals(2, counter.get());
        assertEquals(obj1.toString(), obj1.getClass().getName() + '@' + Integer.toHexString(obj1.hashCode()));
        assertEquals(5, counter.get());
        assertEquals("aaa", obj1.c1("aaa"));
        assertEquals(6, counter.get());
        counter.clear();
        assertFalse(obj2.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj2.hashCode(), Fs.id(obj2));
        assertEquals(2, counter.get());
        assertEquals(obj2.toString(), obj2.getClass().getName() + '@' + Integer.toHexString(obj2.hashCode()));
        assertEquals(5, counter.get());
        String c2Str = obj2.c1("bbb");
        assertEquals("bbb", c2Str);
        assertEquals(6, counter.get());
    }

    private void testClsA(ClsA obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj.hashCode(), Fs.id(obj));
        assertEquals(2, counter.get());
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(5, counter.get());
        assertEquals("aaa", obj.a1("aaa"));
        assertEquals(7, counter.get());// public + protected
    }

    private void testClsB(ClsB obj1, ClsB<String> obj2, IntVar counter) {
        assertFalse(obj1.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj1.hashCode(), Fs.id(obj1));
        assertEquals(2, counter.get());
        assertEquals(obj1.toString(), obj1.getClass().getName() + '@' + Integer.toHexString(obj1.hashCode()));
        assertEquals(5, counter.get());
        assertEquals("aaa", obj1.b1("aaa"));
        assertEquals(7, counter.get());// public + protected
        counter.clear();
        assertFalse(obj2.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj2.hashCode(), Fs.id(obj2));
        assertEquals(2, counter.get());
        assertEquals(obj2.toString(), obj2.getClass().getName() + '@' + Integer.toHexString(obj2.hashCode()));
        assertEquals(5, counter.get());
        String c2Str = obj2.b1("bbb");
        assertEquals("bbb", c2Str);
        assertEquals(7, counter.get());// public + protected
    }

    private void testClsC(ClsC obj, IntVar counter) {
        assertFalse(obj.equals(""));
        assertEquals(1, counter.get());
        assertEquals(obj.hashCode(), Fs.id(obj));
        assertEquals(2, counter.get());
        assertEquals(obj.toString(), obj.getClass().getName() + '@' + Integer.toHexString(obj.hashCode()));
        assertEquals(5, counter.get());
        assertEquals("aaa", obj.b1("aaa"));
        assertEquals(6, counter.get());
        ClsB<String> b1 = Fs.as(obj);
        assertEquals("bbb", b1.b1("bbb"));
        assertEquals(7, counter.get());
        ClsB<Object> b2 = Fs.as(obj);
        Object b2Result = b2.b1("bbb");
        assertEquals("bbb", b2Result);
        assertEquals(8, counter.get());
    }

    private ProxySpec generateProxy(Class<?> superclass, List<Class<?>> interfaces, IntVar counter) {
        ProxyMaker proxyMaker = ProxyMaker.byAsm();
        ProxyHandler proxyHandler = new ProxyHandler() {

            @Override
            public boolean needsProxy(@Nonnull Method method) {
                return true;
            }

            @Override
            public @Nullable Object invoke(
                @Nonnull Object proxy,
                @Nonnull Method method,
                @Nonnull ProxyInvoker invoker,
                @Nullable Object @Nonnull ... args
            ) throws Throwable {
                // printFor("proxy method: " + method);
                counter.incrementAndGet();
                return invoker.invokeSuper(proxy, args);
            }
        };
        ProxySpec spec = proxyMaker.make(superclass, interfaces, proxyHandler);
        assertEquals(spec.proxiedClass(), superclass == null ? Object.class : superclass);
        assertEquals(spec.proxiedInterfaces(), interfaces);
        assertEquals(spec.proxyHandler(), proxyHandler);
        return spec;
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

        default String filteredC() {
            return "filteredC";
        }

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

        public String filteredA() {
            return "filteredA";
        }

        public String a1(String a0) {
            return cp(a0);
        }

        protected String cp(String a0) {
            return a0;
        }

        public void throwClsEx() {
            throw new ProxyTestException();
        }

        private void testModifierFilter() {
            // test modifier filter in ProxyKit
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

    public interface DefaultInter {
        default int defaultInt(int i) {
            return i;
        }
    }

    public interface SameMethodA {

        default int ss(int i) {
            return i * 2;
        }
    }

    public interface SameMethodB {
        default int ss(int i) {
            return i * 4;
        }
    }

    public interface InterOverpass1 {
        default String ooi() {
            return "ooi";
        }
    }

    public interface InterOverpass2 extends InterOverpass1 {
    }

    public interface InterOverpass3 extends InterOverpass2 {
    }

    public static class ClsOverpass1 {
        public String ooc() {
            return "ooc";
        }
    }

    public static class ClsOverpass2 extends ClsOverpass1 {}

    public static class ClsOverpass3 extends ClsOverpass2 {}

    @Test
    public void testSimple() {
        IntVar count = IntVar.of(0);
        BooleanVar replace = BooleanVar.of(false);
        ProxyHandler handler = new ProxyHandler() {

            @Override
            public boolean needsProxy(@Nonnull Method method) {
                return true;
            }

            @Override
            public @Nullable Object invoke(
                @Nonnull Object proxy, @Nonnull Method method, @Nonnull ProxyInvoker invoker, @Nullable Object @Nonnull ... args
            ) throws Throwable {
                try {
                    beforeInvoking(method, args, proxy);
                    Object ret = invoker.invokeSuper(proxy, args);
                    return afterReturning(ret, method, args, proxy);
                } catch (Throwable ex) {
                    return afterThrowing(ex, method, args, proxy);
                }
            }

            private void beforeInvoking(
                @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target
            ) throws Throwable {
                if (replace.get()) {
                    args[0] = "b";
                }
                count.getAndIncrement();
            }

            private @Nullable Object afterReturning(
                @Nullable Object result, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target
            ) throws Throwable {
                if (replace.get()) {
                    assertEquals("b", args[0]);
                } else {
                    assertEquals("a", args[0]);
                }
                count.getAndIncrement();
                return result;
            }

            private @Nullable Object afterThrowing(
                @Nonnull Throwable ex, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target
            ) {
                if (replace.get()) {
                    assertEquals("b", args[0]);
                } else {
                    assertEquals("a", args[0]);
                }
                count.getAndIncrement();
                Object[] result = (Object[]) args[args.length - 1];
                return result[0];
            }
        };
        {
            // String
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                "atrue234567.08.0",
                sc.getString("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            replace.set(true);
            assertEquals(
                "btrue234567.08.0",
                sc.getString("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // void
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            Object[] result = new Object[1];
            sc.getVoid("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, result);
            assertEquals(
                "atrue234567.08.0",
                result[0]
            );
            assertEquals(2, count.get());
            replace.set(true);
            result[0] = null;
            sc.getVoid("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, result);
            assertEquals(
                "btrue234567.08.0",
                result[0]
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // boolean
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                true,
                sc.getBoolean("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                false,
                sc.getBoolean("a", false, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // byte
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                2,
                sc.getByte("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                20,
                sc.getByte("a", true, (byte) 20, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // char
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                '3',
                sc.getChar("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                '6',
                sc.getChar("a", true, (byte) 2, '6', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // short
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                4,
                sc.getShort("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                40,
                sc.getShort("a", true, (byte) 2, '3', (short) 40, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // int
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                5,
                sc.getInt("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                50,
                sc.getInt("a", true, (byte) 2, '3', (short) 4, 50, 6L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // long
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                6L,
                sc.getLong("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                60L,
                sc.getLong("a", true, (byte) 2, '3', (short) 4, 5, 60L, 7.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // float
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                7.0f,
                sc.getFloat("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                70.0f,
                sc.getFloat("a", true, (byte) 2, '3', (short) 4, 5, 6L, 70.0f, 8.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // double
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            assertEquals(
                8.0,
                sc.getDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d)
            );
            assertEquals(2, count.get());
            assertEquals(
                80.0,
                sc.getDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 80.0d)
            );
            assertEquals(4, count.get());
            count.clear();
            replace.clear();
        }
        {
            // throw
            ProxySpec spec = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(0, count.get());
            Object[] result = new Object[1];
            assertEquals(
                8.0,
                sc.throwDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, result)
            );
            assertEquals(
                8.0,
                result[0]
            );
            assertEquals(2, count.get());
            count.clear();
            replace.clear();
            // direct throw
            ProxySpec spec2 = ProxyMaker.byAsm().make(SimpleCls.class, Collections.emptyList(), new ProxyHandler() {

                @Override
                public boolean needsProxy(@Nonnull Method method) {
                    return true;
                }

                @Override
                public @Nullable Object invoke(
                    @Nonnull Object proxy, @Nonnull Method method, @Nonnull ProxyInvoker invoker, @Nullable Object @Nonnull ... args) {
                    throw new ProxyTestException("hello!");
                }
            });
            SimpleCls sc2 = spec2.newInstance();
            ProxyTestException ex = assertThrows(ProxyTestException.class, () ->
                sc2.throwDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, null)
            );
            assertEquals("hello!", ex.getMessage());
        }
    }

    public static class SimpleCls {
        public String getString(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return a + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
        }

        public void getVoid(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8, Object[] result) {
            result[0] = a + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
        }

        public boolean getBoolean(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p1;
        }

        public byte getByte(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p2;
        }

        public char getChar(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p3;
        }

        public short getShort(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p4;
        }

        public int getInt(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p5;
        }

        public long getLong(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p6;
        }

        public float getFloat(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p7;
        }

        public double getDouble(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p8;
        }

        public double throwDouble(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8, Object[] result) {
            result[0] = p8;
            throw new ProxyTestException();
        }
    }

    private static class ProxyTestException extends RuntimeException {

        public ProxyTestException() {
        }

        public ProxyTestException(String msg) {
            super(msg);
        }
    }
}
