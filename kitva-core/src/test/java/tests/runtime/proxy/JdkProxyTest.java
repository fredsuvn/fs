package tests.runtime.proxy;

import org.testng.annotations.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.value.BooleanVar;
import space.sunqian.common.base.value.IntVar;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;
import space.sunqian.common.runtime.proxy.ProxySpec;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class JdkProxyTest {

    @Test
    public void testJdkProxy() {
        ProxyMaker proxyMaker = ProxyMaker.byJdk();
        IntVar counter = IntVar.of(0);
        String result = "66666";
        {
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
                    counter.incrementAndGet();
                    return result;
                }
            };
            ProxySpec spec = proxyMaker.make(
                null, Kit.list(InterA.class, InterB.class, InterC.class), proxyHandler
            );
            assertEquals(spec.proxiedClass(), Object.class);
            assertEquals(spec.proxiedInterfaces(), Kit.list(InterA.class, InterB.class, InterC.class));
            assertEquals(spec.proxyHandler(), proxyHandler);
            assertNotNull(spec.proxyClass());
            InterA a = spec.newInstance();
            assertSame(a.aa("11", 11), result);
            assertEquals(counter.get(), 1);
            InterB b = spec.newInstance();
            assertSame(b.bb("11", 11), result);
            assertEquals(counter.get(), 2);
            InterC c = spec.newInstance();
            assertSame(c.cc("11", 11), result);
            assertEquals(counter.get(), 3);
            counter.clear();
        }
        {
            class Cls implements InterA, InterB, InterC<String> {
                @Override
                public String aa(String a0, int a1) {
                    return "777";
                }
            }
            Cls cls = new Cls();
            ProxySpec spec = proxyMaker.make(
                null, Kit.list(InterA.class, InterB.class, InterC.class),
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
                        counter.incrementAndGet();
                        return invoker.invoke(cls, args);
                    }
                }
            );
            InterA pa2 = spec.newInstance();
            assertEquals(pa2.aa("11", 11), cls.aa("11", 11));
            counter.clear();
        }
        {
            class Cls implements InterA, InterB, InterC<String> {
                @Override
                public String aa(String a0, int a1) {
                    return "777";
                }
            }
            Cls cls = new Cls();
            ProxySpec spec = proxyMaker.make(
                null, Kit.list(InterA.class, InterB.class, InterC.class),
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
                        counter.incrementAndGet();
                        return invoker.invoke(proxy, args);
                    }
                }
            );
            InterA paA = spec.newInstance();
            InterB paB = spec.newInstance();
            InterC paC = spec.newInstance();
            expectThrows(StackOverflowError.class, () -> paA.aa("11", 11));
            expectThrows(StackOverflowError.class, () -> paB.bb("11", 11));
            expectThrows(StackOverflowError.class, () -> paC.cc("11", 11));
            counter.clear();
            assertEquals(paA.filteredA(), cls.filteredA());
            assertEquals(paB.filteredB(), cls.filteredB());
            assertEquals(paC.filteredC(), cls.filteredC());
            assertEquals(paA.filteredA(), cls.filteredA());
            assertEquals(paB.filteredB(), cls.filteredB());
            assertEquals(paC.filteredC(), cls.filteredC());
            assertEquals(counter.get(), 0);
            counter.clear();
        }
        {
            ProxySpec spec = proxyMaker.make(
                null, Kit.list(InterOverpass1.class, InterOverpass2.class, InterOverpass3.class),
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
                        counter.incrementAndGet();
                        return null;
                    }
                }
            );
            InterOverpass1 po = spec.newInstance();
            assertNull(po.ooi("11", 11));
            assertEquals(counter.get(), 1);
            counter.clear();
        }
    }

    @Test
    public void testInvokeSuper() throws Exception {
        ProxyMaker proxyMaker = ProxyMaker.byJdk();
        IntVar counter = IntVar.of(0);
        SuperInter si = new SuperInter() {
            @Override
            public String si2() {
                return "";
            }
        };
        ProxySpec pc = proxyMaker.make(
            null, Kit.list(SuperInter.class),
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
                    counter.incrementAndGet();
                    return invoker.invokeSuper(proxy, args);
                }
            }
        );
        SuperInter psi = pc.newInstance();
        assertEquals(psi.si1(), si.si1());
        assertEquals(counter.get(), 1);
        expectThrows(AbstractMethodError.class, psi::si2);
        assertEquals(counter.get(), 2);
        counter.clear();

        {
            // unsupported default method invocable
            ProxyBackTest.testUnsupportedDefaultMethod();
        }
    }

    @Test
    public void testSameMethod() {
        ProxyMaker proxyMaker = ProxyMaker.byJdk();
        IntVar counter = IntVar.of(0);
        BooleanVar isA = BooleanVar.of(false);
        ProxySpec pc = proxyMaker.make(
            null, Kit.list(SameMethodA.class, SameMethodB.class),
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
        assertEquals(counter.get(), 0);
        SameMethodA sa = pc.newInstance();
        if (isA.get()) {
            assertEquals(sa.ss(66), 66 * 2);
        } else {
            assertEquals(sa.ss(66), 66 * 4);
        }
        assertEquals(counter.get(), 1);
        SameMethodA sb = pc.newInstance();
        if (isA.get()) {
            assertEquals(sb.ss(66), 66 * 2);
        } else {
            assertEquals(sb.ss(66), 66 * 4);
        }
        assertEquals(counter.get(), 2);
        counter.clear();
    }

    public interface InterA {

        static void staticA() {
        }

        String aa(String a0, int a1);

        default String filteredA() {
            return "filteredA";
        }
    }

    public interface InterB {

        static void staticB() {
        }

        default String bb(String a0, int a1) {
            return a0 + a1;
        }

        default String filteredB() {
            return "filteredB";
        }
    }

    public interface InterC<T> {

        static void staticC() {
        }

        default T cc(T a0, int a1) {
            return a0;
        }

        default String filteredC() {
            return "filteredC";
        }
    }

    public interface InterOverpass1 {
        default String ooi(String a0, int a1) {
            return a0 + a1;
        }
    }

    public interface InterOverpass2 extends InterOverpass1 {}

    public interface InterOverpass3 extends InterOverpass2 {}

    public interface SuperInter {

        default String si1() {
            return "t1";
        }

        String si2();
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
}
