package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.MapSchema;
import space.sunqian.fs.object.schema.ObjectParser;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Utilities for object conversion.
 *
 * @author sunqian
 */
public class ConvertKit {

    /**
     * Returns the default {@link MapParser} for object conversion. The returned object is same one and based on
     * {@link MapParser#defaultParser()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link MapParser}
     */
    public static @Nonnull MapParser mapParser() {
        return CachedMapParser.INST;
    }

    /**
     * Returns the specified {@link MapParser} in the given options, or {@link #mapParser()} if the given options does
     * not contain a {@link ConvertOption#MAP_SCHEMA_PARSER}.
     *
     * @param options the given options
     * @return the specified {@link MapParser} in the given options, or {@link #mapParser()} if the given options does
     * not contain a {@link ConvertOption#MAP_SCHEMA_PARSER}
     */
    public static @Nonnull MapParser mapParser(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(OptionKit.findValue(ConvertOption.MAP_SCHEMA_PARSER, options), mapParser());
    }

    /**
     * Returns the default {@link ObjectParser} for object conversion. The returned object is same one and based on
     * {@link ObjectParser#defaultParser()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link ObjectParser}
     */
    public static @Nonnull ObjectParser objectParser() {
        return CachedObjectParser.INST;
    }

    /**
     * Returns the specified {@link ObjectParser} in the given options, or {@link #objectParser()} if the given options
     * does not contain a {@link ConvertOption#OBJECT_SCHEMA_PARSER}.
     *
     * @param options the given options
     * @return the specified {@link ObjectParser} in the given options, or {@link #objectParser()} if the given options
     * does not contain a {@link ConvertOption#OBJECT_SCHEMA_PARSER}
     */
    public static @Nonnull ObjectParser objectParser(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(OptionKit.findValue(ConvertOption.OBJECT_SCHEMA_PARSER, options), objectParser());
    }

    /**
     * Returns the default {@link BuilderProvider} for object conversion. The returned object is same one and based on
     * {@link BuilderProvider#defaultProvider()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link BuilderProvider} with caching for object creation
     */
    public static @Nonnull BuilderProvider builderProvider() {
        return CachedBuilder.INST;
    }

    /**
     * Returns the specified {@link BuilderProvider} in the given options, or {@link #builderProvider()} if the given
     * options does not contain a {@link ConvertOption#BUILDER_PROVIDER}.
     *
     * @param options the given options
     * @return the specified {@link BuilderProvider} in the given options, or {@link #builderProvider()} if the given
     * options does not contain a {@link ConvertOption#BUILDER_PROVIDER}
     */
    public static @Nonnull BuilderProvider builderProvider(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(OptionKit.findValue(ConvertOption.BUILDER_PROVIDER, options), builderProvider());
    }

    private ConvertKit() {
    }

    private enum CachedMapParser implements MapParser {
        INST;

        private final @Nonnull MapParser delegate = MapParser.cachedParser(
            SimpleCache.ofSoft(),
            MapParser.defaultParser()
        );

        @Override
        public @Nonnull MapSchema parse(@Nonnull Type type) throws DataSchemaException {
            return delegate.parse(type);
        }
    }

    private enum CachedObjectParser implements ObjectParser {
        INST;

        private final @Nonnull ObjectParser delegate = ObjectParser.cachedParser(
            SimpleCache.ofSoft(),
            ObjectParser.defaultParser()
        );

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return delegate.handlers();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return delegate.asHandler();
        }
    }

    private enum CachedBuilder implements BuilderProvider {
        INST;

        private final @Nonnull BuilderProvider delegate = BuilderProvider.cachedProvider(
            SimpleCache.ofSoft(),
            BuilderProvider.defaultProvider()
        );

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return delegate.handlers();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return delegate.asHandler();
        }
    }
}
