package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.reflect.proxy.JdkProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class JdkProxyTest {

    @Test
    public void testJdkProxy() {
        ProxyClassGenerator generator = new JdkProxyClassGenerator();
        IntVar counter = IntVar.of(0);
        String result = "66666";
        {
            ProxyClass pc1 = generator.generate(
                null, Jie.list(InterA.class, InterB.class, InterC.class),
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
                        return result;
                    }
                }
            );
            assertNotNull(pc1.getProxyClass());
            InterA a = pc1.newInstance();
            assertSame(a.aa("11", 11), result);
            assertEquals(counter.get(), 1);
            InterB b = pc1.newInstance();
            assertSame(b.bb("11", 11), result);
            assertEquals(counter.get(), 2);
            InterC c = pc1.newInstance();
            assertSame(c.cc("11", 11), result);
            assertEquals(counter.get(), 3);
            counter.set(0);
        }
        {
            class Cls implements InterA, InterB, InterC<String> {
                @Override
                public String aa(String a0, int a1) {
                    return "777";
                }
            }
            Cls cls = new Cls();
            ProxyClass pc2 = generator.generate(
                null, Jie.list(InterA.class, InterB.class, InterC.class),
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
                        return invoker.invoke(cls, args);
                    }
                }
            );
            InterA pa2 = pc2.newInstance();
            assertEquals(pa2.aa("11", 11), cls.aa("11", 11));
            counter.set(0);
        }
        {
            class Cls implements InterA, InterB, InterC<String> {
                @Override
                public String aa(String a0, int a1) {
                    return "777";
                }
            }
            Cls cls = new Cls();
            ProxyClass pc3 = generator.generate(
                null, Jie.list(InterA.class, InterB.class, InterC.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
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
            InterA paA = pc3.newInstance();
            InterB paB = pc3.newInstance();
            InterC paC = pc3.newInstance();
            expectThrows(StackOverflowError.class, () -> paA.aa("11", 11));
            expectThrows(StackOverflowError.class, () -> paB.bb("11", 11));
            expectThrows(StackOverflowError.class, () -> paC.cc("11", 11));
            counter.set(0);
            assertEquals(paA.filteredA(), cls.filteredA());
            assertEquals(paB.filteredB(), cls.filteredB());
            assertEquals(paC.filteredC(), cls.filteredC());
            assertEquals(paA.filteredA(), cls.filteredA());
            assertEquals(paB.filteredB(), cls.filteredB());
            assertEquals(paC.filteredC(), cls.filteredC());
            assertEquals(counter.get(), 0);
            counter.set(0);
        }
        {
            ProxyClass pc4 = generator.generate(
                null, Jie.list(InterOverpass1.class, InterOverpass11.class, InterOverpass111.class),
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
                        return null;
                    }
                }
            );
            InterOverpass1 po = pc4.newInstance();
            assertNull(po.ooi("11", 11));
            assertEquals(counter.get(), 1);
            counter.set(0);
        }
    }

    @Test
    public void testInvokeSuper() {
        ProxyClassGenerator generator = new JdkProxyClassGenerator();
        IntVar counter = IntVar.of(0);
        SuperInter si = new SuperInter() {
            @Override
            public String si2() {
                return "";
            }
        };
        ProxyClass pc = generator.generate(
            null, Jie.list(SuperInter.class),
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
        SuperInter psi = pc.newInstance();
        assertEquals(psi.si1(), si.si1());
        assertEquals(counter.get(), 1);
        expectThrows(AbstractMethodError.class, psi::si2);
        assertEquals(counter.get(), 2);
        counter.set(0);

        // unsupported default method invocable
        Invocable invocable = JdkProxyClassGenerator.UNSUPPORTED_DEFAULT_METHOD_INVOCABLE;
        expectThrows(JdkProxyClassGenerator.JdkProxyException.class, () -> invocable.invokeChecked(null));
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

    public interface InterOverpass11 extends InterOverpass1 {}

    public interface InterOverpass111 extends InterOverpass11 {}

    public interface SuperInter {

        default String si1() {
            return "t1";
        }

        String si2();
    }
}
