package internal.tests.benchmarks;

import internal.test.DataTest;
import internal.tests.common.TcpServerApi;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.common.net.tcp.TcpClient;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode({Mode.Throughput})
// @Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
// @Fork(1)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class TcpServerBenchmark implements DataTest {

    @Param({
        "fs",
        "netty",
    })
    private String serverType;

    private TcpServerApi server;
    private TcpClient[] clients;

    private final byte[] message = randomBytes(16);

    @Setup(Level.Trial)
    public void setup() {
        this.server = TcpServerApi.createServer(serverType);
        this.clients = new TcpClient[30];
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = TcpClient.newBuilder().connect(server.address());
            clients[i] = client;
        }
    }

    @TearDown(Level.Trial)
    public void stopServer() throws InterruptedException {
        if (clients != null) {
            for (TcpClient client : clients) {
                client.close();
            }
        }
        if (server != null) {
            server.shutdown();
        }
    }

    @Benchmark
    public void request(Blackhole blackhole) throws Exception {
        Random random = ThreadLocalRandom.current();
        int clientIndex = random.nextInt(clients.length);
        TcpClient client = clients[clientIndex];
        client.writeBytes(message);
        client.awaitReadable();
        byte[] ret = client.availableBytes();
        blackhole.consume(ret);
    }
}
