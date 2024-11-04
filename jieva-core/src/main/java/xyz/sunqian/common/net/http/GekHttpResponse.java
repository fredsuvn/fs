package xyz.sunqian.common.net.http;

import xyz.sunqian.annotations.Nullable;

import java.io.InputStream;

/**
 * Http response info, including status, headers and body.
 * <p>
 * If the body of response is not null, that means the connection may not have been released yet, call
 * {@link InputStream#close()} to release the connection. See {@link #getBody()}.
 *
 * @author fredsuvn
 */
public interface GekHttpResponse {

    /**
     * Returns status code.
     *
     * @return status code
     */
    int getStatusCode();

    /**
     * Returns status reason.
     *
     * @return status reason
     */
    String getStatusReason();

    /**
     * Returns headers.
     *
     * @return headers
     */
    GekHttpHeaders getHeaders();

    /**
     * Returns response body, maybe null.
     * <p>
     * If the stream is not null, that means the http connection may not have been released, call
     * {@link InputStream#close()} to release the connection.
     *
     * @return response body, maybe null
     */
    @Nullable
    InputStream getBody();
}
