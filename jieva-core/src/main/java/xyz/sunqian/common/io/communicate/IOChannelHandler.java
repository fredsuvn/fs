package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IOKit;

/**
 * Handler for handling events of IO Communication.
 *
 * @param <C> the type of the {@link IOChannel}
 * @author sunqian
 */
public interface IOChannelHandler<C extends IOChannel> {

    /**
     * Returns a {@link Wrapper} for the given {@link IOChannelHandler}.
     * <p>
     * The wrapper ensures that any {@link Throwable} thrown during {@link #channelOpen(IOChannel)},
     * {@link #channelClose(IOChannel)}, or {@link #channelRead(IOChannel)} operations are automatically routed to the
     * {@link #exceptionCaught(IOChannel, Throwable)} method.
     *
     * @param handler the given handler
     * @param <C>     the type of the {@link IOChannel}
     * @return a {@link Wrapper} for the given {@link IOChannelHandler}
     */
    static <C extends IOChannel> @Nonnull Wrapper<C> wrapper(@Nonnull IOChannelHandler<C> handler) {
        return new Wrapper<>(handler);
    }

    /**
     * Returns a handler that only reads and then discards all received data.
     *
     * @param <C> the type of the {@link IOChannel}
     * @return a handler that only reads and then discards all received data
     */
    static <C extends IOChannel> @Nonnull Null<C> nullHandler() {
        return Jie.as(Null.SINGLETON);
    }

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param channel the new channel
     * @throws Exception for any error
     */
    void channelOpen(@Nonnull C channel) throws Exception;

    /**
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param channel the closed channel
     * @throws Exception for any error
     */
    void channelClose(@Nonnull C channel) throws Exception;

    /**
     * This method is invoked after a channel has available data.
     *
     * @param channel the channel has available data
     * @throws Exception for any error
     */
    void channelRead(@Nonnull C channel) throws Exception;

    /**
     * This method is invoked after catching an unhandled exception, the exception may come from this handler or from
     * the container running this handler.
     * <p>
     * The behavior is undefined if this method still throws an exception.
     *
     * @param channel the channel parameter of the method where this handler throws the exception, may be {@code null}
     *                if the exception is not thrown from this handler
     * @param cause   the unhandled exception
     */
    void exceptionCaught(@Nullable C channel, @Nonnull Throwable cause);

    /**
     * Sub-interface for an {@link IOChannelHandler} that all methods have no checked exception declaration, which can
     * be used to avoid handling checked exceptions
     *
     * @param <C> the type of the {@link IOChannel}
     * @author sunqian
     */
    interface Unchecked<C extends IOChannel> extends IOChannelHandler<C> {

        void channelOpen(@Nonnull C channel);

        void channelClose(@Nonnull C channel);

        void channelRead(@Nonnull C channel);

        void exceptionCaught(@Nullable C channel, @Nonnull Throwable cause);
    }

    /**
     * Wraps a given {@link IOChannelHandler} to provide centralized exception handling. And it is a type of
     * {@link IOChannelHandler.Unchecked}.
     * <p>
     * This wrapper ensures that any {@link Throwable} thrown during {@link #channelOpen(IOChannel)} ,
     * {@link #channelClose(IOChannel)}, or {@link #channelRead(IOChannel)} operations are automatically routed to the
     * {@link #exceptionCaught(IOChannel, Throwable)} method.
     *
     * @param <C> the type of the {@link IOChannel}
     * @author sunqian
     */
    class Wrapper<C extends IOChannel> implements Unchecked<C> {

        private final @Nonnull IOChannelHandler<C> handler;

        /**
         * Creates a new instance with the given {@link IOChannelHandler}.
         *
         * @param handler the given handler
         */
        public Wrapper(@Nonnull IOChannelHandler<C> handler) {
            this.handler = handler;
        }

        public void channelOpen(@Nonnull C channel) {
            try {
                handler.channelOpen(channel);
            } catch (Throwable e) {
                exceptionCaught(channel, e);
            }
        }

        public void channelClose(@Nonnull C channel) {
            try {
                handler.channelClose(channel);
            } catch (Throwable e) {
                exceptionCaught(channel, e);
            }
        }

        public void channelRead(@Nonnull C channel) {
            try {
                handler.channelRead(channel);
            } catch (Throwable e) {
                exceptionCaught(channel, e);
            }
        }

        public void exceptionCaught(@Nullable C channel, @Nonnull Throwable cause) {
            handler.exceptionCaught(channel, cause);
        }

    }

    /**
     * An implementation of {@link IOChannelHandler} that only reads and then discards all received data. It is a type
     * of {@link IOChannelHandler.Unchecked}.
     *
     * @param <C> the type of the {@link IOChannel}
     * @author sunqian
     */
    class Null<C extends IOChannel> implements Unchecked<C> {

        private static final @Nonnull IOChannelHandler.Null<?> SINGLETON = new IOChannelHandler.Null<>();

        public void channelOpen(@Nonnull C channel) {
        }

        public void channelClose(@Nonnull C channel) {
        }

        public void channelRead(@Nonnull C channel) {
            IOKit.availableTo(channel, IOKit.nullOutputStream());
        }

        public void exceptionCaught(@Nullable C channel, @Nonnull Throwable cause) {
        }
    }
}
