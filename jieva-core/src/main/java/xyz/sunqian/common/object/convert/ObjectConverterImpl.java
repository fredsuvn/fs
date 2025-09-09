package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.lang.Flag;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.object.convert.handlers.AssignableMapperHandler;
import xyz.sunqian.common.object.convert.handlers.BeanMapperHandler;
import xyz.sunqian.common.object.convert.handlers.CollectionMappingHandler;
import xyz.sunqian.common.object.convert.handlers.EnumMapperHandler;
import xyz.sunqian.common.object.convert.handlers.TypedMapperHandler;
import xyz.sunqian.common.object.data.ObjectProperty;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ObjectConverterImpl implements ObjectConverter, ObjectConverter.Handler {

    static final ObjectConverterImpl DEFAULT_MAPPER = new ObjectConverterImpl(Jie.list(
        new AssignableMapperHandler(),
        new EnumMapperHandler(),
        new TypedMapperHandler(),
        new CollectionMappingHandler(),
        new BeanMapperHandler()
    ), ConversionOptions.defaultOptions2());

    private final List<ObjectConverter.Handler> handlers;
    private final ConversionOptions defaultOptions;

    ObjectConverterImpl(Iterable<ObjectConverter.Handler> handlers, ConversionOptions defaultOptions) {
        this.handlers = ListKit.toList(handlers);
        this.defaultOptions = defaultOptions;
    }

    @Override
    public List<Handler> getHandlers() {
        return handlers;
    }

    @Override
    public ObjectConverter addFirstHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new ObjectConverterImpl(newHandlers, getOptions());
    }

    @Override
    public ObjectConverter addLastHandler(Handler handler) {
        List<Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new ObjectConverterImpl(newHandlers, getOptions());
    }

    @Override
    public ObjectConverter replaceFirstHandler(Handler handler) {
        if (Objects.equals(handlers.get(0), handler)) {
            return this;
        }
        List<Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(0, handler);
        return new ObjectConverterImpl(newHandlers, getOptions());
    }

    @Override
    public ObjectConverter replaceLastHandler(Handler handler) {
        if (Objects.equals(handlers.get(handlers.size() - 1), handler)) {
            return this;
        }
        List<Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(newHandlers.size() - 1, handler);
        return new ObjectConverterImpl(newHandlers, getOptions());
    }

    @Override
    public ConversionOptions getOptions() {
        return defaultOptions;
    }

    @Override
    public ObjectConverter replaceOptions(ConversionOptions options) {
        if (Objects.equals(defaultOptions, options)) {
            return this;
        }
        return new ObjectConverterImpl(handlers, options);
    }

    @Override
    public Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Object map(
        @Nullable Object source, Type sourceType, Type targetType, ObjectConverter objectConverter, ConversionOptions options) {
        Object result = map(source, sourceType, targetType, options);
        if (result == null) {
            return Flag.CONTINUE;
        }
        return result;
    }

    @Override
    public Object mapProperty(
        @Nullable Object source, Type sourceType, Type targetType, ObjectProperty targetProperty, ObjectConverter objectConverter, ConversionOptions options) {
        Object result = mapProperty(source, sourceType, targetType, targetProperty, options);
        if (result == null) {
            return Flag.CONTINUE;
        }
        return result;
    }
}
