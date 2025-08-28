package tests.net;

import org.testng.annotations.Test;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.NetKit;
import xyz.sunqian.test.ErrorCharset;
import xyz.sunqian.test.PrintTest;

import java.net.URLEncoder;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class NetTest implements PrintTest {

    private static final String TEST_URL = "https://www.baidu.com/s";

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
            NetKit.buildUrl(
                TEST_URL,
                MapKit.map("wd", "hello, 中文", "pn", "0", "space", " ")
            ).toString(),
            url
        );
        printFor("url", url);
        assertEquals(
            NetKit.encodeUrl("   "),
            "%20%20%20"
        );
        assertEquals(
            NetKit.buildUrl(TEST_URL, MapKit.map()).toString(),
            TEST_URL
        );
        expectThrows(NetException.class, () -> NetKit.encodeUrl("abc", ErrorCharset.SINGLETON));
    }

    @Test
    public void testException() throws Exception {
        {
            // NetException
            expectThrows(NetException.class, () -> {
                throw new NetException();
            });
            expectThrows(NetException.class, () -> {
                throw new NetException("");
            });
            expectThrows(NetException.class, () -> {
                throw new NetException("", new RuntimeException());
            });
            expectThrows(NetException.class, () -> {
                throw new NetException(new RuntimeException());
            });
        }
    }
}
