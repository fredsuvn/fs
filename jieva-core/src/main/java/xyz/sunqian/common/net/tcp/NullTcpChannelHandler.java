package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IOKit;

final class NullTcpChannelHandler implements TcpChannelHandler {

    static final @Nonnull NullTcpChannelHandler SINGLETON = new NullTcpChannelHandler();

    @Override
    public void channelOpen(@Nonnull TcpChannel channel) {
    }

    @Override
    public void channelClose(@Nonnull TcpChannel channel) {
    }

    @Override
    public void channelRead(@Nonnull TcpChannel channel) {
        IOKit.availableTo(channel, IOKit.nullOutputStream());
    }

    @Override
    public void exceptionCaught(@Nullable TcpChannel channel, @Nonnull Throwable cause) {

    }
}
