package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IOKit;

final class NullServerHandler implements TcpServerHandler {

    static final @Nonnull NullServerHandler SINGLETON = new NullServerHandler();

    @Override
    public void channelOpen(@Nonnull TcpServerHandler.Context context) {
    }

    @Override
    public void channelClose(@Nonnull TcpServerHandler.Context context) {
    }

    @Override
    public void channelRead(@Nonnull TcpServerHandler.Context channel) {
        IOKit.availableTo(channel.channel(), IOKit.nullOutputStream());
    }

    @Override
    public void exceptionCaught(@Nullable TcpServerHandler.Context context, @Nonnull Throwable cause) {
    }
}
