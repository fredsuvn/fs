// package benchmark;
//
// import com.github.benmanes.caffeine.cache.Caffeine;
// import com.google.common.cache.CacheBuilder;
// import org.openjdk.jmh.annotations.*;
// import org.openjdk.jmh.infra.BenchmarkParams;
// import org.openjdk.jmh.infra.Blackhole;
// import xyz.sunqian.common.cache.Cache;
//
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ThreadLocalRandom;
// import java.util.concurrent.TimeUnit;
//
// @BenchmarkMode(Mode.Throughput)
// @Warmup(iterations = 3, time = 5)
// @Measurement(iterations = 3, time = 5)
// @Fork(1)
// @State(value = Scope.Benchmark)
// @OutputTimeUnit(TimeUnit.MILLISECONDS)
// public class CacheJmh {
//
//     private static final int INIT_SIZE = 1024 * 10;
//     private static final int MAX_SIZE = INIT_SIZE * 2;
//
//     private Cache<Integer, Integer> fsSoft;
//     private Cache<Integer, Integer> fsWeak;
//     private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeine;
//     private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineSoft;
//     private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineWeak;
//     private com.google.common.cache.Cache<Integer, Integer> guava;
//     private com.google.common.cache.Cache<Integer, Integer> guavaSoft;
//     private com.google.common.cache.Cache<Integer, Integer> guavaWeak;
//
//     @Setup(Level.Iteration)
//     public void init(BenchmarkParams params) {
//         if (params.getBenchmark().endsWith(".fsSoft")
//             || params.getBenchmark().endsWith(".fsSoftGs")
//             || params.getBenchmark().endsWith(".fsSoftFull")) {
//             fsSoft = Cache.newBuilder()
//                 // .removeListener((k, v, c) -> {
//                 // })
//                 .softValues()
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 fsSoft.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".fsWeak")
//             || params.getBenchmark().endsWith(".fsWeakFull")) {
//             fsWeak = Cache.newBuilder()
//                 // .removeListener((k, v, c) -> {
//                 // })
//                 .weakValues()
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 fsWeak.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".caffeineSoft")
//             || params.getBenchmark().endsWith(".caffeineSoftGs")
//             || params.getBenchmark().endsWith(".caffeineSoftFull")) {
//             caffeineSoft = Caffeine.newBuilder()
//                 // .removalListener((k, v, c) -> {
//                 // })
//                 .softValues()
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 caffeineSoft.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".caffeine")
//             || params.getBenchmark().endsWith(".caffeineFull")) {
//             caffeine = Caffeine.newBuilder()
//                 // .removalListener((k, v, c) -> {
//                 // })
//                 .maximumSize(MAX_SIZE / 3)
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 caffeine.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".caffeineWeak")
//             || params.getBenchmark().endsWith(".caffeineWeakFull")) {
//             caffeineWeak = Caffeine.newBuilder()
//                 // .removalListener((k, v, c) -> {
//                 // })
//                 .weakKeys()
//                 .weakValues()
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 caffeineWeak.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".guava")
//             || params.getBenchmark().endsWith(".guavaFull")) {
//             guava = CacheBuilder.newBuilder()
//                 // .removalListener((k, v, c) -> {
//                 // })
//                 .maximumSize(MAX_SIZE / 3)
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 guava.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".guavaSoft")
//             || params.getBenchmark().endsWith(".guavaSoftFull")) {
//             guavaSoft = CacheBuilder.newBuilder()
//                 // .removalListener((k, v, c) -> {
//                 // })
//                 .softValues()
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 guavaSoft.put(i, i);
//             }
//             return;
//         }
//         if (params.getBenchmark().endsWith(".guavaWeak")
//             || params.getBenchmark().endsWith(".guavaWeakFull")) {
//             guavaWeak = CacheBuilder.newBuilder()
//                 // .removalListener((k, v, c) -> {
//                 // })
//                 .weakKeys()
//                 .weakValues()
//                 .build();
//             for (int i = 0; i < INIT_SIZE; i++) {
//                 guavaWeak.put(i, i);
//             }
//             return;
//         }
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void fsSoft(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(fsSoft.get(value));
//         bh.consume(fsSoft.compute(value * 100, k -> k));
//         bh.consume(fsSoft.compute(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void fsSoftGs(Blackhole bh) {
//         int value = nextGaussian(MAX_SIZE);
//         bh.consume(fsSoft.get(value));
//         bh.consume(fsSoft.compute(value * 100, k -> k));
//         bh.consume(fsSoft.compute(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void fsSoftFull(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(fsSoft.get(value));
//         fsSoft.put(value, value);
//         fsSoft.remove(value);
//         bh.consume(fsSoft.compute(value * 100, k -> k));
//         bh.consume(fsSoft.compute(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void fsWeak(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(fsWeak.get(value));
//         bh.consume(fsWeak.compute(value * 100, k -> k));
//         bh.consume(fsWeak.compute(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void fsWeakFull(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(fsWeak.get(value));
//         fsWeak.put(value, value);
//         fsWeak.remove(value);
//         bh.consume(fsWeak.compute(value * 100, k -> k));
//         bh.consume(fsWeak.compute(value * 100, k -> k));
//     }
//
//
//     @Benchmark
//     @Threads(64)
//     public void caffeine(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(caffeine.getIfPresent(value));
//         bh.consume(caffeine.get(value * 100, k -> k));
//         bh.consume(caffeine.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void caffeineFull(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(caffeine.getIfPresent(value));
//         caffeine.put(value, value);
//         caffeine.invalidate(value);
//         bh.consume(caffeine.get(value * 100, k -> k));
//         bh.consume(caffeine.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void caffeineSoft(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(caffeineSoft.getIfPresent(value));
//         bh.consume(caffeineSoft.get(value * 100, k -> k));
//         bh.consume(caffeineSoft.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void caffeineSoftGs(Blackhole bh) {
//         int value = nextGaussian(MAX_SIZE);
//         bh.consume(caffeineSoft.getIfPresent(value));
//         bh.consume(caffeineSoft.get(value * 100, k -> k));
//         bh.consume(caffeineSoft.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void caffeineSoftFull(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(caffeineSoft.getIfPresent(value));
//         caffeineSoft.put(value, value);
//         caffeineSoft.invalidate(value);
//         bh.consume(caffeineSoft.get(value * 100, k -> k));
//         bh.consume(caffeineSoft.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void caffeineWeak(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(caffeineWeak.getIfPresent(value));
//         bh.consume(caffeineWeak.get(value * 100, k -> k));
//         bh.consume(caffeineWeak.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void caffeineWeakFull(Blackhole bh) {
//         int value = next(INIT_SIZE);
//         bh.consume(caffeineWeak.getIfPresent(value));
//         caffeineWeak.put(value, value);
//         caffeineWeak.invalidate(value);
//         bh.consume(caffeineWeak.get(value * 100, k -> k));
//         bh.consume(caffeineWeak.get(value * 100, k -> k));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void guava(Blackhole bh) throws ExecutionException {
//         int value = next(INIT_SIZE);
//         bh.consume(guava.getIfPresent(value));
//         bh.consume(guava.get(value * 100, () -> value));
//         bh.consume(guava.get(value * 100, () -> value));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void guavaFull(Blackhole bh) throws ExecutionException {
//         int value = next(INIT_SIZE);
//         bh.consume(guava.getIfPresent(value));
//         guava.put(value, value);
//         guava.invalidate(value);
//         bh.consume(guava.get(value * 100, () -> value));
//         bh.consume(guava.get(value * 100, () -> value));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void guavaSoft(Blackhole bh) throws ExecutionException {
//         int value = next(INIT_SIZE);
//         bh.consume(guavaSoft.getIfPresent(value));
//         bh.consume(guavaSoft.get(value * 100, () -> value));
//         bh.consume(guavaSoft.get(value * 100, () -> value));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void guavaSoftFull(Blackhole bh) throws ExecutionException {
//         int value = next(INIT_SIZE);
//         bh.consume(guavaSoft.getIfPresent(value));
//         guavaSoft.put(value, value);
//         guavaSoft.invalidate(value);
//         bh.consume(guavaSoft.get(value * 100, () -> value));
//         bh.consume(guavaSoft.get(value * 100, () -> value));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void guavaWeak(Blackhole bh) throws ExecutionException {
//         int value = next(INIT_SIZE);
//         bh.consume(guavaWeak.getIfPresent(value));
//         bh.consume(guavaWeak.get(value * 100, () -> value));
//         bh.consume(guavaWeak.get(value * 100, () -> value));
//     }
//
//     @Benchmark
//     @Threads(64)
//     public void guavaWeakFull(Blackhole bh) throws ExecutionException {
//         int value = next(INIT_SIZE);
//         bh.consume(guavaWeak.getIfPresent(value));
//         guavaWeak.put(value, value);
//         guavaWeak.invalidate(value);
//         bh.consume(guavaWeak.get(value * 100, () -> value));
//         bh.consume(guavaWeak.get(value * 100, () -> value));
//     }
//
//     private int next(int max) {
//         return ThreadLocalRandom.current().nextInt(max);
//     }
//
//     private int nextGaussian(int max) {
//         return (int) (ThreadLocalRandom.current().nextGaussian() * max);
//     }
// }
