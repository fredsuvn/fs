/**
 * Provides builders to rapidly build TCP server and client with minimal effort, using code similar to the following:
 * <pre>{@code
 * TcpServer server = TcpServer.newBuilder()
 *     .handler(handler)
 *     .build();
 * TcpClient client = TcpClient.newBuilder()
 *     .build();
 * }</pre>
 *
 */
package xyz.sunqian.common.net.tcp;