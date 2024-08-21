package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.GekLog;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.data.GekData;
import xyz.fslabo.common.io.JieIO;
import xyz.fslabo.common.net.GekNetException;
import xyz.fslabo.common.net.GekNetServerException;
import xyz.fslabo.common.net.http.GekHttp;
import xyz.fslabo.common.net.http.GekHttpHeaders;
import xyz.fslabo.common.net.http.GekHttpResponse;
import xyz.fslabo.common.net.tcp.*;
import xyz.fslabo.common.net.tcp.handlers.DelimiterBasedTcpChannelHandler;
import xyz.fslabo.common.net.tcp.handlers.LengthBasedTcpChannelHandler;
import xyz.fslabo.common.net.udp.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NetTest {

    @Test
    public void testTcp() {
        testTcpLengthBased(6, 5, 10);
        testTcpLengthBased(1024, 8, 20);
        testTcpLengthBased(22, 8, 50);
        testTcpDelimiterBased(6, 5, 10);
        testTcpDelimiterBased(1024, 8, 20);
        testTcpDelimiterBased(22, 8, 50);
    }

    @Test
    public void testUdp() {
        testUdp0(60, 5, 10);
        testUdp0(1024, 8, 20);
        testUdp0(22, 8, 50);
    }

    @Test
    public void testHttp() throws Exception {
        testHttp0();
    }

    private void testTcpLengthBased(int bufferSize, int serverThreads, int clientThreads) {
        Map<String, AtomicInteger> data = new ConcurrentHashMap<>();

        //server: hlo
        //client: hlo
        //client: abc * 10
        //server: qwe * 10
        //client: bye
        //server bye

        GekTcpServer server = GekTcpServer.newBuilder()
            .channelBufferSize(bufferSize)
            .executor(Executors.newFixedThreadPool(serverThreads))
            .serverHandler(new GekTcpServerHandler() {
                @Override
                public void onException(GekNetServerException exception) {
                    GekLog.getInstance().info("server.onException: ", exception);
                }

                @Override
                public void onOpen(GekTcpChannel channel) {
                    TestUtil.count("server-onOpen", data);
                    channel.sendAndFlush(GekData.wrap(buildServerData("hlo")));
                    TestUtil.count("hlo", data);
                }

                @Override
                public void onClose(GekTcpChannel channel, ByteBuffer buffer) {
                    TestUtil.count("server-onClose", data);
                }

                @Override
                public void onException(GekTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("server-channel.onException", data);
                    GekLog.getInstance().info("server-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new LengthBasedTcpChannelHandler(3))
            .addChannelHandler(new GekTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(GekTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = JieIO.readString(buffer);
                        TestUtil.count(str, data);
                        System.out.println("TCP receive (" + channel.getRemoteSocketAddress() + "): " + str);
                        switch (str) {
                            case "abc": {
                                channel.sendAndFlush(GekData.wrap(buildServerData("qwe")));
                                break;
                            }
                            case "bye": {
                                channel.sendAndFlush(GekData.wrap(buildServerData("bye")));
                                //channel.flush();
                                channel.closeNow();
                                break;
                            }
                        }
                    }
                    //channel.flush();
                    return null;
                }
            })
            .build();
        server.start(false);
        server.closeNow();
        server = server.toBuilder().executor(Executors.newFixedThreadPool(serverThreads)).build();
        CountDownLatch latch = new CountDownLatch(clientThreads);
        GekTcpClient client = GekTcpClient.newBuilder()
            .channelBufferSize(bufferSize)
            .clientHandler(new GekTcpClientHandler() {
                @Override
                public void onOpen(GekTcpChannel channel) {
                    TestUtil.count("client-onOpen", data);
                }

                @Override
                public void onClose(GekTcpChannel channel, ByteBuffer buffer) {
                    TestUtil.count("client-onClose", data);
                }

                @Override
                public void onException(GekTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("client-channel.onException", data);
                    GekLog.getInstance().info("client-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new LengthBasedTcpChannelHandler(1, 2))
            .addChannelHandler(new GekTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(GekTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = parseServerData(buffer);
                        TestUtil.count(str, data);
                        switch (str) {
                            case "hlo": {
                                new Thread(() -> {
                                    channel.sendAndFlush(GekData.wrap("a"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("bc"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("abc"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("ab"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("ca"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("bca"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("bc"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("abcabcabcabcabc"));
                                    Jie.sleep(500);
                                    channel.sendAndFlush(GekData.wrap("bye"));
                                }).start();
                                break;
                            }
                            case "bye": {
                                //channel.flush();
                                channel.closeNow();
                                break;
                            }
                        }
                    }
                    channel.flush();
                    return null;
                }
            })
            .build();
        List<GekTcpClient> clients = new LinkedList<>();
        clients.add(client);
        for (int i = 0; i < clientThreads - 1; i++) {
            clients.add(client.toBuilder().build());
        }
        GekTcpServer tcpServer = server;
        tcpServer.start(false);
        for (GekTcpClient c : clients) {
            new Thread(() -> {
                try {
                    c.start("localhost", tcpServer.getPort());
                } catch (Exception e) {
                    System.out.println(e);
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        tcpServer.close();
        //server: hlo
        //client: hlo
        //client: abc * 10
        //server: qwe * 10
        //client: bye
        //server bye
        Assert.assertEquals(data.get("server-onOpen").get(), clientThreads);
        Assert.assertEquals(data.get("client-onOpen").get(), clientThreads);
        Assert.assertEquals(data.get("server-onClose").get(), clientThreads);
        Assert.assertEquals(data.get("client-onClose").get(), clientThreads);
        Assert.assertEquals(data.get("hlo").get(), clientThreads * 2);
        Assert.assertEquals(data.get("bye").get(), clientThreads * 2);
        Assert.assertEquals(data.get("abc").get(), clientThreads * 10);
        Assert.assertEquals(data.get("qwe").get(), clientThreads * 10);
    }

    private byte[] buildServerData(String data) {
        byte[] dataBytes = data.getBytes(JieChars.defaultCharset());
        byte[] bytes = new byte[6];
        bytes[0] = 0;
        bytes[1] = 0;
        bytes[2] = 6;
        bytes[3] = dataBytes[0];
        bytes[4] = dataBytes[1];
        bytes[5] = dataBytes[2];
        return bytes;
    }

    private String parseServerData(ByteBuffer buffer) {
        byte[] bytes = JieIO.read(buffer);
        if (bytes.length != 6) {
            throw new GekNetException("bytes.length != 6");
        }
        return new String(bytes, 3, 3, JieChars.defaultCharset());
    }

    private void testTcpDelimiterBased(int bufferSize, int serverThreads, int clientThreads) {
        Map<String, AtomicInteger> data = new ConcurrentHashMap<>();

        //server: hlo
        //client: hlo
        //client: abc * 10
        //server: qwe * 10
        //client: bye
        //server bye

        GekTcpServer server = GekTcpServer.newBuilder()
            .channelBufferSize(bufferSize)
            .executor(Executors.newFixedThreadPool(serverThreads))
            .serverHandler(new GekTcpServerHandler() {
                @Override
                public void onException(GekNetServerException exception) {
                    GekLog.getInstance().info("server.onException: ", exception);
                }

                @Override
                public void onOpen(GekTcpChannel channel) {
                    TestUtil.count("server-onOpen", data);
                    channel.sendAndFlush(GekData.wrap("hlo|"));
                    TestUtil.count("hlo", data);
                }

                @Override
                public void onClose(GekTcpChannel channel, ByteBuffer buffer) {
                    TestUtil.count("server-onClose", data);
                }

                @Override
                public void onException(GekTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("server-channel.onException", data);
                    GekLog.getInstance().info("server-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new DelimiterBasedTcpChannelHandler((byte) '|'))
            .addChannelHandler(new GekTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(GekTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = JieIO.readString(buffer);
                        TestUtil.count(str, data);
                        System.out.println("TCP receive (" + channel.getRemoteSocketAddress() + "): " + str);
                        switch (str) {
                            case "abc": {
                                channel.sendAndFlush(GekData.wrap("qwe|"));
                                break;
                            }
                            case "bye": {
                                channel.sendAndFlush(GekData.wrap("bye|"));
                                //channel.flush();
                                channel.closeNow();
                                break;
                            }
                        }
                    }
                    //channel.flush();
                    return null;
                }
            })
            .build();
        server.start(false);
        server.closeNow();
        server = server.toBuilder().executor(Executors.newFixedThreadPool(serverThreads)).build();
        CountDownLatch latch = new CountDownLatch(clientThreads);
        GekTcpClient client = GekTcpClient.newBuilder()
            .channelBufferSize(bufferSize)
            .clientHandler(new GekTcpClientHandler() {
                @Override
                public void onOpen(GekTcpChannel channel) {
                    TestUtil.count("client-onOpen", data);
                }

                @Override
                public void onClose(GekTcpChannel channel, ByteBuffer buffer) {
                    TestUtil.count("client-onClose", data);
                }

                @Override
                public void onException(GekTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("client-channel.onException", data);
                    GekLog.getInstance().info("client-channel.onException: ", throwable);
                }
            })
            .addChannelHandler(new DelimiterBasedTcpChannelHandler((byte) '|'))
            .addChannelHandler(new GekTcpChannelHandler<List<ByteBuffer>>() {
                @Override
                public @Nullable Object onMessage(GekTcpChannel channel, List<ByteBuffer> message) {
                    for (ByteBuffer buffer : message) {
                        String str = JieIO.readString(buffer);
                        TestUtil.count(str, data);
                        switch (str) {
                            case "hlo": {
                                new Thread(() -> {
                                    channel.sendAndFlush(GekData.wrap("a"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("bc|"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("abc|"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("ab"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("c|a"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("bc|a"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("bc"));
                                    Jie.sleep(200);
                                    channel.sendAndFlush(GekData.wrap("|abc|abc|abc|abc|abc"));
                                    Jie.sleep(500);
                                    channel.sendAndFlush(GekData.wrap("|bye|"));
                                }).start();
                                break;
                            }
                            case "bye": {
                                //channel.flush();
                                channel.closeNow();
                                break;
                            }
                        }
                    }
                    channel.flush();
                    return null;
                }
            })
            .build();
        List<GekTcpClient> clients = new LinkedList<>();
        clients.add(client);
        for (int i = 0; i < clientThreads - 1; i++) {
            clients.add(client.toBuilder().build());
        }
        GekTcpServer tcpServer = server;
        tcpServer.start(false);
        for (GekTcpClient c : clients) {
            new Thread(() -> {
                try {
                    c.start("localhost", tcpServer.getPort());
                } catch (Exception e) {
                    System.out.println(e);
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        tcpServer.close();
        //server: hlo
        //client: hlo
        //client: abc * 10
        //server: qwe * 10
        //client: bye
        //server bye
        Assert.assertEquals(data.get("server-onOpen").get(), clientThreads);
        Assert.assertEquals(data.get("client-onOpen").get(), clientThreads);
        Assert.assertEquals(data.get("server-onClose").get(), clientThreads);
        Assert.assertEquals(data.get("client-onClose").get(), clientThreads);
        Assert.assertEquals(data.get("hlo").get(), clientThreads * 2);
        Assert.assertEquals(data.get("bye").get(), clientThreads * 2);
        Assert.assertEquals(data.get("abc").get(), clientThreads * 10);
        Assert.assertEquals(data.get("qwe").get(), clientThreads * 10);
    }

    private void testUdp0(int bufferSize, int serverThreads, int clientThreads) {
        Map<String, AtomicInteger> data = new ConcurrentHashMap<>();

        //client: udp-client * 10

        GekUdpServer server = GekUdpServer.newBuilder()
            .executor(Executors.newFixedThreadPool(serverThreads))
            .serverHandler(new GekUdpServerHandler() {
                @Override
                public void onException(GekNetServerException exception) {
                    GekLog.getInstance().info("server.onException: ", exception);
                }

                @Override
                public void onException(GekUdpHeader header, GekUdpClient client, Throwable throwable, ByteBuffer buffer) {
                    TestUtil.count("server-header.onException", data);
                    GekLog.getInstance().info("server-header.onException: ", throwable);
                }
            })
            .addPacketHandler(new GekUdpPacketHandler<ByteBuffer>() {
                @Override
                public @Nullable Object onPacket(GekUdpHeader header, GekUdpClient client, ByteBuffer buffer) {
                    String str = JieIO.readString(buffer);
                    TestUtil.count(str, data);
                    System.out.println("UDP receive (" + header.getInetSocketAddress() + "): " + str);
                    return null;
                }
            })
            .build();
        server.start(false);
        server.closeNow();
        server = server.toBuilder().executor(Executors.newFixedThreadPool(serverThreads)).build();
        CountDownLatch latch = new CountDownLatch(clientThreads);
        GekUdpClient client = GekUdpClient.newBuilder().build();
        List<GekUdpClient> clients = new LinkedList<>();
        clients.add(client);
        for (int i = 0; i < clientThreads - 1; i++) {
            clients.add(client.toBuilder().build());
        }
        GekUdpServer udpServer = server;
        udpServer.start(false);
        for (GekUdpClient c : clients) {
            new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        c.send(GekUdpPacket.of(
                            ByteBuffer.wrap("udp-client".getBytes(JieChars.defaultCharset())),
                            new InetSocketAddress("localhost", udpServer.getPort()))
                        );
                        Jie.sleep(50);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        udpServer.close();
        //client: udp-client * 10
        Assert.assertEquals(data.get("udp-client").get(), clientThreads * 10);
    }

    private void testHttp0() throws Exception {
        ByteArrayOutputStream received = new ByteArrayOutputStream();
        GekTcpServer server = GekTcpServer.newBuilder()
            .executor(Executors.newFixedThreadPool(5))
            .addChannelHandler(new GekTcpChannelHandler<ByteBuffer>() {
                @Override
                public @Nullable Object onMessage(GekTcpChannel channel, ByteBuffer message) {
                    JieIO.readTo(JieIO.toInputStream(message), received);
                    String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: 9\r\n" +
                        "Content-Length2: 9\r\n" +
                        "Content-Length2: 9\r\n" +
                        "Content-Length2: 9\r\n" +
                        "\r\n" +
                        "Fuck Off!";
                    channel.sendAndFlush(response.getBytes(StandardCharsets.ISO_8859_1));
                    channel.close();
                    return null;
                }
            })
            .build();
        server.start(false);
        GekHttpResponse response1 = GekHttp.get(
            "http://localhost:" + server.getPort(),
            GekHttpHeaders.of(
                "Connection", "keep-alive"
            )
        );
        System.out.println("response1 Server received:");
        System.out.println(new String(received.toByteArray(), JieChars.defaultCharset()));
        System.out.println("response1 Client response header:");
        GekHttpHeaders headers = response1.getHeaders();
        System.out.println(headers);
        InputStream body = response1.getBody();
        System.out.println("response1 Client response body:");
        String bodyString = JieIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");
        GekHttpResponse response2 = GekHttp.get(
            "http://localhost:" + server.getPort(),
            JieColl.toMap("hello", "hello world")
        );
        System.out.println("response2 Server received:");
        System.out.println(new String(received.toByteArray(), JieChars.defaultCharset()));
        System.out.println("response2 Client response header:");
        headers = response2.getHeaders();
        System.out.println(headers);
        body = response2.getBody();
        System.out.println("response2 Client response body:");
        bodyString = JieIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");
        GekHttpResponse response3 = GekHttp.post(
            "http://localhost:" + server.getPort(),
            new ByteArrayInputStream(JieString.encode("request3"))
        );
        System.out.println("response3 Server received:");
        System.out.println(new String(received.toByteArray(), JieChars.defaultCharset()));
        System.out.println("response3 Client response header:");
        headers = response3.getHeaders();
        System.out.println(headers);
        body = response3.getBody();
        System.out.println("response3 Client response body:");
        bodyString = JieIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");
        GekHttpResponse response4 = GekHttp.post(
            "http://localhost:" + server.getPort(),
            ByteBuffer.wrap(JieString.encode("request4"))
        );
        System.out.println("response4 Server received:");
        System.out.println(new String(received.toByteArray(), JieChars.defaultCharset()));
        System.out.println("response4 Client response header:");
        headers = response4.getHeaders();
        System.out.println(headers);
        body = response4.getBody();
        System.out.println("response4 Client response body:");
        bodyString = JieIO.readString(body);
        System.out.println(bodyString);
        body.close();
        Assert.assertEquals(headers.getHeaderFirst("Content-Length"), "9");
        Assert.assertEquals(headers.getHeader("Content-Length2"), Arrays.asList("9", "9", "9"));
        Assert.assertEquals(bodyString, "Fuck Off!");
        received.reset();
        System.out.println("----------------------------------");

        server.close();
    }
}
