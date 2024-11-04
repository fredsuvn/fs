// package benchmark;
//
// import com.google.common.base.CaseFormat;
// import org.openjdk.jmh.annotations.*;
// import org.openjdk.jmh.infra.Blackhole;
// import xyz.sunqian.common.base.CaseFormatter;
//
// import java.util.concurrent.TimeUnit;
//
// @BenchmarkMode(Mode.Throughput)
// @Warmup(iterations = 3, time = 3)
// @Measurement(iterations = 3, time = 3)
// @Fork(1)
// @State(value = Scope.Benchmark)
// @OutputTimeUnit(TimeUnit.MILLISECONDS)
// public class CaseJmh {
//
//     private static final String TEST_CAMEL = "getSimpleNameOptionsBuilder";
//     private static final String TEST_UNDERSCORE = "get_Simple_Name_Options_Builder";
//     private static final String TEST_HYPHEN = "get-Simple-Name-Options-Builder";
//
//     private CaseFormatter fsUpperCamel = CaseFormatter.UPPER_CAMEL;
//     private CaseFormatter fsLowerCamel = CaseFormatter.LOWER_CAMEL;
//     private CaseFormatter fsUpperUnderscore = CaseFormatter.UPPER_UNDERSCORE;
//     private CaseFormatter fsLowerUnderscore = CaseFormatter.LOWER_UNDERSCORE;
//     private CaseFormatter fsHyphen = CaseFormatter.HYPHEN;
//     private CaseFormatter fsUnderscore = CaseFormatter.UNDERSCORE;
//     private CaseFormat guavaUpperCamel = CaseFormat.UPPER_CAMEL;
//     private CaseFormat guavaLowerCamel = CaseFormat.LOWER_CAMEL;
//     private CaseFormat guavaUpperUnderscore = CaseFormat.UPPER_UNDERSCORE;
//     private CaseFormat guavaLowerUnderscore = CaseFormat.LOWER_UNDERSCORE;
//     private CaseFormat guavaLowerHyphen = CaseFormat.LOWER_HYPHEN;
//
//     @Setup(Level.Iteration)
//     public void init() {
//     }
//
//     @Benchmark
//     public void fsCamel(Blackhole bh) {
//         bh.consume(fsUpperCamel.to(fsLowerCamel, TEST_CAMEL));
//         bh.consume(fsLowerCamel.to(fsUpperCamel, TEST_CAMEL));
//     }
//
//     @Benchmark
//     public void fsUnderscore(Blackhole bh) {
//         bh.consume(fsUpperUnderscore.to(fsLowerUnderscore, TEST_UNDERSCORE));
//         bh.consume(fsLowerUnderscore.to(fsUpperUnderscore, TEST_UNDERSCORE));
//     }
//
//     @Benchmark
//     public void fsMix(Blackhole bh) {
//         bh.consume(fsUpperCamel.to(fsLowerUnderscore, TEST_CAMEL));
//         bh.consume(fsUpperUnderscore.to(fsLowerCamel, TEST_UNDERSCORE));
//     }
//
//     @Benchmark
//     public void fsMix2(Blackhole bh) {
//         bh.consume(fsHyphen.to(fsUnderscore, TEST_HYPHEN));
//         bh.consume(fsUnderscore.to(fsHyphen, TEST_UNDERSCORE));
//     }
//
//     @Benchmark
//     public void guavaCamel(Blackhole bh) {
//         bh.consume(guavaUpperCamel.to(guavaLowerCamel, TEST_CAMEL));
//         bh.consume(guavaLowerCamel.to(guavaUpperCamel, TEST_CAMEL));
//     }
//
//     @Benchmark
//     public void guavaUnderscore(Blackhole bh) {
//         bh.consume(guavaUpperUnderscore.to(guavaLowerUnderscore, TEST_UNDERSCORE));
//         bh.consume(guavaLowerUnderscore.to(guavaUpperUnderscore, TEST_UNDERSCORE));
//     }
//
//     @Benchmark
//     public void guavaMix(Blackhole bh) {
//         bh.consume(guavaUpperCamel.to(guavaLowerUnderscore, TEST_CAMEL));
//         bh.consume(guavaUpperUnderscore.to(guavaLowerCamel, TEST_UNDERSCORE));
//     }
//
//     @Benchmark
//     public void guavaMix2(Blackhole bh) {
//         bh.consume(guavaLowerHyphen.to(guavaLowerUnderscore, TEST_HYPHEN));
//         bh.consume(guavaLowerUnderscore.to(guavaLowerHyphen, TEST_UNDERSCORE));
//     }
// }
