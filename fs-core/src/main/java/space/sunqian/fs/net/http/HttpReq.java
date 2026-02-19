package space.sunqian.fs.net.http;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.BufferKit;
import space.sunqian.fs.io.IOKit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
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
     * The default request timeout: 30 seconds.
     */
    @Nonnull
    Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(30);

    /**
     * Returns a new builder for {@link HttpReq}.
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
    Map<@Nonnull String, @Nonnull List<@Nonnull String>> headers();

    /**
     * Returns the body of the request, or {@code null} if no body data.
     *
     * @return the body of the request, or {@code null} if no body data
     */
    @Nullable
    Body body();

    /**
     * Returns the timeout of the request. The default is {@link #DEFAULT_REQUEST_TIMEOUT}.
     *
     * @return the timeout of the request
     */
    @Nonnull
    Duration timeout();

    /**
     * Represents the body of the request.
     */
    interface Body {

        /**
         * Returns the type of the body.
         *
         * @return the type of the body
         */
        @Nonnull
        Type type();

        /**
         * Returns an {@link InputStream} to read the data of the body. If the data is originally of input stream type,
         * return the data itself.
         *
         * @return an {@link InputStream} to read the data of the body
         */
        @Nonnull
        InputStream toInputStream();

        /**
         * Returns a byte array represents the data of the body. If the data is originally of byte array type, return
         * the data itself.
         *
         * @return a byte array represents the data of the body
         */
        byte @Nonnull [] toByteArray();

        /**
         * Returns a byte buffer represents the data of the body. If the data is originally of byte buffer type, return
         * the data itself.
         *
         * @return a byte buffer represents the data of the body
         */
        @Nonnull
        ByteBuffer toByteBuffer();

        /**
         * Returns a string represents the data of the body using {@link CharsKit#defaultCharset()}. If the data is
         * originally of string type, return the data itself.
         *
         * @return a string represents the data of the body using {@link CharsKit#defaultCharset()}
         */
        @Nonnull
        String toText();

        /**
         * Type of request body.
         */
        enum Type {
            /**
             * Input stream.
             */
            INPUT_STREAM,
            /**
             * Byte Array.
             */
            BYTE_ARRAY,
            /**
             * Byte Buffer.
             */
            BYTE_BUFFER,
            /**
             * Text.
             */
            TEXT
        }
    }

    /**
     * Builder for {@link HttpReq}.
     */
    class Builder {

        private URL url;
        private String method = "GET";
        private Map<String, List<String>> headers;
        private @Nullable Body body;
        private @Nonnull Duration timeout = DEFAULT_REQUEST_TIMEOUT;

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
         * @throws HttpNetException if the url is invalid
         */
        public @Nonnull Builder url(@Nonnull String url) throws HttpNetException {
            return url(Fs.uncheck(() -> new URL(url), HttpNetException::new));
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
        public @Nonnull Builder headers(
            @Nonnull @RetainedParam Map<@Nonnull String, @Nonnull List<@Nonnull String>> headers
        ) {
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
         * @param body the body from the specified input stream
         * @return this builder
         */
        public @Nonnull Builder body(@Nonnull InputStream body) {
            this.body = new Body() {

                @Override
                public @Nonnull Type type() {
                    return Type.INPUT_STREAM;
                }

                @Override
                public @Nonnull InputStream toInputStream() {
                    return body;
                }

                @Override
                public byte @Nonnull [] toByteArray() {
                    return Fs.asNonnull(IOKit.read(body));
                }

                @Override
                public @Nonnull ByteBuffer toByteBuffer() {
                    return ByteBuffer.wrap(toByteArray());
                }

                @Override
                public @Nonnull String toText() {
                    return Fs.asNonnull(IOKit.string(body));
                }
            };
            return this;
        }

        /**
         * Sets the body of the request. The body uses {@link CharsKit#defaultCharset()}.
         *
         * @param body the body from the specified string
         * @return this builder
         */
        public @Nonnull Builder body(@Nonnull String body) {
            this.body = new Body() {

                @Override
                public @Nonnull Type type() {
                    return Type.TEXT;
                }

                @Override
                public @Nonnull InputStream toInputStream() {
                    return new ByteArrayInputStream(body.getBytes(CharsKit.defaultCharset()));
                }

                @Override
                public byte @Nonnull [] toByteArray() {
                    return body.getBytes(CharsKit.defaultCharset());
                }

                @Override
                public @Nonnull ByteBuffer toByteBuffer() {
                    return ByteBuffer.wrap(toByteArray());
                }

                @Override
                public @Nonnull String toText() {
                    return body;
                }
            };
            return this;
        }

        /**
         * Sets the body of the request.
         *
         * @param body the body from the specified byte array
         * @return this builder
         */
        public @Nonnull Builder body(byte @Nonnull [] body) {
            this.body = new Body() {

                @Override
                public @Nonnull Type type() {
                    return Type.BYTE_ARRAY;
                }

                @Override
                public @Nonnull InputStream toInputStream() {
                    return new ByteArrayInputStream(body);
                }

                @Override
                public byte @Nonnull [] toByteArray() {
                    return body;
                }

                @Override
                public @Nonnull ByteBuffer toByteBuffer() {
                    return ByteBuffer.wrap(toByteArray());
                }

                @Override
                public @Nonnull String toText() {
                    return new String(body, CharsKit.defaultCharset());
                }
            };
            return this;
        }

        /**
         * Sets the body of the request.
         *
         * @param body the body from the specified byte buffer
         * @return this builder
         */
        public @Nonnull Builder body(@Nonnull ByteBuffer body) {
            this.body = new Body() {

                @Override
                public @Nonnull Type type() {
                    return Type.BYTE_BUFFER;
                }

                @Override
                public @Nonnull InputStream toInputStream() {
                    return IOKit.newInputStream(body);
                }

                @Override
                public byte @Nonnull [] toByteArray() {
                    return Fs.asNonnull(BufferKit.read(body));
                }

                @Override
                public @Nonnull ByteBuffer toByteBuffer() {
                    return body;
                }

                @Override
                public @Nonnull String toText() {
                    return new String(toByteArray(), CharsKit.defaultCharset());
                }
            };
            return this;
        }

        /**
         * Sets the timeout of the request.
         *
         * @param timeout the timeout of the request
         * @return this builder
         */
        public @Nonnull Builder timeout(@Nonnull Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Builds and returns a {@link HttpReq} with the configurations.
         *
         * @return a {@link HttpReq} with the configurations
         * @throws IllegalArgumentException if there exists invalid arguments
         */
        public @Nonnull HttpReq build() throws IllegalArgumentException {
            Checker.checkArgument(url != null, "The url can not be null.");
            // CheckKit.checkArgument(method != null, "The method can not be null.");
            return new HttpReqImpl(
                url,
                method,
                Fs.nonnull(headers, Collections.emptyMap()),
                body,
                timeout
            );
        }

        private static final class HttpReqImpl implements HttpReq {

            private final @Nonnull URL url;
            private final @Nonnull String method;
            private final @Nonnull Map<String, List<String>> headers;
            private final @Nullable Body body;
            private final @Nonnull Duration timeout;

            private HttpReqImpl(
                @Nonnull URL url,
                @Nonnull String method,
                @Nonnull Map<String, List<String>> headers,
                @Nullable Body body,
                @Nonnull Duration timeout
            ) {
                this.url = url;
                this.method = method;
                this.headers = headers;
                this.body = body;
                this.timeout = timeout;
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
            public @Nullable Body body() {
                return body;
            }

            @Override
            public @Nonnull Duration timeout() {
                return timeout;
            }
        }
    }
}
