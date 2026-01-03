package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.handlers.AssignableConvertHandler;
import space.sunqian.fs.object.convert.handlers.CommonConvertHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class ObjectConverterImpl implements ObjectConverter, ObjectConverter.Handler {

    static final @Nonnull ObjectConverterImpl DEFAULT = new ObjectConverterImpl(FsLoader.loadInstances(
        FsLoader.loadClassByDependent(
            ThirdKit.thirdClassName("protobuf", "ProtobufConvertHandler"),
            "com.google.protobuf.Message"
        ),
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
