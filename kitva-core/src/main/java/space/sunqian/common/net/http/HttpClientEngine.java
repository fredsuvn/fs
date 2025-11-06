package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.ThreadSafe;
import space.sunqian.common.net.NetException;

import java.net.Proxy;

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
        return HttpClientEngineService.INST.newEngine(bufSize);
    }

    /**
     * Requests the given http request, returns the response.
     *
     * @param req   the given http request
     * @param proxy the proxy, may be {@code null} if no proxy is needed
     * @return the response
     * @throws NetException if an error occurs
     */
    @Nonnull
    HttpResp request(@Nonnull HttpReq req, @Nullable Proxy proxy) throws NetException;
}
