package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;

/**
 * Utilities for {@link IOChannel} and {@link IOChannelHandler}.
 *
 * @author sunqian
 */
public class IOChannelKit {

    /**
     * Invokes {@link IOChannelHandler#channelOpen(IOChannel)}, and all {@link Throwable} thrown by it will be caught
     * and passed to {@link IOChannelHandler#exceptionCaught(IOChannel, Throwable)}.
     *
     * @param handler the handler of which {@link IOChannelHandler#channelOpen(IOChannel)} is invoked
     * @param channel the channel to be handled
     */
    public static <C extends IOChannel> void channelOpen(@Nonnull IOChannelHandler<C> handler, @Nonnull C channel) {
        try {
            handler.channelOpen(channel);
        } catch (Throwable e) {
            handler.exceptionCaught(channel, e);
        }
    }

    /**
     * Invokes {@link IOChannelHandler#channelClose(IOChannel)}, and all {@link Throwable} thrown by it will be caught
     * and passed to {@link IOChannelHandler#exceptionCaught(IOChannel, Throwable)}.
     *
     * @param handler the handler of which {@link IOChannelHandler#channelClose(IOChannel)} is invoked
     * @param channel the channel to be handled
     */
    public static <C extends IOChannel> void channelClose(@Nonnull IOChannelHandler<C> handler, @Nonnull C channel) {
        try {
            handler.channelClose(channel);
        } catch (Throwable e) {
            handler.exceptionCaught(channel, e);
        }
    }

    /**
     * Invokes {@link IOChannelHandler#channelRead(IOChannel)}, and all {@link Throwable} thrown by it will be caught
     * and passed to {@link IOChannelHandler#exceptionCaught(IOChannel, Throwable)}.
     *
     * @param handler the handler of which {@link IOChannelHandler#channelRead(IOChannel)} is invoked
     * @param channel the channel to be handled
     */
    public static <C extends IOChannel> void channelRead(@Nonnull IOChannelHandler<C> handler, @Nonnull C channel) {
        try {
            handler.channelRead(channel);
        } catch (Throwable e) {
            handler.exceptionCaught(channel, e);
        }
    }
}
