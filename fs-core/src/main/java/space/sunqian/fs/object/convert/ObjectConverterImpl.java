package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.object.convert.handlers.AssignableConvertHandler;
import space.sunqian.fs.object.convert.handlers.CommonConvertHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

final class ObjectConverterImpl implements ObjectConverter, ObjectConverter.Handler {

    static final @Nonnull ObjectConverterImpl DEFAULT = new ObjectConverterImpl(FsLoader.loadInstances(
        FsLoader.loadClassByDependent(
            ThirdKit.thirdClassName("protobuf", "ProtobufConvertHandler"),
            "com.google.protobuf.Message"
        ),
        AssignableConvertHandler.getInstance(),
        CommonConvertHandler.getInstance()
    ), Collections.emptyList());

    private final @Nonnull List<ObjectConverter.@Nonnull Handler> handlers;
    private final @Nonnull List<@Nonnull Option<?, ?>> defaultOptions;
    private final @Nonnull Option<?, ?> @Nonnull [] defaultOptionsArray;

    ObjectConverterImpl(
        @Nonnull @RetainedParam List<ObjectConverter.@Nonnull Handler> handlers,
        @Nonnull @RetainedParam List<@Nonnull Option<?, ?>> defaultOptions
    ) {
        this.handlers = Collections.unmodifiableList(handlers);
        this.defaultOptions = Collections.unmodifiableList(defaultOptions);
        this.defaultOptionsArray = defaultOptions.toArray(new Option[0]);
    }

    @Override
    public Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws ObjectConvertException {
        @Nonnull Option<?, ?> @Nonnull [] actualOptions = OptionKit.mergeOptions(defaultOptionsArray, options);
        for (Handler handler : handlers()) {
            Object ret;
            try {
                ret = handler.convert(src, srcType, target, this, actualOptions);
            } catch (Exception e) {
                throw new ObjectConvertException(e);
            }
            if (ret == Status.HANDLER_CONTINUE) {
                continue;
            }
            if (ret == Status.HANDLER_BREAK) {
                throw new UnsupportedObjectConvertException(src, srcType, target, this, actualOptions);
            }
            return Fs.as(ret);
        }
        throw new UnsupportedObjectConvertException(src, srcType, target, this, actualOptions);
    }

    @Override
    public @Nonnull List<@Nonnull Handler> handlers() {
        return handlers;
    }

    @Override
    public @Nonnull List<@Nonnull Option<?, ?>> defaultOptions() {
        return defaultOptions;
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
