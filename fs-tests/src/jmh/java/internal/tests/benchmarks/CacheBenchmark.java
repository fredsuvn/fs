package internal.tests.benchmarks;

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
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.common.cache.SimpleCache;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CacheBenchmark {

    private static final int DATA_SIZE = 100000;
    private static final KeyValue[] DATA = new KeyValue[DATA_SIZE];

    static {
        Random random = new Random();
        for (int i = 0; i < DATA.length; i++) {
            String key = String.valueOf(random.nextDouble());
            String value = String.valueOf(random.nextDouble() * random.nextDouble());
            DATA[i] = new KeyValue(key, value);
        }
    }

    private static KeyValue nextData() {
        return DATA[ThreadLocalRandom.current().nextInt(DATA.length)];
    }

    private static final int CAPACITY = 1000;

    @Param({
        "simpleWeak",
        "simpleSoft",
        "caffeineWeak",
        "caffeineSoft",
        "caffeine",
        "guavaWeak",
        "guavaSoft",
        "guava",
        "concurrentHashMap",
    })
    private String cacheType;

    private CacheApi<String, String> cache;

    @Setup(Level.Trial)
    public void setup() {
        this.cache = createCache(cacheType);
    }

    @Benchmark
    @Threads(8)
    public void computeIfAbsent(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        KeyValue data = DATA[random.nextInt(DATA_SIZE)];
        String value = cache.get(data.key, k -> data.value);
        blackhole.consume(value);
    }

    public <K, V> CacheApi<K, V> createCache(String cacheType) {
        return switch (cacheType) {
            case "simpleWeak" -> {
                SimpleCache<K, V> cache = SimpleCache.ofWeak();
                yield cache::get;
            }
            case "simpleSoft" -> {
                SimpleCache<K, V> cache = SimpleCache.ofSoft();
                yield cache::get;
            }
            case "caffeineWeak" -> {
                Cache<K, V> cache = Caffeine.newBuilder().weakValues().build();
                yield cache::get;
            }
            case "caffeineSoft" -> {
                Cache<K, V> cache = Caffeine.newBuilder().softValues().build();
                yield cache::get;
            }
            case "caffeine" -> {
                Cache<K, V> cache = Caffeine.newBuilder().maximumSize(CAPACITY).build();
                yield cache::get;
            }
            case "guavaWeak" -> {
                com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder().weakValues().build();
                yield (key, function) -> cache.get(key, () -> function.apply(key));
            }
            case "guavaSoft" -> {
                com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder().softValues().build();
                yield (key, function) -> cache.get(key, () -> function.apply(key));
            }
            case "guava" -> {
                com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder().maximumSize(CAPACITY).build();
                yield (key, function) -> cache.get(key, () -> function.apply(key));
            }
            case "concurrentHashMap" -> {
                ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
                yield cache::computeIfAbsent;
            }
            default -> throw new IllegalArgumentException("cacheType is not support");
        };
    }

    public interface CacheApi<K, V> {
        V get(K key, Function<? super K, ? extends V> function) throws Exception;
    }

    private record KeyValue(String key, String value) {}
}
