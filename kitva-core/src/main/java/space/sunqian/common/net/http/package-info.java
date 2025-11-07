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
 * or
 * <pre>{@code
 * HttpResp resp = HttpCaller.newHttpCaller().request(
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
 *     <li>{@link space.sunqian.common.net.http.HttpKit}</li>
 * </ul>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.common.net.http.HttpCaller}</li>
 *     <li>{@link space.sunqian.common.net.http.HttpReq}</li>
 *     <li>{@link space.sunqian.common.net.http.HttpResp}</li>
 * </ul>
 */
package space.sunqian.common.net.http;