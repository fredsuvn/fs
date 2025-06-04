package xyz.sunqian.common.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;
import xyz.sunqian.common.reflect.proxy.ProxyMode;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ProxyJmh {

    private static final String UUIDS;

    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(UUID.randomUUID());
        }
        UUIDS = sb.toString();
    }

    private static void doPrefixAndSuffix() {
        // UUIDS.matches(".+A.+");
    }

    private static final class DirectImpl extends ForProxy.Impl {

        @Override
        public String doSomething(String input) {
            doPrefixAndSuffix();
            String result = super.doSomething(input);
            doPrefixAndSuffix();
            return result;
        }
    }

    private static final ForProxy impl = new ForProxy.Impl();

    private static final ForProxy directImpl = new DirectImpl();

    private static final ForProxy asmImpl = ProxyMode.ASM.generate(null, Jie.list(ForProxy.class), new ProxyMethodHandler() {
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
            doPrefixAndSuffix();
            Object result = invoker.invokeSuper(proxy, args);
            doPrefixAndSuffix();
            return result;
        }
    }).newInstance();

    private static final ForProxy jdkImpl = ProxyMode.JDK.generate(null, Jie.list(ForProxy.class), new ProxyMethodHandler() {
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
            doPrefixAndSuffix();
            Object result = invoker.invokeSuper(proxy, args);
            doPrefixAndSuffix();
            return result;
        }
    }).newInstance();

    private static final ForProxy jdkProxy = (ForProxy) Proxy.newProxyInstance(
        new BytesClassLoader(),
        new Class<?>[]{ForProxy.class},
        new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                doPrefixAndSuffix();
                Object result = method.invoke(impl, args);
                doPrefixAndSuffix();
                return result;
            }
        }
    );

    @Benchmark
    public void directImpl() {
        directImpl.doSomething(UUIDS);
    }

    @Benchmark
    public void asmImpl() {
        asmImpl.doSomething(UUIDS);
    }

    @Benchmark
    public void jdkImpl() {
        jdkImpl.doSomething(UUIDS);
    }

    @Benchmark
    public void jdkProxy() {
        jdkProxy.doSomething(UUIDS);
    }

    @Benchmark
    public void directImplSimple() {
        directImpl.doSomeSimple(UUIDS);
    }

    @Benchmark
    public void asmImplSimple() {
        asmImpl.doSomeSimple(UUIDS);
    }

    @Benchmark
    public void jdkImplSimple() {
        jdkImpl.doSomeSimple(UUIDS);
    }

    @Benchmark
    public void jdkProxySimple() {
        jdkProxy.doSomeSimple(UUIDS);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // Benchmark                   Mode  Cnt        Score        Error   Units
    // ProxyJmh.asmImpl           thrpt   15     1054.900 ±     17.863  ops/ms
    // ProxyJmh.asmImplSimple     thrpt   15   411217.788 ±  17314.753  ops/ms
    // ProxyJmh.directImpl        thrpt   15     1057.106 ±     20.946  ops/ms
    // ProxyJmh.directImplSimple  thrpt   15  4651567.167 ± 119688.626  ops/ms
    // ProxyJmh.jdkImpl           thrpt   15      997.243 ±     19.840  ops/ms
    // ProxyJmh.jdkImplSimple     thrpt   15    22619.390 ±    333.944  ops/ms
    // ProxyJmh.jdkProxy          thrpt   15     1046.002 ±     23.540  ops/ms
    // ProxyJmh.jdkProxySimple    thrpt   15   313120.218 ±   5717.647  ops/ms
}
