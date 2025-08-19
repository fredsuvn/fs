package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;

import java.nio.ByteBuffer;

/**
 * Wraps a given {@link NetChannelHandler} to provide centralized exception handling.
 * <p>
 * This wrapper ensures that exceptions occurring during {@link #channelOpen(NetChannelContext)},
 * {@link #channelClose(NetChannelContext)}, or {@link #channelRead(NetChannelContext, ByteBuffer)} operations are
 * automatically routed to the {@link #exceptionCaught(NetChannelContext, Throwable)} method.
 *
 * @author sunqian
 */
public class NetChannelHandlerWrapper implements NetChannelHandler {

    private final @Nonnull NetChannelHandler handler;

    public NetChannelHandlerWrapper(@Nonnull NetChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelOpen(NetChannelContext context) throws Exception {
        try {
            handler.channelOpen(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void channelClose(NetChannelContext context) throws Exception {
        try {
            handler.channelClose(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void channelRead(NetChannelContext context, ByteBuffer buffer) throws Exception {
        try {
            handler.channelRead(context, buffer);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    /**
     * This method is invoked after catching the unhandled exception thrown from this handler. If this method still
     * throws an exception, the channel will be closed. Typically, this method does not throw an exception, and even if
     * it does, it will be a {@link NetException}.
     *
     * @param context the context of the channel
     * @param cause   the unhandled exception
     */
    @Override
    public void exceptionCaught(NetChannelContext context, Throwable cause) {
        try {
            handler.exceptionCaught(context, cause);
        } catch (Exception e) {
            try {
                context.close();
            } catch (Exception ex) {
                throw new NetException(ex);
            }
        }
    }
}
