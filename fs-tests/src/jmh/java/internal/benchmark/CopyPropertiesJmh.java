package internal.benchmark;

import internal.api.PropertiesCopier;
import internal.data.TestPropsData;
import internal.data.TestPropsTarget;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;

public class CopyPropertiesJmh extends AbstractJmhBenchmark {

    private final TestPropsData data = new TestPropsData();
    @Param({
        "fs",
        "fs-newInstMode",
        "spring",
        "apache",
        "hutool",
        //"direct"
    })
    private String copierType;
    private PropertiesCopier copier;

    {
        data.setI1(1);
        data.setL1(2L);
        data.setStr1("hello");
        data.setIi1(3);
        data.setLl1(4L);
        data.setBb1(new BigDecimal("5.0"));
        data.setI2(1);
        data.setL2(2L);
        data.setStr2("hello");
        data.setIi2(3);
        data.setLl2(4L);
        data.setBb2(new BigDecimal("5.0"));
    }

    @Setup(Level.Trial)
    public void setup() {
        this.copier = PropertiesCopier.createCopier(copierType);
    }

    @Benchmark
    public void copyProperties(Blackhole blackhole) throws Exception {
        TestPropsTarget target = new TestPropsTarget();
        copier.copyProperties(data, target, false);
        blackhole.consume(target);
    }
}
