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
    private static final int max = 100;
    private static int key = 0;

    @Setup(Level.Invocation)
    public void resetRandom() {
        random = new Random(randomSeed);
    }

    private int nextKey() {
        // double d = random.nextGaussian();
        // return (int) (d * max);

        if (key > max) {
            key = 0;
        }
        return key++;
    }

    @Benchmark
    public void simpleWeakCache() {
        int key = nextKey();
        // simpleWeakCache.get(key);
        // key = nextKey();
        simpleWeakCache.get(key, k -> value);
        // key = nextKey();
        // simpleWeakCache.put(key, value);
    }

    @Benchmark
    public void simpleSoftCache() {
        int key = nextKey();
        // simpleSoftCache.get(key);
        // key = nextKey();
        simpleSoftCache.get(key, k -> value);
        // key = nextKey();
        // simpleSoftCache.put(key, value);
    }

    @Benchmark
    public void caffeineWeakCache() {
        int key = nextKey();
        // caffeineWeakCache.getIfPresent(key);
        // key = nextKey();
        caffeineWeakCache.get(key, k -> value);
        // key = nextKey();
        // caffeineWeakCache.put(key, value);
    }

    @Benchmark
    public void caffeineSoftCache() {
        int key = nextKey();
        // caffeineSoftCache.getIfPresent(key);
        // key = nextKey();
        caffeineSoftCache.get(key, k -> value);
        // key = nextKey();
        // caffeineSoftCache.put(key, value);
    }

    @Benchmark
    public void caffeineCache() {
        int key = nextKey();
        // caffeineCache.getIfPresent(key);
        // key = nextKey();
        caffeineCache.get(key, k -> value);
        // key = nextKey();
        // caffeineCache.put(key, value);
    }

    @Benchmark
    public void guavaWeakCache() throws Exception {
        int key = nextKey();
        // guavaWeakCache.getIfPresent(key);
        // key = nextKey();
        guavaWeakCache.get(key, () -> value);
        // key = nextKey();
        // guavaWeakCache.put(key, value);
    }

    @Benchmark
    public void guavaSoftCache() throws Exception {
        int key = nextKey();
        // guavaSoftCache.getIfPresent(key);
        // key = nextKey();
        guavaSoftCache.get(key, () -> value);
        // key = nextKey();
        // guavaSoftCache.put(key, value);
    }

    @Benchmark
    public void guavaCache() throws Exception {
        int key = nextKey();
        // guavaCache.getIfPresent(key);
        // key = nextKey();
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

    // 1.8, max = 1000000
    // Benchmark                    Mode  Cnt     Score      Error   Units
    // CacheJmh.caffeineCache      thrpt   15  8833.668 ± 1555.508  ops/ms
    // CacheJmh.caffeineSoftCache  thrpt   15  5915.108 ± 1113.756  ops/ms
    // CacheJmh.caffeineWeakCache  thrpt   15  6788.272 ±  542.033  ops/ms
    // CacheJmh.guavaCache         thrpt   15  6764.647 ±  792.849  ops/ms
    // CacheJmh.guavaSoftCache     thrpt   15  8447.886 ±  135.096  ops/ms
    // CacheJmh.guavaWeakCache     thrpt   15  8198.285 ±  787.352  ops/ms
    // CacheJmh.simpleSoftCache    thrpt   15  8117.297 ± 1896.493  ops/ms
    // CacheJmh.simpleWeakCache    thrpt   15  9169.518 ±  953.596  ops/ms

    // 21, max = 1000000
    // Benchmark                    Mode  Cnt      Score      Error   Units
    // CacheJmh.caffeineCache      thrpt   15  10302.236 ± 1078.281  ops/ms
    // CacheJmh.caffeineSoftCache  thrpt   15   8303.985 ±  376.398  ops/ms
    // CacheJmh.caffeineWeakCache  thrpt   15   7843.164 ±  431.880  ops/ms
    // CacheJmh.guavaCache         thrpt   15   7109.553 ±  595.350  ops/ms
    // CacheJmh.guavaSoftCache     thrpt   15   8567.682 ± 1594.552  ops/ms
    // CacheJmh.guavaWeakCache     thrpt   15   9290.737 ±  919.522  ops/ms
    // CacheJmh.simpleSoftCache    thrpt   15  10917.945 ±  137.824  ops/ms
    // CacheJmh.simpleWeakCache    thrpt   15  10395.673 ± 1163.620  ops/ms

    // 1.8, 0 - 100
    // Benchmark                    Mode  Cnt      Score      Error   Units
    // CacheJmh.caffeineCache      thrpt   15  44694.099 ±  424.318  ops/ms
    // CacheJmh.caffeineSoftCache  thrpt   15  21128.623 ±  881.527  ops/ms
    // CacheJmh.caffeineWeakCache  thrpt   15  21582.304 ±  874.896  ops/ms
    // CacheJmh.guavaCache         thrpt   15  16674.343 ±  215.666  ops/ms
    // CacheJmh.guavaSoftCache     thrpt   15  27549.797 ±  210.121  ops/ms
    // CacheJmh.guavaWeakCache     thrpt   15  29452.458 ±  493.650  ops/ms
    // CacheJmh.simpleSoftCache    thrpt   15  45165.334 ±  852.212  ops/ms
    // CacheJmh.simpleWeakCache    thrpt   15  45556.343 ± 1031.038  ops/ms

    // 21, 0 - 100
    // Benchmark                    Mode  Cnt      Score     Error   Units
    // CacheJmh.caffeineCache      thrpt   15  44248.739 ± 177.115  ops/ms
    // CacheJmh.caffeineSoftCache  thrpt   15  20350.407 ± 779.108  ops/ms
    // CacheJmh.caffeineWeakCache  thrpt   15  21003.369 ± 570.520  ops/ms
    // CacheJmh.guavaCache         thrpt   15  14984.114 ± 173.463  ops/ms
    // CacheJmh.guavaSoftCache     thrpt   15  32449.117 ± 549.786  ops/ms
    // CacheJmh.guavaWeakCache     thrpt   15  33180.463 ± 565.954  ops/ms
    // CacheJmh.simpleSoftCache    thrpt   15  45289.764 ± 233.634  ops/ms
    // CacheJmh.simpleWeakCache    thrpt   15  46085.584 ± 257.607  ops/ms
}
