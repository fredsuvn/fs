package xyz.sunqian.common.test;

import xyz.sunqian.common.net.tcp.TcpClient;

import java.net.InetSocketAddress;

public class TestClient {

    public static void main(String[] args) throws Exception {
        InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 7082);
        while (true) {
            for (int i = 0; i < 50; i++) {
                TcpClient client = TcpClient.newBuilder()
                    .connect(serverAddress);
            }
            Thread.sleep(1000L);
        }
    }
}
