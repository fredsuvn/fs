package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.handlers.AssignableConvertHandler;
import xyz.sunqian.common.object.convert.handlers.CommonConvertHandler;
import xyz.sunqian.common.object.convert.handlers.TypedMapperHandler;

import java.lang.reflect.Type;
import java.util.List;

final class ObjectConverterImpl implements ObjectConverter, ObjectConverter.Handler {

    static final ObjectConverterImpl DEFAULT_MAPPER = new ObjectConverterImpl(Jie.list(
        new AssignableConvertHandler(),
        // new EnumConversionHandler(),
        new TypedMapperHandler(),
        // new CollectionConversionHandler(CollectionConversionHandler.defaultBuilderFactory()),
        // new DataConversionHandler(DataConversionHandler.defaultBuilderFactory()),
        new CommonConvertHandler()
    ));

    private final List<ObjectConverter.Handler> handlers;

    ObjectConverterImpl(@RetainedParam List<ObjectConverter.Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public @Nonnull List<Handler> handlers() {
        return handlers;
    }

    @Override
    public @Nonnull Handler asHandler() {
        return this;
    }

    @Override
    public Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        try {
            return convert(src, srcType, target, options);
        } catch (UnsupportedObjectConvertException e) {
            return Status.HANDLER_CONTINUE;
        }
    }
}
