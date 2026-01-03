/**
 * Provides builders to rapidly build UDP server and sender with minimal effort, using codes similar to the following:
 * <pre>{@code
 * UdpServer server = UdpServer.newBuilder()
 *     .handler(handler)
 *     .bind();
 * UdpSender sender = UdpSender.newSender(true);
 * }</pre>
 * <p>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.net.udp.UdpServer}</li>
 *     <li>{@link space.sunqian.fs.net.udp.UdpSender}</li>
 * </ul>
 */
package space.sunqian.fs.net.udp;