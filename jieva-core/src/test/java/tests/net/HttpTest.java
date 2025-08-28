package tests.net;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.http.HttpKit;
import xyz.sunqian.common.net.http.HttpReq;
import xyz.sunqian.common.net.http.HttpResp;
import xyz.sunqian.common.net.tcp.TcpServer;
import xyz.sunqian.common.net.tcp.TcpServerHandler;
import xyz.sunqian.test.ErrorCharset;
import xyz.sunqian.test.PrintTest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class HttpTest implements PrintTest {

    private static final String TEST_URL = "https://www.baidu.com/s";

    @Test(timeOut = 100000)
    public void testRequest() throws Exception {
        CountDownLatch readLatch = new CountDownLatch(1);
        TcpServer httpServer = TcpServer.newBuilder()
            .handler(new HttpHandler(readLatch))
            .bind();
        {
            // post
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.localAddress().getPort())
                .method("GET")
                //.headers(MapKit.map("Accept", "text/html"))
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .body("hello, world!")
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals(resp.statusCode(), "200");
            assertEquals(resp.statusText(), "OK");
            assertEquals(resp.protocolVersion(), "HTTP/1.1");
            assertEquals(resp.contentType(), "text/html;charset=UTF-8");
            assertEquals(resp.bodyCharset(), CharsKit.UTF_8);
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals(bodyString, "hello, world2!");
        }
        {
            // get
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.localAddress().getPort())
                .method("GET")
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals(resp.statusCode(), "200");
            assertEquals(resp.statusText(), "OK");
            assertEquals(resp.protocolVersion(), "HTTP/1.1");
            assertEquals(resp.contentType(), "text/html;charset=UTF-8");
            assertEquals(resp.bodyCharset(), CharsKit.UTF_8);
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals(bodyString, "hello, world2!");
        }
        {
            // get
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.localAddress().getPort())
                .method("GET")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals(resp.statusCode(), "200");
            assertEquals(resp.statusText(), "OK");
            assertEquals(resp.protocolVersion(), "HTTP/1.1");
            assertEquals(resp.contentType(), "text/html;charset=UTF-8");
            assertEquals(resp.bodyCharset(), CharsKit.UTF_8);
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals(bodyString, "hello, world2!");
        }
        httpServer.close();
        expectThrows(IllegalArgumentException.class, () -> HttpReq.newBuilder().build());
    }

    @Test
    public void testContentType() throws Exception {
        assertEquals(HttpKit.contentCharset("text/html;charset=UTF-8;some=haha"), CharsKit.UTF_8);
        assertNull(HttpKit.contentCharset("text/html;"));
        assertNull(HttpKit.contentCharset("text/html"));
        CountDownLatch readLatch = new CountDownLatch(1);
        TcpServer httpServer = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {

                @Override
                public void channelOpen(@Nonnull Context context) throws Exception {
                }

                @Override
                public void channelClose(@Nonnull Context context) throws Exception {
                }

                @Override
                public void channelRead(@Nonnull Context context) throws Exception {
                    context.writeString("HTTP/1.1 200 OK\r\n" +
                        // "Content-Type: text/html;charset=UTF-8\r\n" +
                        //"Content-Length: " + respBody.length() + "\r\n" +
                        "X-HEADER: hello2!\r\n" +
                        "\r\n");
                    readLatch.countDown();
                    context.close();
                }

                @Override
                public void exceptionCaught(@Nullable Context context, @Nonnull Throwable cause) {
                }
            })
            .bind();
        HttpResp resp = HttpKit.request(HttpReq.newBuilder()
            .url("http://localhost:" + httpServer.localAddress().getPort())
            .method("GET")
            .headers(MapKit.map(
                "X-HEADER", Collections.singletonList("hello!"),
                "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
            ))
            .build());
        assertNull(resp.bodyCharset());
        readLatch.await();
        httpServer.close();
    }

    @Test
    public void testUrl() throws Exception {
        String url = TEST_URL + "?" +
            URLEncoder.encode("wd", "UTF-8") + "=" +
            URLEncoder.encode("hello, 中文", "UTF-8").replace("+", "%20") +
            "&" +
            URLEncoder.encode("pn", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8") +
            "&" +
            URLEncoder.encode("space", "UTF-8") + "=%20"
            + "&";
        assertEquals(
            HttpKit.buildUrl(
                TEST_URL,
                MapKit.map("wd", "hello, 中文", "pn", "0", "space", " ")
            ).toString(),
            url
        );
        printFor("url", url);
        assertEquals(
            HttpKit.encodeUrl("   "),
            "%20%20%20"
        );
        assertEquals(
            HttpKit.buildUrl(TEST_URL, MapKit.map()).toString(),
            TEST_URL
        );
        expectThrows(NetException.class, () -> HttpKit.encodeUrl("abc", ErrorCharset.SINGLETON));
    }

    private static final class HttpHandler implements TcpServerHandler, PrintTest {

        private final CountDownLatch readLatch;

        private HttpHandler(CountDownLatch readLatch) {
            this.readLatch = readLatch;
        }

        @Override
        public void channelOpen(@Nonnull Context context) throws Exception {
            printFor("http open", context.clientAddress());
        }

        @Override
        public void channelClose(@Nonnull Context context) throws Exception {
            printFor("http close", context.clientAddress());
        }

        @Override
        public void channelRead(@Nonnull Context context) throws Exception {
            printFor("http read", context.clientAddress());
            String msg = context.availableString();
            printFor("http read msg", msg);
            Scanner scanner = new Scanner(msg);
            Map<String, List<String>> headers = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int index = line.indexOf(':');
                if (index < 0) {
                    continue;
                }
                String[] kv = line.split(":");
                headers.compute(kv[0], (k, l) -> {
                    if (l == null) {
                        l = new ArrayList<>();
                    }
                    l.add(kv[1].trim());
                    return l;
                });
            }
            assertTrue(headers.containsKey("X-HEADER"));
            assertEquals(headers.get("X-HEADER"), ListKit.list("hello!"));
            assertTrue(headers.containsKey("X-HEADER2"));
            assertEquals(headers.get("X-HEADER2"), ListKit.list("hello2-1!", "hello2-2!"));
            String respBody = "hello, world2!";
            context.writeString("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=UTF-8\r\n" +
                //"Content-Length: " + respBody.length() + "\r\n" +
                "X-HEADER: hello2!\r\n" +
                "\r\n" +
                respBody);
            readLatch.countDown();
            context.close();
        }

        @Override
        public void exceptionCaught(@Nullable Context context, @Nonnull Throwable cause) {
            System.out.println(cause);
        }
    }
}
