package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.nio.ByteBuffer;

final class ClientListenerWrapper implements TcpNetClient.Listener {

    private final TcpNetClient client;
    private final TcpNetClient.Listener listener;

    ClientListenerWrapper(TcpNetClient client, TcpNetClient.Listener listener) {
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void onOpen() {
        try {
            listener.onOpen();
        } catch (Exception e) {
            listener.onException(client, null, e);
        }
    }

    @Override
    public void onClose() {
        try {
            listener.onClose();
        } catch (Exception e) {
            listener.onException(client, null, e);
        }
    }

    @Override
    public void onConnection(@Nonnull TcpNetEndpoint endpoint) {
        try {
            listener.onConnection(endpoint);
        } catch (Exception e) {
            listener.onException(client, endpoint, e);
        }
    }

    @Override
    public void onConnectionFailed(@Nonnull TcpNetEndpoint endpoint, @Nonnull Throwable cause) {
        try {
            listener.onConnectionFailed(endpoint, cause);
        } catch (Exception e) {
            listener.onException(client, endpoint, e);
        }
    }

    @Override
    public void onDisconnection(@Nonnull TcpNetEndpoint endpoint) {
        try {
            listener.onDisconnection(endpoint);
        } catch (Exception e) {
            listener.onException(client, endpoint, e);
        }
    }

    @Override
    public void onException(@Nonnull TcpNetClient client, @Nullable TcpNetEndpoint endpoint, Throwable throwable) {
        listener.onException(client, endpoint, throwable);
    }

    @Override
    public void onMessage(@Nonnull TcpNetEndpoint endpoint, @Nonnull ByteBuffer msg) {
        try {
            listener.onMessage(endpoint, msg);
        } catch (Exception e) {
            listener.onException(client, endpoint, e);
        }
    }
}
