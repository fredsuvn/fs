package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.IntVar;
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
            ProxyClass pc3 = generator.generate(
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
                        return invoker.invoke(proxy, args);
                    }
                }
            );
            InterA pa3 = pc3.newInstance();
            expectThrows(StackOverflowError.class, () -> pa3.aa("11", 11));
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

        {
            InvokeSuper tm = new InvokeSuper() {};
            ProxyClass pc5 = generator.generate(
                null, Jie.list(InvokeSuper.class),
                new ProxyMethodHandler() {

                    @Override
                    public boolean requiresProxy(Method method) {
                        return method.getName().equals("t2");
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
            InvokeSuper pt = pc5.newInstance();
            assertEquals(pt.t1(), tm.t1());
            assertEquals(counter.get(), 0);
            assertSame(pt.t2(), result);
            assertEquals(counter.get(), 1);
            counter.set(0);
        }
    }

    @Test
    public void testJdkProxyException() {
        String message = "hello";
        Throwable cause = new RuntimeException(message);
        JdkProxyClassGenerator.JdkProxyException e = new JdkProxyClassGenerator.JdkProxyException(cause);
        assertEquals(e.getMessage(), message);
        assertSame(e.getCause(), cause);
    }

    public interface InterA {

        String aa(String a0, int a1);
    }

    public interface InterB {

        default String bb(String a0, int a1) {
            return a0 + a1;
        }
    }

    public interface InterC<T> {

        default T cc(T a0, int a1) {
            return a0;
        }
    }

    public interface InterOverpass1 {
        default String ooi(String a0, int a1) {
            return a0 + a1;
        }
    }

    public interface InterOverpass11 extends InterOverpass1 {}

    public interface InterOverpass111 extends InterOverpass11 {}

    public interface InvokeSuper {

        default String t1() {
            return "t1";
        }

        default String t2() {
            return "t2";
        }
    }
}
