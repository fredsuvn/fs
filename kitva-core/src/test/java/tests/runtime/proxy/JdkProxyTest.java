package tests.runtime.proxy;

import internal.test.J17Also;
import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.value.BooleanVar;
import space.sunqian.common.base.value.IntVar;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;
import space.sunqian.common.runtime.proxy.ProxySpec;
import space.sunqian.common.runtime.proxy.jdk.JdkProxyMaker;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
            assertEquals(Object.class, spec.proxiedClass());
            assertEquals(spec.proxiedInterfaces(), Kit.list(InterA.class, InterB.class, InterC.class));
            assertEquals(spec.proxyHandler(), proxyHandler);
            assertNotNull(spec.proxyClass());
            InterA a = spec.newInstance();
            assertSame(result, a.aa("11", 11));
            assertEquals(1, counter.get());
            InterB b = spec.newInstance();
            assertSame(result, b.bb("11", 11));
            assertEquals(2, counter.get());
            InterC c = spec.newInstance();
            assertSame(result, c.cc("11", 11));
            assertEquals(3, counter.get());
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
            assertThrows(StackOverflowError.class, () -> paA.aa("11", 11));
            assertThrows(StackOverflowError.class, () -> paB.bb("11", 11));
            assertThrows(StackOverflowError.class, () -> paC.cc("11", 11));
            counter.clear();
            assertEquals(paA.filteredA(), cls.filteredA());
            assertEquals(paB.filteredB(), cls.filteredB());
            assertEquals(paC.filteredC(), cls.filteredC());
            assertEquals(paA.filteredA(), cls.filteredA());
            assertEquals(paB.filteredB(), cls.filteredB());
            assertEquals(paC.filteredC(), cls.filteredC());
            assertEquals(0, counter.get());
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
            assertEquals(1, counter.get());
            counter.clear();
        }
    }

    @J17Also
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
        assertEquals(1, counter.get());
        assertThrows(AbstractMethodError.class, psi::si2);
        assertEquals(2, counter.get());
        counter.clear();
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
        assertEquals(0, counter.get());
        SameMethodA sa = pc.newInstance();
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

    @Test
    public void testException() {
        // JdkProxyMaker.JdkProxyException
        assertThrows(JdkProxyMaker.JdkProxyException.class, () -> {
            throw new JdkProxyMaker.JdkProxyException(new Exception());
        });
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
