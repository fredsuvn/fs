package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.nio.channels.ReadableByteChannel;

/**
 * Wraps a given {@link NetChannelHandler} to provide centralized exception handling.
 * <p>
 * This wrapper ensures that exceptions occurring during {@link #channelOpen(NetChannelContext)},
 * {@link #channelClose(NetChannelContext)}, or {@link #channelRead(NetChannelContext, ReadableByteChannel)} operations
 * are automatically routed to the {@link #exceptionCaught(NetChannelContext, Throwable)} method.
 *
 * @author sunqian
 */
public class NetChannelHandlerWrapper implements NetChannelHandler {

    private final @Nonnull NetChannelHandler handler;

    public NetChannelHandlerWrapper(@Nonnull NetChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelOpen(NetChannelContext context) {
        try {
            handler.channelOpen(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void channelClose(NetChannelContext context) {
        try {
            handler.channelClose(context);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void channelRead(NetChannelContext context, ReadableByteChannel reader) {
        try {
            handler.channelRead(context, reader);
        } catch (Exception e) {
            exceptionCaught(context, e);
        }
    }

    @Override
    public void exceptionCaught(@Nullable NetChannelContext context, Throwable cause) {
        handler.exceptionCaught(context, cause);
    }
}
