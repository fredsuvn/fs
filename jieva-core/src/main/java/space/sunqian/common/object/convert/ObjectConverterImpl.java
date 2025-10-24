package space.sunqian.common.object.convert;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.option.Option;
import space.sunqian.common.object.convert.handlers.AssignableConvertHandler;
import space.sunqian.common.object.convert.handlers.CommonConvertHandler;

import java.lang.reflect.Type;
import java.util.List;

final class ObjectConverterImpl implements ObjectConverter, ObjectConverter.Handler {

    static final @Nonnull ObjectConverterImpl DEFAULT_MAPPER = new ObjectConverterImpl(Kit.list(
        new AssignableConvertHandler(),
        new CommonConvertHandler()
    ));

    private final @Nonnull List<ObjectConverter.@Nonnull Handler> handlers;

    ObjectConverterImpl(@RetainedParam List<ObjectConverter.@Nonnull Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public @Nonnull List<@Nonnull Handler> handlers() {
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
