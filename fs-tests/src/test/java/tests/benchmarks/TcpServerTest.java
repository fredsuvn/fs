package tests.benchmarks;

import internal.test.DataTest;
import internal.tests.api.TcpServerApi;
import org.junit.jupiter.api.Test;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.tcp.TcpClient;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TcpServerTest implements DataTest {

    @Test
    public void testTcpServer() throws Exception {
        testTcpServer("fs");
        testTcpServer("netty");
    }

    public void testTcpServer(String serverType) throws Exception {
        TcpServerApi server = TcpServerApi.createServer(serverType);
        TcpClient client = TcpClient.newBuilder().connect(server.address());
        byte[] message = randomBytes(10);
        client.writeBytes(message);
        client.awaitReadable();
        byte[] received = IOKit.readBytes(client.channel(), message.length);
        assertArrayEquals(message, received);
        server.shutdown();
        client.close();
    }
}
