// package benchmark;
//
// import cn.hutool.core.bean.BeanUtil;
// import lombok.Data;
// import org.apache.commons.beanutils.BeanUtils;
// import org.openjdk.jmh.annotations.*;
// import xyz.sunqian.common.base.Jie;
//
// import java.lang.reflect.InvocationTargetException;
// import java.util.Arrays;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
//
// @BenchmarkMode(Mode.Throughput)
// @Warmup(iterations = 3, time = 5)
// @Measurement(iterations = 3, time = 5)
// @Threads(1)
// @Fork(1)
// @State(value = Scope.Benchmark)
// @OutputTimeUnit(TimeUnit.MILLISECONDS)
// public class CopyPropertiesJmh {
//
//     private static final Bean bean = new Bean();
//
//     @Setup
//     public void init() {
//     }
//
//     @Benchmark
//     public void springCopy() {
//         org.springframework.beans.BeanUtils.copyProperties(bean, new Bean());
//     }
//
//     @Benchmark
//     public void hutoolCopy() {
//         BeanUtil.copyProperties(bean, new Bean());
//     }
//
//     @Benchmark
//     public void apacheCopy() throws InvocationTargetException, IllegalAccessException {
//         BeanUtils.copyProperties(new Bean(), bean);
//     }
//
//     @Benchmark
//     public void fsCopy() {
//         Jie.copyProperties(bean, new Bean());
//     }
//
//     @Benchmark
//     public void springCopyIgnore() {
//         org.springframework.beans.BeanUtils.copyProperties(bean, new Bean(), "s1", "i1", "l1");
//     }
//
//     @Benchmark
//     public void hutoolCopyIgnore() {
//         BeanUtil.copyProperties(bean, new Bean(), "s1", "i1", "l1");
//     }
//
//     @Benchmark
//     public void fsCopyIgnore() {
//         Jie.copyProperties(bean, new Bean(), "s1", "i1", "l1");
//     }
//
//     @Data
//     public static class Bean {
//         private String s1 = "s1";
//         private String s2 = "s2";
//         private String s3 = "s3";
//         private String s4 = "s4";
//         private int i1 = 1;
//         private int i2 = 2;
//         private int i3 = 3;
//         private int i4 = 4;
//         private List<String> l1 = Arrays.asList("1", "2");
//         private List<String> l2 = Arrays.asList("1", "2");
//         private List<String> l3 = Arrays.asList("1", "2");
//         private List<String> l4 = Arrays.asList("1", "2");
//     }
// }
