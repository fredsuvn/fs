package tests.net;

import internal.test.ErrorCharset;
import internal.test.J17Also;
import internal.test.PrintTest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.collect.MapKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.NetException;
import space.sunqian.common.net.http.HttpKit;
import space.sunqian.common.net.http.HttpReq;
import space.sunqian.common.net.http.HttpResp;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Also
public class HttpTest implements PrintTest {

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
            // error proxy
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
        assertThrows(IllegalArgumentException.class, () -> HttpReq.newBuilder().build());
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
}
