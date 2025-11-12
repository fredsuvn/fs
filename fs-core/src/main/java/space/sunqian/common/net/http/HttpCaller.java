package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.io.IOKit;

import java.net.Proxy;

/**
 * {@code HttpCaller} is a very simple HTTP client interface. It can set some default settings for HTTP requests, such
 * as proxy.
 *
 * @author sunqian
 */
public interface HttpCaller {

    /**
     * Returns a new {@link HttpCaller} with the default settings.
     *
     * @return a new {@link HttpCaller}
     */
    static @Nonnull HttpCaller newHttpCaller() {
        return new HttpCaller.Builder().build();
    }

    /**
     * Returns a new builder for {@link HttpCaller}.
     *
     * @return a new builder
     */
    static @Nonnull HttpCaller.Builder newBuilder() {
        return new HttpCaller.Builder();
    }

    /**
     * Requests the given http request, returns the response.
     *
     * @param req the given http request
     * @return the response
     * @throws HttpNetException if an error occurs
     */
    @Nonnull
    HttpResp request(@Nonnull HttpReq req) throws HttpNetException;

    /**
     * Builder for {@link HttpCaller}.
     */
    class Builder {

        private @Nonnull Proxy proxy = Proxy.NO_PROXY;
        private int bufSize = IOKit.bufferSize();

        /**
         * Sets the proxy of the caller.
         *
         * @param proxy the proxy of the caller
         * @return this builder
         */
        public @Nonnull Builder proxy(@Nonnull Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Sets the buffer size of the caller for I/O operations if needed.
         *
         * @param bufSize the buffer size of the caller
         * @return this builder
         */
        public @Nonnull Builder bufSize(int bufSize) {
            this.bufSize = bufSize;
            return this;
        }

        /**
         * Builds and returns a {@link HttpCaller} with the configurations.
         *
         * @return a {@link HttpCaller} with the configurations
         */
        public @Nonnull HttpCaller build() throws HttpNetException {
            return HttpCallerService.INST.newCaller(bufSize, proxy);
        }
    }
}
