package xyz.sunqian.common.net.http;

import lombok.Getter;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.io.IOKit;

import java.net.Proxy;
import java.time.Duration;

/**
 * Http client interface.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekHttpClient {

    /**
     * Return default http client.
     *
     * @return default http client
     */
    static GekHttpClient defaultClient() {
        return Builder.INSTANCE;
    }

    /**
     * Returns new builder of {@link GekHttpClient}.
     *
     * @return new builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Requests with given request info
     *
     * @param request request info
     * @return response of request
     */
    GekHttpResponse request(GekHttpRequest request);

    /**
     * Builder for {@link GekHttpClient}.
     */
    @Getter
    class Builder {

        private static final GekHttpClient INSTANCE = newBuilder().build();

        /**
         * Connection time out.
         */
        private @Nullable Duration connectTimeout;
        /**
         * Read time out.
         */
        private @Nullable Duration readTimeout;
        /**
         * Chunk size.
         */
        private int chunkSize = IOKit.bufferSize();
        /**
         * Proxy info.
         */
        private @Nullable Proxy proxy;

        /**
         * Sets connect timeout.
         *
         * @param connectTimeout connect timeout
         * @return this builder
         */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets read timeout.
         *
         * @param readTimeout read timeout
         * @return this builder
         */
        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets chunk size.
         *
         * @param chunkSize chunk size
         * @return this builder
         */
        public Builder chunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        /**
         * Sets proxy.
         *
         * @param proxy the proxy
         * @return this builder
         */
        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Returns built {@link GekHttpClient}.
         *
         * @return built {@link GekHttpClient}
         */
        GekHttpClient build() {
            return new HttpClientImpl(this);
        }
    }
}
