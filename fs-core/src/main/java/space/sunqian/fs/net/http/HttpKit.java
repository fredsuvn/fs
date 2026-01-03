package space.sunqian.fs.net.http;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;

import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Http utilities. The part of the HTTP request is based on {@link HttpCaller}.
 *
 * @author sunqian
 */
public class HttpKit {

    private static final @Nonnull HttpCaller DEFAULT_CALLER = HttpCaller.newHttpCaller();

    /**
     * Requests the given http request, returns the response.
     *
     * @param req the given http request
     * @return the response
     * @throws HttpNetException if an error occurs
     */
    public static @Nonnull HttpResp request(@Nonnull HttpReq req) throws HttpNetException {
        return DEFAULT_CALLER.request(req);
    }

    /**
     * Requests the given http request, returns the response.
     *
     * @param req   the given http request
     * @param proxy the proxy, may be {@code null} if no proxy is needed
     * @return the response
     * @throws HttpNetException if an error occurs
     */
    public static @Nonnull HttpResp request(@Nonnull HttpReq req, @Nullable Proxy proxy) throws HttpNetException {
        return HttpCaller.newBuilder().proxy(Fs.nonnull(proxy, Proxy.NO_PROXY)).build().request(req);
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
     * @throws HttpNetException if an error occurs
     */
    public static @Nonnull URL buildUrl(
        @Nonnull String baseUrl, @Nonnull Map<String, String> queryString
    ) throws HttpNetException {
        return buildUrl(baseUrl, queryString, CharsKit.defaultCharset());
    }

    /**
     * Builds and returns a url with the query string. The query string is encoded using the specified charset.
     *
     * @param baseUrl     the base url
     * @param queryString the map represents the query string
     * @param charset     the specified charset for encoding the query string
     * @return a url with the query string
     * @throws HttpNetException if an error occurs
     */
    public static @Nonnull URL buildUrl(
        @Nonnull String baseUrl, @Nonnull Map<String, String> queryString, @Nonnull Charset charset
    ) throws HttpNetException {
        StringBuilder url = new StringBuilder(baseUrl);
        if (!queryString.isEmpty()) {
            url.append("?");
            queryString.forEach((key, value) -> url.append(
                encodeUrl(key, charset)
            ).append("=").append(
                encodeUrl(value, charset)
            ).append("&"));
        }
        return Fs.uncheck(() -> new URL(url.toString()), HttpNetException::new);
    }

    /**
     * Translates a string into {@code application/x-www-form-urlencoded} format using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param str the {@code String} to be translated
     * @return the translated {@code String}
     * @throws HttpNetException if an error occurs
     */
    public static @Nonnull String encodeUrl(@Nonnull String str) throws HttpNetException {
        return encodeUrl(str, CharsKit.defaultCharset());
    }

    /**
     * Translates a string into {@code application/x-www-form-urlencoded} format using the specific charset.
     *
     * @param str     the {@code String} to be translated
     * @param charset the specified charset
     * @return the translated {@code String}
     * @throws HttpNetException if an error occurs
     */
    public static @Nonnull String encodeUrl(@Nonnull String str, @Nonnull Charset charset) throws HttpNetException {
        try {
            return URLEncoder.encode(str, charset.name()).replace("+", "%20");
        } catch (Exception e) {
            throw new HttpNetException(e);
        }
    }

    private HttpKit() {
    }
}
