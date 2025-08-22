package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

/**
 * Wraps a given {@link NetChannelHandler} to provide centralized exception handling.
 * <p>
 * This wrapper ensures that exceptions occurring during {@link #channelOpen(NetChannelContext)},
 * {@link #channelClose(NetChannelContext)}, or {@link #channelRead(NetChannelContext)} operations are automatically
 * routed to the {@link #exceptionCaught(NetChannelContext, Throwable)} method.
 *
 * @author sunqian
 */
public class NetChannelHandlerWrapper implements NetChannelHandler {

    private final @Nonnull NetChannelHandler handler;

    public NetChannelHandlerWrapper(@Nonnull NetChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelOpen(@Nonnull NetChannelContext context) {
        try {
            handler.channelOpen(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void channelClose(@Nonnull NetChannelContext context) {
        try {
            handler.channelClose(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void channelRead(@Nonnull NetChannelContext context) {
        try {
            handler.channelRead(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
        handler.exceptionCaught(context, cause);
    }
}
