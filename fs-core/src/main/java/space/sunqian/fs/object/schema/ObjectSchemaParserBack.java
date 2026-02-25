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

final class ObjectSchemaParserBack {

    static @Nonnull ObjectSchemaParser defaultParser() {
        return ObjectSchemaParserImpl.DEFAULT;
    }


    static @Nonnull ObjectSchemaParser defaultCachedParser() {
        return CachedObjectSchemaParser.DEFAULT;
    }

    static @Nonnull ObjectSchemaParser newParser(
        @Nonnull @RetainedParam List<ObjectSchemaParser.@Nonnull Handler> handlers
    ) {
        return new ObjectSchemaParserImpl(handlers);
    }

    static @Nonnull ObjectSchemaParserBack.CachedObjectSchemaParser newCachedParser(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache,
        @Nonnull ObjectSchemaParser parser
    ) {
        return new CachedObjectSchemaParser(cache, parser);
    }

    private static final class ObjectSchemaParserImpl implements ObjectSchemaParser, ObjectSchemaParser.Handler {

        private static final @Nonnull ObjectSchemaParserBack.ObjectSchemaParserImpl DEFAULT = new ObjectSchemaParserImpl(FsLoader.loadInstances(
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

        private ObjectSchemaParserImpl(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
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

    private static final class CachedObjectSchemaParser implements ObjectSchemaParser {

        private static final @Nonnull ObjectSchemaParserBack.CachedObjectSchemaParser DEFAULT = newCachedParser(
            SimpleCache.ofSoft(),
            ObjectSchemaParser.defaultParser()
        );

        private final @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache;
        private final @Nonnull ObjectSchemaParser parser;

        private CachedObjectSchemaParser(
            @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectSchema> cache,
            @Nonnull ObjectSchemaParser parser
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

    private ObjectSchemaParserBack() {
    }
}
