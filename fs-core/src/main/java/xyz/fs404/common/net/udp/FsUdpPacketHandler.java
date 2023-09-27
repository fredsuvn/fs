package xyz.fs404.common.net.udp;

import xyz.fs404.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * UDP datagram packet callback handler.
 * <p>
 * An implementation of {@link FsUdpServer} has a list of {@link FsUdpPacketHandler}.
 * <p>
 * When the server receives new packet from remote point, the data will be wrapped as {@link ByteBuffer} and passed to
 * {@link #onPacket(FsUdpHeader, Object)} of first handler. Returned object from first handler will be passed to next
 * handler as {@code packet}, and so on.
 * If null is returned, the handlers calling chain will be broken;
 * if an exception is thrown, {@link FsUdpServerHandler#onException(FsUdpHeader, Throwable, ByteBuffer)} will be called
 * and the handlers calling chain will also be broken.
 * <p>
 * If there has remaining data in the buffer not be consumed, the remaining data will be discarded.
 *
 * @param <P> packet type
 * @author fredsuvn
 */
public interface FsUdpPacketHandler<P> {

    /**
     * Callback when new data has received.
     * <p>
     * Server has a list of handlers,
     * the first handler will be passed a {@link ByteBuffer} as {@code packet} parameter,
     * then returned object from first handler will be passed to next handler as {@code packet} parameter, and so on.
     * If null is returned from any handler, the handlers calling chain will be broken at that handler;
     * if an exception is thrown,
     * {@link FsUdpServerHandler#onException(FsUdpHeader, FsUdpClient, Throwable, ByteBuffer)} will be called
     * and the calling chain will also be broken. The process like:
     * <pre>
     *     Object packet = ByteBuffer.wrap(...);
     *     for (FsUdpPacketHandler<...> handler : handlers) {
     *         try {
     *             Object result = handler.onPacket(header, packet);
     *             if (result == null) {
     *                 break;
     *             }
     *             packet = result;
     *         } catch (Throwable e) {
     *             serverHandler.onException(header, e, remaining);
     *             break;
     *         }
     *     }
     * </pre>
     * If there has remaining data in the buffer not be consumed, the remaining data will be discarded.
     *
     * @param header header of the packet
     * @param client the server itself as a client
     * @param packet the packet
     */
    @Nullable
    Object onPacket(FsUdpHeader header, FsUdpClient client, P packet);
}
