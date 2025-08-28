/**
 * Provides http utilities and interfaces to request http server with minimal effort, using code similar to the
 * following:
 * <pre>{@code
 * HttpResp resp = HttpKit.request(
 *     HttpReq.newBuilder()
 *         .url("http://localhost:" + httpServer.localAddress().getPort())
 *         .method("GET")
 *         .header(key, value)
 *         .header(key, value)
 *         .build()
 *     )
 * );
 * }</pre>
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link xyz.sunqian.common.net.http.HttpKit}</li>
 * </ul>
 * Core interfaces:
 * <ul>
 *     <li>{@link xyz.sunqian.common.net.http.HttpReq}</li>
 *     <li>{@link xyz.sunqian.common.net.http.HttpResp}</li>
 *     <li>{@link xyz.sunqian.common.net.http.HttpClientEngine}</li>
 * </ul>
 */
package xyz.sunqian.common.net.http;