package xyz.sunqian.common.net.http;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IOKit;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Http response info.
 *
 * @author sunqian
 */
public interface HttpResp {

    /**
     * Returns the protocol version of the response, such as {@code HTTP/1.1} or {@code HTTP/2}.
     *
     * @return the protocol version of the response
     */
    @Nonnull
    String protocolVersion();

    /**
     * Returns the status code of the response, such as {@code 200}, {@code 404}, {@code 500}, etc.
     *
     * @return the status code of the response
     */
    @Nonnull
    String statusCode();

    /**
     * Returns the status text of the response, such as {@code OK}, {@code Not Found}, {@code Internal Server Error},
     * etc.
     *
     * @return the status text of the response
     */
    @Nonnull
    String statusText();

    /**
     * Returns the headers of the response.
     *
     * @return the headers of the response
     */
    @Nonnull
    Map<String, List<String>> headers();

    /**
     * Returns the body of the response.
     *
     * @return the body of the response
     */
    @Nonnull
    InputStream body();

    /**
     * Returns the content type of the response, may be {@code null} if the content type is not specified.
     *
     * @return the content type of the response, may be {@code null} if the content type is not specified
     */
    @Nullable
    String contentType();

    /**
     * Returns the charset of the response, parsed from the content type, may be {@code null} if the charset is not
     * specified.
     *
     * @return the charset of the response, parsed from the content type, may be {@code null} if the charset is not
     * specified
     */
    default @Nullable Charset bodyCharset() {
        String contentType = contentType();
        if (contentType == null) {
            return null;
        }
        return HttpKit.parseCharset(contentType);
    }

    /**
     * Returns the body of the response as a string. The charset of the string is determined by the content type, and if
     * the content type is not specified, {@link CharsKit#defaultCharset()} will be used. Using {@link #bodyCharset()}
     * can get the charset this method uses, if the returned charset is not {@code null}.
     *
     * @return the body of the response as a string, may be {@code null} if the response body is empty
     */
    default @Nullable String bodyString() {
        Charset charset = Jie.nonnull(bodyCharset(), CharsKit.defaultCharset());
        return IOKit.string(body(), charset);
    }
}
