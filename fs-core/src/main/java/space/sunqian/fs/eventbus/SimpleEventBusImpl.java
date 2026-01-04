package space.sunqian.fs.eventbus;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

final class SimpleEventBusImpl implements SimpleEventBus {

    private volatile Map<@Nonnull Type, @Nonnull List<@Nonnull Consumer<@Nonnull ?>>> subscriberMap
        = Collections.emptyMap();
    private final @Nonnull Executor executor;

    SimpleEventBusImpl(@Nonnull Executor executor) {
        this.executor = executor;
    }

    @Override
    public <T> void register(@Nonnull Type type, @Nonnull Consumer<T> subscriber) {
        synchronized (this) {
            Map<Type, List<Consumer<?>>> newMap = new HashMap<>(subscriberMap);
            newMap.computeIfAbsent(type, k -> new ArrayList<>()).add(subscriber);
            this.subscriberMap = newMap;
        }
    }

    @Override
    public void register(@Nonnull Map<@Nonnull Type, @Nonnull List<@Nonnull Consumer<@Nonnull ?>>> subscribers) {
        synchronized (this) {
            Map<Type, List<Consumer<?>>> newMap = new HashMap<>(subscriberMap);
            subscribers.forEach((type, consumerList) ->
                newMap.computeIfAbsent(type, k -> new ArrayList<>()).addAll(consumerList));
            this.subscriberMap = newMap;
        }
    }

    @Override
    public void unregister(@Nonnull Object subscriber) {
        synchronized (this) {
            Map<Type, List<Consumer<?>>> newMap = new HashMap<>(subscriberMap);
            newMap.values().forEach(consumerList ->
                consumerList.removeIf(c -> c == subscriber));
            this.subscriberMap = newMap;
        }
    }

    @Override
    public void unregister(@Nonnull Iterable<?> subscribers) {
        synchronized (this) {
            Map<Type, List<Consumer<?>>> newMap = new HashMap<>(subscriberMap);
            subscribers.forEach(subscriber ->
                newMap.values().forEach(consumerList ->
                    consumerList.removeIf(c -> c == subscriber)));
            this.subscriberMap = newMap;
        }
    }

    @Override
    public void post(@Nonnull Object event, @Nonnull Type eventType, @Nonnull DispatchMode dispatchMode) {
        post0(event, eventType, dispatchMode);
    }

    private void post0(@Nonnull Object event, @Nonnull Type eventType, @Nonnull DispatchMode dispatchMode) {
        List<Consumer<Object>> consumerList = Fs.as(subscriberMap.get(eventType));
        if (consumerList == null) {
            return;
        }
        if (DispatchMode.CHAIN.equals(dispatchMode)) {
            postChain(event, consumerList);
        } else {
            postBroadcast(event, consumerList);
        }
    }

    private void postBroadcast(
        @Nonnull Object event,
        @Nonnull List<@Nonnull Consumer<@Nonnull Object>> consumerList
    ) {
        consumerList.forEach(consumer ->
            executor.execute(() -> consumer.accept(event)));
    }

    private void postChain(
        @Nonnull Object event,
        @Nonnull List<@Nonnull Consumer<@Nonnull Object>> consumerList
    ) {
        executor.execute(() -> {
                for (Consumer<Object> consumer : consumerList) {
                    try {
                        consumer.accept(event);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        );
    }
}
