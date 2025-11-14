package space.sunqian.common.object.data;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.common.FsLoader;
import space.sunqian.common.object.data.handlers.SimpleBeanSchemaHandler;

import java.util.List;

final class ObjectSchemaParserImpl implements ObjectSchemaParser, ObjectSchemaParser.Handler {

    static @Nonnull ObjectSchemaParserImpl DEFAULT = new ObjectSchemaParserImpl(FsLoader.loadInstances(
        FsLoader.loadClassByDependent(
            "space.sunqian.common.third.protobuf.ProtobufSchemaHandler",
            "com.google.protobuf.Message"
        ),
        SimpleBeanSchemaHandler.class
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
