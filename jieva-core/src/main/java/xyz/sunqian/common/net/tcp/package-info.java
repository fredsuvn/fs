/**
 * Provides builders to rapidly build TCP server and client with minimal effort, using code similar to the following:
 * <pre>{@code
 * TcpServer server = TcpServer.newBuilder()
 *     .handler(handler)
 *     .bind();
 * TcpClient client = TcpClient.newBuilder()
 *     .bind(address)
 *     .connect();
 * }</pre>
 * <p>
 * Core interfaces:
 * <ul>
 *     <li>{@link xyz.sunqian.common.net.tcp.TcpServer}</li>
 *     <li>{@link xyz.sunqian.common.net.tcp.TcpClient}</li>
 * </ul>
 */
package xyz.sunqian.common.net.tcp;