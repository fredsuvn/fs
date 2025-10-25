package space.sunqian.common.net.tcp;

import space.sunqian.annotations.Nonnull;

/**
 * Utilities for TCP.
 *
 * @author sunqian
 */
public class TcpKit {

    static void channelOpen(@Nonnull TcpServerHandler handler, @Nonnull TcpContext context) {
        try {
            handler.channelOpen(context);
        } catch (Throwable e) {
            handler.exceptionCaught(context, e);
        }
    }

    static void channelClose(@Nonnull TcpServerHandler handler, @Nonnull TcpContext context) {
        try {
            handler.channelClose(context);
        } catch (Throwable e) {
            handler.exceptionCaught(context, e);
        }
    }

    static void channelRead(@Nonnull TcpServerHandler handler, @Nonnull TcpContext context) {
        try {
            handler.channelRead(context);
        } catch (Throwable e) {
            handler.exceptionCaught(context, e);
        }
    }

    static void channelLoop(@Nonnull TcpServerHandler handler, @Nonnull TcpContext context) {
        try {
            handler.channelLoop(context);
        } catch (Throwable e) {
            handler.exceptionCaught(context, e);
        }
    }
}
