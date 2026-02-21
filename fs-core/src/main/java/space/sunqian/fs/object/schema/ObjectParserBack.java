package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.schema.handlers.CommonSchemaHandler;
import space.sunqian.fs.object.schema.handlers.RecordSchemaHandler;
import space.sunqian.fs.third.ThirdKit;

import java.lang.reflect.Type;
import java.util.List;

final class ObjectParserBack {

    static @Nonnull ObjectParser defaultParser() {
        return ObjectParserImpl.DEFAULT;
    }


    static @Nonnull ObjectParser defaultCachedParser() {
        return CachedObjectParser.DEFAULT;
    }

    static @Nonnull ObjectParser newParser(
        @Nonnull @RetainedParam List<ObjectParser.@Nonnull Handler> handlers
    ) {
        return new ObjectParserImpl(handlers);
    }

    static @Nonnull CachedObjectParser newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache,
        @Nonnull ObjectParser parser
    ) {
        return new CachedObjectParser(cache, parser);
    }

    private static final class ObjectParserImpl implements ObjectParser, ObjectParser.Handler {

        private static final @Nonnull ObjectParserImpl DEFAULT = new ObjectParserImpl(FsLoader.loadInstances(
            FsLoader.loadClassByDependent(
                ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler"),
                "com.google.protobuf.Message"
            ),
            FsLoader.supplyByDependent(
                RecordSchemaHandler::getInstance, RecordSchemaHandler.class.getName() + "ImplByJ16"
            ),
            CommonSchemaHandler.getInstance()
        ));

        private final @Nonnull List<@Nonnull Handler> handlers;

        private ObjectParserImpl(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
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

    private static final class CachedObjectParser implements ObjectParser {

        private static final @Nonnull CachedObjectParser DEFAULT = newCachedParser(
            SimpleCache.ofSoft(),
            ObjectParser.defaultParser()
        );

        private final @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache;
        private final @Nonnull ObjectParser parser;

        private CachedObjectParser(
            @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache,
            @Nonnull ObjectParser parser
        ) {
            this.cache = cache;
            this.parser = parser;
        }

        @Override
        public @Nonnull ObjectSchema parse(@Nonnull Type type) throws DataSchemaException {
            return cache.get(type, parser::parse);
        }

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return parser.handlers();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return parser.asHandler();
        }
    }

    private ObjectParserBack() {
    }
}
