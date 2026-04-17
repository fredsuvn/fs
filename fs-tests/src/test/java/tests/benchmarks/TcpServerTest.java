package tests.benchmarks;

import internal.api.TcpServerApi;
import internal.utils.DataTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.net.tcp.TcpClient;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TcpServerTest implements DataTest {

    @Test
    public void testTcpServerWithDifferentImplementations() throws Exception {
        testTcpServerImplementation("fs");
        testTcpServerImplementation("netty");
    }

    private void testTcpServerImplementation(String serverType) throws Exception {
        TcpServerApi server = TcpServerApi.createApi(serverType);
        TcpClient client = TcpClient.newBuilder().connect(server.address());

        try {
            byte[] message = randomBytes(10);
            client.writeBytes(message);
            client.writeBytes(message);
            client.readWait();
            byte[] received = IOKit.readBytes(client.channel(), message.length);
            assertArrayEquals(message, received);
        } finally {
            server.shutdown();
            client.close();
        }
    }
}
