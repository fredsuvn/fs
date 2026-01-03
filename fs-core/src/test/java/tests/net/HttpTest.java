package tests.net;

import internal.test.DataTest;
import internal.test.ErrorCharset;
import internal.test.J17Also;
import internal.test.PrintTest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.net.NetException;
import space.sunqian.fs.net.http.HttpCaller;
import space.sunqian.fs.net.http.HttpKit;
import space.sunqian.fs.net.http.HttpNetException;
import space.sunqian.fs.net.http.HttpReq;
import space.sunqian.fs.net.http.HttpResp;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Also
public class HttpTest implements PrintTest, DataTest {

    private static final String TEST_URL = "https://www.baidu.com/s";

    private static Server httpServer;

    @BeforeAll
    public static void startHttpServer() throws Exception {
        httpServer = new Server(0);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        httpServer.setHandler(context);
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                String msg = IOKit.availableString(req.getInputStream());
                Map<String, List<String>> headers = new HashMap<>();
                Enumeration<String> namesIt = req.getHeaderNames();
                while (namesIt.hasMoreElements()) {
                    String headerName = namesIt.nextElement();
                    Enumeration<String> values = req.getHeaders(headerName);
                    List<String> list = new ArrayList<>();
                    while (values.hasMoreElements()) {
                        list.add(values.nextElement());
                    }
                    headers.put(headerName, list);
                }
                assertTrue(headers.containsKey("X-HEADER"));
                assertEquals(headers.get("X-HEADER"), ListKit.list("hello!"));
                assertTrue(headers.containsKey("X-HEADER2"));
                assertEquals(headers.get("X-HEADER2"), ListKit.list("hello2-1!", "hello2-2!"));
                String respBody = "no-body".equals(msg) ? null : "hello, world2!";
                if ("no-code".equals(msg)) {
                    resp.setStatus(999, "Unknown");
                    // resp.setStatus(999);
                }
                if ("no-content".equals(msg)) {
                    resp.setContentType(null);
                } else {
                    resp.setContentType("text/html;charset=UTF-8");
                }
                resp.setHeader("X-HEADER", "hello2!");
                if (respBody != null) {
                    resp.getWriter().write(respBody);
                }
            }
        }), "/*");
        httpServer.start();
    }

    @AfterAll
    public static void stopServer() throws Exception {
        httpServer.stop();
    }

    @Test
    public void testRequest() throws Exception {
        {
            // no write body
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .timeout(Duration.ofSeconds(5))
                .build();
            HttpResp resp = HttpKit.request(req);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // write body
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body("hello, world!")
                .timeout(Duration.ofSeconds(5))
                .build();
            HttpResp resp = HttpKit.request(req, null);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // write empty body
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body("")
                .timeout(Duration.ofSeconds(5))
                .build();
            HttpResp resp = HttpKit.request(req, Proxy.NO_PROXY);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // no-body
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body("no-body")
                .build();
            HttpResp resp = HttpKit.request(req);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            assertNull(resp.bodyString());
        }
        {
            // no-code
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .body("no-code")
                .build();
            HttpResp resp = HttpKit.request(req);
            assertEquals("999", resp.statusCode());
            assertEquals("Unknown", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // with io buffer size
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .timeout(Duration.ofSeconds(5))
                .build();
            HttpResp resp = HttpCaller.newBuilder().bufSize(1024).build().request(req);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // with proxy
            Proxy proxy = new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress(httpServer.getURI().getHost(), httpServer.getURI().getPort())
            );
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("GET")
                .header("X-HEADER", "hello!")
                .header("X-HEADER2", "hello2-1!")
                .header("X-HEADER2", "hello2-2!")
                .timeout(Duration.ofSeconds(5))
                .build();
            HttpResp resp = HttpKit.request(req, proxy);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // error
            assertThrows(IllegalArgumentException.class, () -> HttpReq.newBuilder().build());
        }
    }

    private void checkRespHeaders(HttpResp resp) {
        assertEquals("HTTP/1.1", resp.protocolVersion());
        String contentType = resp.contentType();
        assertNotNull(contentType);
        assertEquals("text/html;charset=UTF-8".toLowerCase(), contentType.toLowerCase());
        assertEquals(CharsKit.UTF_8, resp.bodyCharset());
        assertEquals(resp.headers().get("X-HEADER"), Collections.singletonList("hello2!"));
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

    @Test
    public void testContentType() throws Exception {
        assertNull(HttpKit.contentCharset("no-content"));
        // no-content
        HttpReq req = HttpReq.newBuilder()
            .url("http://localhost:" + httpServer.getURI().getPort())
            .method("GET")
            .headers(MapKit.map(
                "X-HEADER", Collections.singletonList("hello!"),
                "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
            ))
            .body("no-content")
            .build();
        HttpResp resp = HttpKit.request(req);
        assertNull(resp.contentType());
        assertNull(resp.bodyCharset());
    }

    @Test
    public void testRequestBody() throws Exception {
        String text = "hello, world!";
        byte[] bytes = text.getBytes(CharsKit.defaultCharset());
        {
            // input stream
            ByteArrayInputStream data = new ByteArrayInputStream(bytes);
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("POST")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body(data)
                .build();
            HttpReq.Body body = req.body();
            assertNotNull(body);
            assertEquals(HttpReq.Body.Type.INPUT_STREAM, body.type());
            assertSame(data, body.toInputStream());
            assertEquals(text, body.toText());
            data.reset();
            assertArrayEquals(bytes, body.toByteArray());
            data.reset();
            assertEquals(ByteBuffer.wrap(bytes), body.toByteBuffer());
            data.reset();
            HttpResp resp = HttpKit.request(req, null);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // text
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("POST")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body(text)
                .build();
            HttpReq.Body body = req.body();
            assertNotNull(body);
            assertEquals(HttpReq.Body.Type.TEXT, body.type());
            assertArrayEquals(bytes, IOKit.read(body.toInputStream()));
            assertSame(text, body.toText());
            assertArrayEquals(bytes, body.toByteArray());
            assertEquals(ByteBuffer.wrap(bytes), body.toByteBuffer());
            HttpResp resp = HttpKit.request(req, null);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // byte array
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("POST")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body(bytes)
                .build();
            HttpReq.Body body = req.body();
            assertNotNull(body);
            assertEquals(HttpReq.Body.Type.BYTE_ARRAY, body.type());
            assertArrayEquals(bytes, IOKit.read(body.toInputStream()));
            assertEquals(text, body.toText());
            assertSame(bytes, body.toByteArray());
            assertEquals(ByteBuffer.wrap(bytes), body.toByteBuffer());
            HttpResp resp = HttpKit.request(req, null);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
        {
            // byte buffer
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            HttpReq req = HttpReq.newBuilder()
                .url("http://localhost:" + httpServer.getURI().getPort())
                .method("POST")
                .headers(MapKit.map(
                    "X-HEADER", Collections.singletonList("hello!"),
                    "X-HEADER2", ListKit.list("hello2-1!", "hello2-2!")
                ))
                .body(buffer)
                .build();
            HttpReq.Body body = req.body();
            assertNotNull(body);
            assertEquals(HttpReq.Body.Type.BYTE_BUFFER, body.type());
            buffer.mark();
            assertArrayEquals(bytes, IOKit.read(body.toInputStream()));
            buffer.reset();
            assertEquals(text, body.toText());
            buffer.reset();
            assertArrayEquals(bytes, body.toByteArray());
            buffer.reset();
            assertSame(buffer, body.toByteBuffer());
            HttpResp resp = HttpKit.request(req, null);
            assertEquals("200", resp.statusCode());
            assertEquals("OK", resp.statusText());
            checkRespHeaders(resp);
            String bodyString = resp.bodyString();
            assertEquals("hello, world2!", bodyString);
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // HttpNetException
            assertThrows(HttpNetException.class, () -> {
                throw new HttpNetException();
            });
            assertThrows(HttpNetException.class, () -> {
                throw new HttpNetException("");
            });
            assertThrows(HttpNetException.class, () -> {
                throw new HttpNetException("", new RuntimeException());
            });
            assertThrows(HttpNetException.class, () -> {
                throw new HttpNetException(new RuntimeException());
            });
        }
    }
}
