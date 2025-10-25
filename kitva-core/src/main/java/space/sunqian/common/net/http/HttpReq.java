package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.CheckKit;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.net.NetException;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Http request info, can be built by {@link #newBuilder()}.
 *
 * @author sunqian
 */
public interface HttpReq {

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the url of the request.
     *
     * @return the url of the request
     */
    @Nonnull
    URL url();

    /**
     * Returns the method of the request.
     *
     * @return the method of the request
     */
    @Nonnull
    String method();

    /**
     * Returns the headers of the request.
     *
     * @return the headers of the request
     */
    @Nonnull
    Map<String, List<String>> headers();

    /**
     * Returns the body of the request.
     *
     * @return the body of the request
     */
    @Nonnull
    InputStream body();

    /**
     * Builder for {@link HttpReq}.
     */
    class Builder {

        private URL url;
        private String method = "GET";
        private Map<String, List<String>> headers;
        private InputStream body;

        /**
         * Sets the url of the request.
         *
         * @param url the url of the request
         * @return this builder
         */
        public @Nonnull Builder url(@Nonnull URL url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the url of the request.
         *
         * @param url the url of the request
         * @return this builder
         * @throws NetException if the url is invalid
         */
        public @Nonnull Builder url(@Nonnull String url) throws NetException {
            return url(Kit.uncheck(() -> new URL(url), NetException::new));
        }

        /**
         * Sets the method of the request.
         *
         * @param method the method of the request
         * @return this builder
         */
        public @Nonnull Builder method(@Nonnull String method) {
            this.method = method;
            return this;
        }

        /**
         * Sets the headers of the request.
         *
         * @param headers the headers of the request
         * @return this builder
         */
        public @Nonnull Builder headers(@Nonnull Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Adds a header of the request on the current builder.
         *
         * @param key   the key of the header
         * @param value the value of the header
         * @return this builder
         */
        public @Nonnull Builder header(@Nonnull String key, @Nonnull String value) {
            Map<String, List<String>> headers = this.headers;
            if (headers == null) {
                headers = new LinkedHashMap<>();
                this.headers = headers;
            }
            headers.compute(key, (k, l) -> {
                if (l == null) {
                    List<String> list = new ArrayList<>(1);
                    list.add(value);
                    return list;
                } else {
                    l.add(value);
                    return l;
                }
            });
            return this;
        }

        /**
         * Sets the body of the request.
         *
         * @param body the body of the request
         * @return this builder
         */
        public @Nonnull Builder body(@Nonnull InputStream body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the body of the request. The body uses {@link CharsKit#defaultCharset()}.
         *
         * @param body the body of the request
         * @return this builder
         */
        public @Nonnull Builder body(@Nonnull String body) {
            return body(body, CharsKit.defaultCharset());
        }

        /**
         * Sets the body of the request.
         *
         * @param body    the body of the request
         * @param charset the charset of the body
         * @return this builder
         */
        public @Nonnull Builder body(@Nonnull String body, @Nonnull Charset charset) {
            return body(IOKit.newInputStream(new StringReader(body), charset));
        }

        /**
         * Builds and returns a {@link HttpReq} with the configurations.
         *
         * @return a {@link HttpReq} with the configurations
         * @throws IllegalArgumentException if there exists invalid arguments
         */
        public @Nonnull HttpReq build() throws IllegalArgumentException {
            CheckKit.checkArgument(url != null, "The url can not be null.");
            // CheckKit.checkArgument(method != null, "The method can not be null.");
            return new HttpReqImpl(
                url,
                method,
                Kit.nonnull(headers, Collections.emptyMap()),
                Kit.nonnull(body, IOKit.emptyInputStream())
            );
        }

        private static final class HttpReqImpl implements HttpReq {

            private final @Nonnull URL url;
            private final @Nonnull String method;
            private final @Nonnull Map<String, List<String>> headers;
            private final @Nonnull InputStream body;

            private HttpReqImpl(
                @Nonnull URL url,
                @Nonnull String method,
                @Nonnull Map<String, List<String>> headers,
                @Nonnull InputStream body
            ) {
                this.url = url;
                this.method = method;
                this.headers = headers;
                this.body = body;
            }


            @Override
            public @Nonnull URL url() {
                return url;
            }

            @Override
            public @Nonnull String method() {
                return method;
            }

            @Override
            public @Nonnull Map<String, List<String>> headers() {
                return headers;
            }

            @Override
            public @Nonnull InputStream body() {
                return body;
            }
        }
    }
}
