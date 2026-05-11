package internal.benchmark;

import internal.api.CacheApi;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CacheJmh extends AbstractJmhBenchmark {

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

    private CacheApi<String, String> cacheApi;

    @Setup(Level.Trial)
    public void setup() {
        this.cacheApi = CacheApi.createApi(cacheType);
    }

    @Benchmark
    @Threads(8)
    public void computeIfAbsent(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        KeyValue data = DATA[random.nextInt(DATA_SIZE)];
        String value = cacheApi.get(data.key, k -> data.value);
        blackhole.consume(value);
    }

    private record KeyValue(String key, String value) {}
}
