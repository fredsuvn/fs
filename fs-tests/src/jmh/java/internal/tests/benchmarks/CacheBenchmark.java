package internal.tests.benchmarks;

import internal.tests.api.CacheApi;
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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(3)
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

    @Param({
        "fs-simpleWeak",
        "fs-simpleSoft",
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
        this.cache = CacheApi.createCache(cacheType);
    }

    @Benchmark
    @Threads(8)
    public void computeIfAbsent(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        KeyValue data = DATA[random.nextInt(DATA_SIZE)];
        String value = cache.get(data.key, k -> data.value);
        blackhole.consume(value);
    }


    private record KeyValue(String key, String value) {}
}
