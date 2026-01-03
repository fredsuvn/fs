/**
 * Provides builders to rapidly build TCP server and client with minimal effort, using codes similar to the following:
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
 *     <li>{@link space.sunqian.fs.net.tcp.TcpServer}</li>
 *     <li>{@link space.sunqian.fs.net.tcp.TcpClient}</li>
 * </ul>
 */
package space.sunqian.fs.net.tcp;