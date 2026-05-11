package internal.benchmark;

import internal.api.ProxyApi;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ProxyJmh extends AbstractJmhBenchmark {

    @Param({
        "fs-asm",
        "fs-jdk",
        //"byte-buddy",
        //"cglib",
        "direct",
    })
    private String proxyType;

    @Param({
        "true",
        "false",
    })
    private String withPrimitive;

    private ProxyApi proxyApi;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        this.proxyApi = ProxyApi.createApi(proxyType);
    }

    @Benchmark
    public void proxy(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        String value = "true".equals(withPrimitive) ?
            proxyApi.withPrimitive(random.nextInt(), random.nextLong(), "hello")
            :
            proxyApi.withoutPrimitive(random.nextInt(), random.nextLong(), "hello");
        blackhole.consume(value);
    }
}
