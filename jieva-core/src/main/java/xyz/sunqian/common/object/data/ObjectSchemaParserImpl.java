package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.object.data.handlers.SimpleBeanSchemaHandler;
import xyz.sunqian.common.runtime.reflect.ClassKit;

import java.util.List;

final class ObjectSchemaParserImpl implements ObjectSchemaParser, ObjectSchemaParser.Handler {

    static @Nonnull ObjectSchemaParserImpl DEFAULT = new ObjectSchemaParserImpl(ClassKit.runtimeInstances(
        "xyz.sunqian.common.third.protobuf.ProtobufSchemaHandler",
        SimpleBeanSchemaHandler.class.getName()
    ));

    private final @Nonnull List<ObjectSchemaParser.@Nonnull Handler> handlers;

    ObjectSchemaParserImpl(@RetainedParam List<ObjectSchemaParser.@Nonnull Handler> handlers) {
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
    public boolean parse(@Nonnull Context context) throws Exception {
        for (Handler handler : handlers) {
            if (!handler.parse(context)) {
                return false;
            }
        }
        return true;
    }
}
