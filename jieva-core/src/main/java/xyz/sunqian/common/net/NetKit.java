package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Network utilities.
 *
 * @author sunqian
 */
public class NetKit {

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
