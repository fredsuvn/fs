package xyz.sunqian.common.benchmark;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import xyz.sunqian.common.cache.SimpleCache;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(5)
// @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheJmh {

    private static final SimpleCache<Integer, String> simpleWeakCache = SimpleCache.ofWeak();
    private static final SimpleCache<Integer, String> simpleSoftCache = SimpleCache.ofSoft();
    private static final Cache<Integer, String> caffeineWeakCache = Caffeine.newBuilder().weakValues().build();
    private static final Cache<Integer, String> caffeineSoftCache = Caffeine.newBuilder().softValues().build();
    private static final Cache<Integer, String> caffeineCache = Caffeine.newBuilder().maximumSize(10000).build();
    private static final com.google.common.cache.Cache<Integer, String> guavaWeakCache = CacheBuilder.newBuilder().weakValues().build();
    private static final com.google.common.cache.Cache<Integer, String> guavaSoftCache = CacheBuilder.newBuilder().softValues().build();
    private static final com.google.common.cache.Cache<Integer, String> guavaCache = CacheBuilder.newBuilder().maximumSize(10000).build();
    // private static final ConcurrentHashMap<Integer, String> concurrentHashMap = new ConcurrentHashMap<>();

    private static final long randomSeed = System.currentTimeMillis();
    private static Random random;

    private static final String value = "hello";
    private static final int max = 1000000;

    @Setup(Level.Invocation)
    public void resetRandom() {
        random = new Random(randomSeed);
    }

    private int nextKey() {
        double gs = random.nextGaussian();
        return (int) (gs * max);
    }

    @Benchmark
    public void simpleWeakCache() {
        int key = nextKey();
        simpleWeakCache.get(key);
        key = nextKey();
        simpleWeakCache.get(key, k -> value);
        // key = nextKey();
        // simpleWeakCache.put(key, value);
    }

    @Benchmark
    public void simpleSoftCache() {
        int key = nextKey();
        simpleSoftCache.get(key);
        key = nextKey();
        simpleSoftCache.get(key, k -> value);
        // key = nextKey();
        // simpleSoftCache.put(key, value);
    }

    @Benchmark
    public void caffeineWeakCache() {
        int key = nextKey();
        caffeineWeakCache.getIfPresent(key);
        key = nextKey();
        caffeineWeakCache.get(key, k -> value);
        // key = nextKey();
        // caffeineWeakCache.put(key, value);
    }

    @Benchmark
    public void caffeineSoftCache() {
        int key = nextKey();
        caffeineSoftCache.getIfPresent(key);
        key = nextKey();
        caffeineSoftCache.get(key, k -> value);
        // key = nextKey();
        // caffeineSoftCache.put(key, value);
    }

    @Benchmark
    public void caffeineCache() {
        int key = nextKey();
        caffeineCache.getIfPresent(key);
        key = nextKey();
        caffeineCache.get(key, k -> value);
        // key = nextKey();
        // caffeineCache.put(key, value);
    }

    @Benchmark
    public void guavaWeakCache() throws Exception {
        int key = nextKey();
        guavaWeakCache.getIfPresent(key);
        key = nextKey();
        guavaWeakCache.get(key, () -> value);
        // key = nextKey();
        // guavaWeakCache.put(key, value);
    }

    @Benchmark
    public void guavaSoftCache() throws Exception {
        int key = nextKey();
        guavaSoftCache.getIfPresent(key);
        key = nextKey();
        guavaSoftCache.get(key, () -> value);
        // key = nextKey();
        // guavaSoftCache.put(key, value);
    }

    @Benchmark
    public void guavaCache() throws Exception {
        int key = nextKey();
        guavaCache.getIfPresent(key);
        key = nextKey();
        guavaCache.get(key, () -> value);
        // key = nextKey();
        // guavaCache.put(key, value);
    }

    // @Benchmark
    // public void concurrentMap() {
    //     int key = nextKey();
    //     concurrentHashMap.computeIfAbsent(key, k -> value);
    // }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // Benchmark                    Mode  Cnt     Score      Error   Units
    // CacheJmh.caffeineCache      thrpt   15  8329.264 ± 2197.013  ops/ms
    // CacheJmh.caffeineSoftCache  thrpt   15  6052.971 ±  935.735  ops/ms
    // CacheJmh.caffeineWeakCache  thrpt   15  6425.101 ±  571.973  ops/ms
    // CacheJmh.guavaCache         thrpt   15  6003.908 ±  876.103  ops/ms
    // CacheJmh.guavaSoftCache     thrpt   15  6159.012 ± 1608.401  ops/ms
    // CacheJmh.guavaWeakCache     thrpt   15  7863.530 ±  208.349  ops/ms
    // CacheJmh.simpleSoftCache    thrpt   15  7632.215 ±  676.453  ops/ms
    // CacheJmh.simpleWeakCache    thrpt   15  7386.267 ±  846.319  ops/ms
}
