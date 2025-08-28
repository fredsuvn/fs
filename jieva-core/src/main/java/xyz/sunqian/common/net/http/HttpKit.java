package xyz.sunqian.common.net.http;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.NetException;

import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;

/**
 * Http utilities.
 *
 * @author sunqian
 */
public class HttpKit {

    private static final @Nonnull HttpClientEngine HTTP_CLIENT_ENGINE = HttpClientEngine.newEngine(IOKit.bufferSize());

    /**
     * Requests the given http request, returns the response. The timeout for connection and read are both 30 seconds.
     *
     * @param req the given http request
     * @return the response
     * @throws NetException if an error occurs
     */
    public static @Nonnull HttpResp request(@Nonnull HttpReq req) throws NetException {
        return request(
            req,
            Duration.ofSeconds(30),
            Duration.ofSeconds(30),
            Proxy.NO_PROXY
        );
    }

    /**
     * Requests the given http request, returns the response.
     *
     * @param req            the given http request
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param proxy          the proxy, may be {@code null} if no proxy is needed
     * @return the response
     * @throws NetException if an error occurs
     */
    public static @Nonnull HttpResp request(
        @Nonnull HttpReq req,
        @Nonnull Duration connectTimeout,
        @Nonnull Duration readTimeout,
        @Nullable Proxy proxy
    ) throws NetException {
        return HTTP_CLIENT_ENGINE.request(req, connectTimeout, readTimeout, Jie.nonnull(proxy, Proxy.NO_PROXY));
    }

    /**
     * Parses the charset from the given http content type, may be {@code null} if the charset is not specified.
     *
     * @param contentType the given http content type
     * @return the charset parsed from the given http content type, may be {@code null} if the charset is not specified
     */
    public static @Nullable Charset contentCharset(@Nonnull String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            String charsetToken = "charset=";
            int charsetIndex = part.indexOf(charsetToken);
            if (charsetIndex < 0) {
                continue;
            }
            String charsetName = part.substring(charsetIndex + charsetToken.length()).trim();
            return Charset.forName(charsetName);
        }
        return null;
    }

    /**
     * Builds and returns a url with the query string. The query string is encoded using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param baseUrl     the base url
     * @param queryString the map represents the query string
     * @return a url with the query string
     * @throws NetException if an error occurs
     */
    public static @Nonnull URL buildUrl(
        @Nonnull String baseUrl, @Nonnull Map<String, String> queryString
    ) throws NetException {
        return buildUrl(baseUrl, queryString, CharsKit.defaultCharset());
    }

    /**
     * Builds and returns a url with the query string. The query string is encoded using the specified charset.
     *
     * @param baseUrl     the base url
     * @param queryString the map represents the query string
     * @param charset     the specified charset for encoding the query string
     * @return a url with the query string
     * @throws NetException if an error occurs
     */
    public static @Nonnull URL buildUrl(
        @Nonnull String baseUrl, @Nonnull Map<String, String> queryString, @Nonnull Charset charset
    ) throws NetException {
        StringBuilder url = new StringBuilder(baseUrl);
        if (!queryString.isEmpty()) {
            url.append("?");
            queryString.forEach((key, value) -> url.append(
                encodeUrl(key, charset)
            ).append("=").append(
                encodeUrl(value, charset)
            ).append("&"));
        }
        return Jie.uncheck(() -> new URL(url.toString()), NetException::new);
    }

    /**
     * Translates a string into {@code application/x-www-form-urlencoded} format using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param str the {@code String} to be translated
     * @return the translated {@code String}
     * @throws NetException if an error occurs
     */
    public static @Nonnull String encodeUrl(@Nonnull String str) throws NetException {
        return encodeUrl(str, CharsKit.defaultCharset());
    }

    /**
     * Translates a string into {@code application/x-www-form-urlencoded} format using the specific charset.
     *
     * @param str     the {@code String} to be translated
     * @param charset the specified charset
     * @return the translated {@code String}
     * @throws NetException if an error occurs
     */
    public static @Nonnull String encodeUrl(@Nonnull String str, @Nonnull Charset charset) throws NetException {
        try {
            return URLEncoder.encode(str, charset.name()).replace("+", "%20");
        } catch (Exception e) {
            throw new NetException(e);
        }
    }
}
