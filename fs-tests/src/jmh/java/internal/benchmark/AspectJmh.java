package internal.benchmark;

import internal.api.AspectApi;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AspectJmh extends AbstractJmhBenchmark {

    @Param({
        "fs-asm",
        //"byte-buddy",
        "direct",
    })
    private String aspectType;

    @Param({
        "true",
        "false",
    })
    private String withPrimitive;

    private AspectApi aspectApi;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        this.aspectApi = AspectApi.createApi(aspectType);
    }

    @Benchmark
    public void aspect(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        String value = "true".equals(withPrimitive) ?
            aspectApi.withPrimitive(random.nextInt(), random.nextLong(), "hello")
            :
            aspectApi.withoutPrimitive(random.nextInt(), random.nextLong(), "hello");
        blackhole.consume(value);
    }
}
