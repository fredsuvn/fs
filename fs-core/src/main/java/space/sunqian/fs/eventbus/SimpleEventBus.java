package space.sunqian.fs.eventbus;

import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * This interface is a simplified event bus interface. It provides simple methods to register and unregister event
 * subscribers with the specified event type, and post methods to post events with the specified event type.
 *
 * @author sunqian
 */
public interface SimpleEventBus {

    /**
     * Creates a new event bus, all event consuming will be executed in the post thread. It is same as
     * {@link #newEventBus(Executor)} with the executor: {@code Runnable::run}.
     *
     * @return a new event bus
     */
    static @Nonnull SimpleEventBus newEventBus() {
        return newEventBus(Runnable::run);
    }

    /**
     * Creates a new event bus with the specified executor.
     *
     * @param executor the executor to execute event subscribers
     * @return a new event bus
     */
    static @Nonnull SimpleEventBus newEventBus(@Nonnull Executor executor) {
        return new SimpleEventBusImpl(executor);
    }

    /**
     * Registers an event subscriber for the specified event type.
     * <p>
     * The default implementations use copy-on-write to add subscribers, so it is recommended to use
     * {@link #register(Map)} for registering multiple event subscribers at once.
     *
     * @param type       the event type
     * @param subscriber the event subscriber
     * @param <T>        the event type
     */
    <T> void register(@Nonnull Type type, @Nonnull Consumer<@Nonnull T> subscriber);

    /**
     * Registers event subscribers with the specified event types, the keys of the map are the event types, and the
     * values are the event subscribers.
     *
     * @param subscribers the event subscribers
     */
    void register(@Nonnull Map<@Nonnull Type, @Nonnull List<@Nonnull Consumer<@Nonnull ?>>> subscribers);

    /**
     * Unregisters an event subscriber which is registered via {@code register} methods. The subscriber object is the
     * same object that is passed to {@code register} methods.
     * <p>
     * The default implementations use copy-on-write to remove subscribers, so it is recommended to use
     * {@link #unregister(Iterable)} for unregistering multiple event subscribers at once.
     *
     * @param subscriber the event subscriber, which is the same object that is passed to {@code register} methods
     */
    void unregister(@Nonnull Object subscriber);

    /**
     * Unregisters event subscribers which are registered via {@code register} methods. The subscriber objects are the
     * same objects that are passed to {@code register} methods.
     *
     * @param subscribers the event subscribers, which are the same objects that are passed to {@code register} methods
     */
    void unregister(@Nonnull Iterable<?> subscribers);

    /**
     * Posts an event. This method will call {@link #post(Object, Type)} with {@code event} and
     * {@code event.getClass()}.
     *
     * @param event the event to post
     */
    default void post(@Nonnull Object event) {
        post(event, event.getClass());
    }

    /**
     * Posts an event with the specified type. This method will call {@link #post(Object, Type, DispatchMode)} with
     * {@code event}, {@code eventType}, and {@link  DispatchMode#BROADCAST}.
     *
     * @param event     the event to post
     * @param eventType the event type
     */
    default void post(@Nonnull Object event, @Nonnull Type eventType) {
        post(event, eventType, DispatchMode.BROADCAST);
    }

    /**
     * Posts an event with the specified dispatch mode. This method will call {@link #post(Object, Type, DispatchMode)}
     * with {@code event}, {@code event.getClass()}, and {@code dispatchMode}.
     *
     * @param event        the event to post
     * @param dispatchMode the dispatch mode
     */
    default void post(@Nonnull Object event, @Nonnull DispatchMode dispatchMode) {
        post(event, event.getClass(), dispatchMode);
    }

    /**
     * Posts an event with the specified type and dispatch mode.
     *
     * @param event        the event to post
     * @param eventType    the event type
     * @param dispatchMode the dispatch mode
     */
    void post(@Nonnull Object event, @Nonnull Type eventType, @Nonnull DispatchMode dispatchMode);

    /**
     * The dispatch mode for posting events.
     */
    enum DispatchMode {
        /**
         * The broadcast mode: subscribers which subscribe the event type will receive the event.
         * <p>
         * This is the default mode.
         */
        BROADCAST,
        /**
         * The chain dispatch mode: subscribers which subscribe the event type will receive the event in the order they
         * are registered, and the event propagation can be prevented by any subscriber.
         * <p>
         * In this mode, if a subscriber throws an exception, the event propagation will be stopped.
         */
        CHAIN
    }
}
