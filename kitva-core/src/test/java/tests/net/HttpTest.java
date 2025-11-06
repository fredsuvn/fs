package tests.net;

import internal.test.ErrorCharset;
import internal.test.J17Also;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.collect.MapKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.NetException;
import space.sunqian.common.net.http.HttpKit;
import space.sunqian.common.net.http.HttpReq;
import space.sunqian.common.net.http.HttpResp;
import space.sunqian.common.net.tcp.TcpContext;
import space.sunqian.common.net.tcp.TcpServer;
import space.sunqian.common.net.tcp.TcpServerHandler;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Also
public class HttpTest implements PrintTest {

    private static final String TEST_URL = "https://www.baidu.com/s";

    @Test
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
                .timeout(Duration.ofSeconds(5))
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            assertEquals("HTTP/1.1", resp.protocolVersion());
            assertEquals("text/html;charset=UTF-8", resp.contentType());
            assertEquals(CharsKit.UTF_8, resp.bodyCharset());
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // post no-body
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.localAddress().getPort())
                .method("GET")
                //.headers(MapKit.map("Accept", "text/html"))
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .body("no-body")
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            assertEquals("HTTP/1.1", resp.protocolVersion());
            assertEquals("text/html;charset=UTF-8", resp.contentType());
            assertEquals(CharsKit.UTF_8, resp.bodyCharset());
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            assertNull(resp.bodyString());
        }
        {
            // post no-code
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.localAddress().getPort())
                .method("GET")
                //.headers(MapKit.map("Accept", "text/html"))
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .body("no-code")
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals("999", resp.statusCode());
            assertEquals("Unknown", resp.statusText());
            assertEquals("HTTP/1.1", resp.protocolVersion());
            assertEquals("text/html;charset=UTF-8", resp.contentType());
            assertEquals(CharsKit.UTF_8, resp.bodyCharset());
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
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
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            assertEquals("HTTP/1.1", resp.protocolVersion());
            assertEquals("text/html;charset=UTF-8", resp.contentType());
            assertEquals(CharsKit.UTF_8, resp.bodyCharset());
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
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
                .body(IOKit.emptyInputStream())
                .build();
            HttpResp resp = HttpKit.request(req);
            readLatch.await();
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            assertEquals("HTTP/1.1", resp.protocolVersion());
            assertEquals("text/html;charset=UTF-8", resp.contentType());
            assertEquals(CharsKit.UTF_8, resp.bodyCharset());
            assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        httpServer.close();
        assertThrows(IllegalArgumentException.class, () -> HttpReq.newBuilder().build());
    }

    @Test
    public void testContentType() throws Exception {
        assertEquals(CharsKit.UTF_8, HttpKit.contentCharset("text/html;charset=UTF-8;some=haha"));
        assertNull(HttpKit.contentCharset("text/html;"));
        assertNull(HttpKit.contentCharset("text/html"));
        CountDownLatch readLatch = new CountDownLatch(1);
        TcpServer httpServer = TcpServer.newBuilder()
            .handler(new TcpServerHandler() {

                @Override
                public void channelOpen(@Nonnull TcpContext context) throws Exception {
                }

                @Override
                public void channelClose(@Nonnull TcpContext context) throws Exception {
                }

                @Override
                public void channelRead(@Nonnull TcpContext context) throws Exception {
                    context.writeString("HTTP/1.1 200 OK\r\n" +
                        // "Content-Type: text/html;charset=UTF-8\r\n" +
                        //"Content-Length: " + respBody.length() + "\r\n" +
                        "X-HEADER: hello2!\r\n" +
                        "\r\n");
                    readLatch.countDown();
                    context.close();
                }

                @Override
                public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
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
            "%20%20%20",
            HttpKit.encodeUrl("   ")
        );
        assertEquals(
            TEST_URL,
            HttpKit.buildUrl(TEST_URL, MapKit.map()).toString()
        );
        assertThrows(NetException.class, () -> HttpKit.encodeUrl("abc", ErrorCharset.SINGLETON));
    }

    private static final class HttpHandler implements TcpServerHandler, PrintTest {

        private final CountDownLatch readLatch;

        private HttpHandler(CountDownLatch readLatch) {
            this.readLatch = readLatch;
        }

        @Override
        public void channelOpen(@Nonnull TcpContext context) throws Exception {
            printFor("http open", context.clientAddress());
        }

        @Override
        public void channelClose(@Nonnull TcpContext context) throws Exception {
            printFor("http close", context.clientAddress());
        }

        @Override
        public void channelRead(@Nonnull TcpContext context) throws Exception {
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
            String respBody = msg.endsWith("no-body") ? "" : "hello, world2!";
            String status = msg.endsWith("no-code") ? "999 Unknown" : "200 OK";
            context.writeString("HTTP/1.1 " + status + "\r\n" +
                "Content-Type: text/html;charset=UTF-8\r\n" +
                //"Content-Length: " + respBody.length() + "\r\n" +
                "X-HEADER: hello2!\r\n" +
                "\r\n" +
                respBody);
            readLatch.countDown();
            context.close();
        }

        @Override
        public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
            printFor("exceptionCaught", cause);
        }
    }
}
