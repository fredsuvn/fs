package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;
import xyz.sunqian.common.reflect.proxy.asm.AsmProxyClassGenerator;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

public class AsmProxyTest {

    @Test
    public void testAsmProxy() {
        ProxyClassGenerator generator = new AsmProxyClassGenerator();
        IntVar counter = IntVar.of(0);
        ProxyClass pc = generator.generate(
            new Class<?>[]{Foo.class},
            new ProxyMethodHandler() {

                @Override
                public boolean requiresProxy(Method method) {
                    String[] names = {"foo1"};
                    return JieArray.indexOf(names, method.getName()) >= 0;
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
        Foo foo = pc.newInstance();
        foo.foo1();
        assertEquals(counter.get(), 1);
    }


    public static class Foo {
        public String foo1() {
            System.out.println(getClass());
            return "foo1";
        }
    }
}
