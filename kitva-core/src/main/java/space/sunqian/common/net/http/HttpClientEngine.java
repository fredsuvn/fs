package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.ThreadSafe;
import space.sunqian.common.net.NetException;

import java.net.Proxy;
import java.time.Duration;

/**
 * Engine for http client.
 *
 * @author sunqian
 */
@ThreadSafe
public interface HttpClientEngine {

    /**
     * Creates a new http client engine.
     *
     * @param bufSize the buffer size for io operations
     * @return the new http client engine
     * @throws IllegalArgumentException if the buffer size {@code <= 0}
     */
    static @Nonnull HttpClientEngine newEngine(int bufSize) throws IllegalArgumentException {
        return new EngineImpl(bufSize);
    }

    /**
     * Requests the given http request, returns the response.
     *
     * @param req            the given http request
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param proxy          the proxy
     * @return the response
     * @throws NetException if an error occurs
     */
    @Nonnull
    HttpResp request(
        @Nonnull HttpReq req,
        @Nonnull Duration connectTimeout,
        @Nonnull Duration readTimeout,
        @Nonnull Proxy proxy
    ) throws NetException;
}
