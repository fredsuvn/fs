package internal.benchmark;

import internal.api.TcpServerApi;
import internal.utils.DataGen;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.fs.net.tcp.TcpClient;

public class TcpServerJmh extends AbstractJmhBenchmark implements DataGen {

    private final byte[] message = randomBytes(16);
    @Param({
        "fs",
        "netty",
    })
    private String serverType;
    private TcpServerApi serverApi;
    private TcpClient[] clients;

    @Setup(Level.Trial)
    public void setup() {
        this.serverApi = TcpServerApi.createApi(serverType);
        this.clients = new TcpClient[30];
        for (int i = 0; i < clients.length; i++) {
            TcpClient client = TcpClient.newBuilder().connect(serverApi.address());
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
        if (serverApi != null) {
            serverApi.shutdown();
        }
    }

    @Benchmark
    public void request(Blackhole blackhole) throws Exception {
        for (TcpClient client : clients) {
            client.writeBytes(message);
            // client.awaitReadable();
            // byte[] ret = client.availableBytes();
            // blackhole.consume(ret);
        }
        for (TcpClient client : clients) {
            byte[] ret = client.availableBytes();
            blackhole.consume(ret);
        }
    }
}
