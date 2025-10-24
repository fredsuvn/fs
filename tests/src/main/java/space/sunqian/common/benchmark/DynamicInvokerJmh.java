// package xyz.sunqian.common.benchmark;
//
// import org.openjdk.jmh.annotations.Benchmark;
// import org.openjdk.jmh.annotations.BenchmarkMode;
// import org.openjdk.jmh.annotations.Fork;
// import org.openjdk.jmh.annotations.Measurement;
// import org.openjdk.jmh.annotations.Mode;
// import org.openjdk.jmh.annotations.OutputTimeUnit;
// import org.openjdk.jmh.annotations.Scope;
// import org.openjdk.jmh.annotations.State;
// import org.openjdk.jmh.annotations.Warmup;
// import xyz.sunqian.common.invoke.JieInvoke;
//
// import java.lang.invoke.MethodHandle;
// import java.lang.invoke.MethodHandles;
// import java.lang.reflect.Method;
// import java.util.concurrent.TimeUnit;
//
// @BenchmarkMode(Mode.Throughput)
// @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Fork(1)
// @State(value = Scope.Benchmark)
// @OutputTimeUnit(TimeUnit.MILLISECONDS)
// public class DynamicInvokerJmh {
//
//     public static String doSomething(String a, String b) {
//         return a + b;
//     }
//
//     private static final Method DO_SOMETHING;
//
//     static {
//         try {
//             DO_SOMETHING = DynamicInvokerJmh.class.getMethod("doSomething", String.class, String.class);
//         } catch (NoSuchMethodException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     private static final Invoker ofReflection = new OfReflection(DO_SOMETHING);
//     private static final Invoker ofMethodHandle = new OfMethodHandle(DO_SOMETHING);
//     private static final MethodHandle directHandle;
//
//     static {
//         try {
//             directHandle = MethodHandles.lookup().unreflect(DO_SOMETHING);
//         } catch (IllegalAccessException e) {
//             throw new RuntimeException(e);
//         }
//     }
//
//     @Benchmark
//     public void ofReflection() throws Throwable {
//         ofReflection.invoke(null, new Object[]{"a", "b"});
//     }
//
//     @Benchmark
//     public void ofMethodHandle() throws Throwable {
//         ofMethodHandle.invoke(null, new Object[]{"a", "b"});
//     }
//
//     @Benchmark
//     public void directHandle() throws Throwable {
//         directHandle.invoke("a", "b");
//     }
//
//     public static void main(String[] args) throws Exception {
//         org.openjdk.jmh.Main.main(args);
//     }
//
//     public interface Invoker {
//
//         Object invoke(Object inst, Object[] args) throws Throwable;
//     }
//
//     public static class OfReflection implements Invoker {
//
//         private final Method method;
//
//         public OfReflection(Method method) {
//             this.method = method;
//         }
//
//         @Override
//         public Object invoke(Object inst, Object[] args) throws Throwable {
//             return method.invoke(inst, args);
//         }
//     }
//
//     public static class OfMethodHandle implements Invoker {
//
//         private final MethodHandle handle;
//
//         public OfMethodHandle(Method method) {
//             try {
//                 this.handle = MethodHandles.lookup().unreflect(method);
//             } catch (IllegalAccessException e) {
//                 throw new RuntimeException(e);
//             }
//         }
//
//         @Override
//         public Object invoke(Object inst, Object[] args) throws Throwable {
//             // switch (args.length) {
//             //     case 0:
//             //         return handle.invoke();
//             //     case 1:
//             //         return handle.invoke(args[0]);
//             //     case 2:
//             //         return handle.invoke(args[0], args[1]);
//             // }
//             return handle.invokeWithArguments(args);
//         }
//     }
// }
