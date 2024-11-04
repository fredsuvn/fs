package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.net.tcp.handlers.DelimiterBasedTcpChannelHandler;
import xyz.sunqian.common.net.tcp.handlers.LengthBasedTcpChannelHandler;

import java.nio.ByteBuffer;

/**
 * TCP/IP network channel callback handler.
 * <p>
 * An implementation of {@link GekTcpServer} has a list of {@link GekTcpChannelHandler}.
 * <p>
 * When the server receives new data from remote point, the data will be wrapped as {@link ByteBuffer} and passed to
 * {@link #onMessage(GekTcpChannel, Object)} of first handler. Returned object from first handler will be passed to next
 * handler as {@code message}, and so on. If null is returned, the handlers calling chain will be broken; if an
 * exception is thrown, {@link GekTcpServerHandler#onException(GekTcpChannel, Throwable, ByteBuffer)} will be called and
 * the handlers calling chain will also be broken.
 * <p>
 * {@link #onMessage(GekTcpChannel, Object)} only be called when new data was received, for more detailed operations,
 * try {@link GekTcpServerHandler#onLoop(GekTcpChannel, boolean, ByteBuffer)}.
 * <p>
 * If buffer of the channel is full, it will not read and put again. If the remaining data from the last callback has
 * not been consumed, it will be left until the next callback.
 * <p>
 * See the built-in handler implementations: {@link LengthBasedTcpChannelHandler},
 * {@link DelimiterBasedTcpChannelHandler}.
 *
 * @param <M> message type
 * @author fredsuvn
 * @see LengthBasedTcpChannelHandler
 * @see DelimiterBasedTcpChannelHandler
 */
public interface GekTcpChannelHandler<M> {

    /**
     * Callback when new data has received.
     * <p>
     * Server has a list of handlers, the first handler will be passed a {@link ByteBuffer} as {@code message}
     * parameter, then returned object from first handler will be passed to next handler as {@code message} parameter,
     * and so on. If null is returned from any handler, the handlers calling chain will be broken at that handler; if an
     * exception is thrown, {@link GekTcpServerHandler#onException(GekTcpChannel, Throwable, ByteBuffer)} will be called
     * and the calling chain will also be broken. The process like:
     * <pre>
     *     Object message = ByteBuffer.wrap(...);
     *     for (GekNetChannelHandler&lt;...&gt; channelHandler : channelHandlers) {
     *         try {
     *             Object result = channelHandler.onMessage(channel, message);
     *             if (result == null) {
     *                 break;
     *             }
     *             message = result;
     *         } catch (Throwable e) {
     *             serverHandler.onException(channel, e, remaining);
     *             break;
     *         }
     *     }
     * </pre>
     * If buffer of the channel is full, it will not read and put again. If the buffer on this callback is not fully
     * consumed (remaining() &gt; 0), the remaining data will be reserved to next calling. However, the handler chain
     * will be called if and only if there has new data read from the remote endpoint. It's best to try to consume as
     * much as possible for each time, or using {@link GekTcpServerHandler#onLoop(GekTcpChannel, boolean, ByteBuffer)}
     * to deal with un-consumed data.
     *
     * @param channel the channel
     * @param message the message
     * @return result of callback
     */
    @Nullable
    Object onMessage(GekTcpChannel channel, M message);
}
