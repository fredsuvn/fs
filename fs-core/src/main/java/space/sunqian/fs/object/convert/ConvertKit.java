package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
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
     * Returns the default {@link ObjectParser} for object conversion. The returned object is same one and based on
     * {@link ObjectParser#defaultParser()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link ObjectParser}
     */
    public static @Nonnull ObjectParser objectParser() {
        return CachedObjectParser.INST;
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
