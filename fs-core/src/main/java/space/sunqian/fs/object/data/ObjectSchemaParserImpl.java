package space.sunqian.fs.object.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.object.data.handlers.SimpleBeanSchemaHandler;
import space.sunqian.fs.third.ThirdKit;

import java.util.List;

final class ObjectSchemaParserImpl implements ObjectSchemaParser, ObjectSchemaParser.Handler {

    static @Nonnull ObjectSchemaParserImpl DEFAULT = new ObjectSchemaParserImpl(FsLoader.loadInstances(
        FsLoader.loadClassByDependent(
            ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler"),
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
