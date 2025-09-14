package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IOKit;

enum NullServerHandler implements TcpServerHandler {

    INST;

    @Override
    public void channelOpen(@Nonnull TcpContext context) {
    }

    @Override
    public void channelClose(@Nonnull TcpContext context) {
    }

    @Override
    public void channelRead(@Nonnull TcpContext channel) {
        IOKit.availableTo(channel.channel(), IOKit.nullOutputStream());
    }

    @Override
    public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
    }
}
