package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.create.CreatorProvider;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;

/**
 * Utilities for object conversion.
 *
 * @author sunqian
 */
public class ConvertKit {

    private static final @Nonnull MapParser MAP_PARSER = MapParser.cachedParser(
        SimpleCache.ofSoft(),
        MapParser.defaultParser()
    );

    private static final @Nonnull ObjectParser OBJECT_PARSER = ObjectParser.cachedParser(
        SimpleCache.ofSoft(),
        ObjectParser.defaultParser()
    );

    private static final @Nonnull CreatorProvider CREATOR_PROVIDER = CreatorProvider.cachedProvider(
        SimpleCache.ofSoft(),
        CreatorProvider.defaultProvider()
    );

    /**
     * Returns the default {@link MapParser} for object conversion. The returned object is same one and based on
     * {@link MapParser#defaultParser()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link MapParser}
     */
    public static @Nonnull MapParser mapParser() {
        return MAP_PARSER;
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
        return Fs.nonnull(Option.findValue(ConvertOption.MAP_SCHEMA_PARSER, options), mapParser());
    }

    /**
     * Returns the default {@link ObjectParser} for object conversion. The returned object is same one and based on
     * {@link ObjectParser#defaultParser()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link ObjectParser}
     */
    public static @Nonnull ObjectParser objectParser() {
        return OBJECT_PARSER;
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
        return Fs.nonnull(Option.findValue(ConvertOption.OBJECT_SCHEMA_PARSER, options), objectParser());
    }

    /**
     * Returns the default {@link CreatorProvider} for object conversion. The returned object is same one and based on
     * {@link CreatorProvider#defaultProvider()}, and its results are cached with {@link SimpleCache#ofSoft()}.
     *
     * @return the default {@link CreatorProvider} with caching for object creation
     */
    public static @Nonnull CreatorProvider creatorProvider() {
        return CREATOR_PROVIDER;
    }

    /**
     * Returns the specified {@link CreatorProvider} in the given options, or {@link #creatorProvider()} if the given
     * options does not contain a {@link ConvertOption#CREATOR_PROVIDER}.
     *
     * @param options the given options
     * @return the specified {@link CreatorProvider} in the given options, or {@link #creatorProvider()} if the given
     * options does not contain a {@link ConvertOption#CREATOR_PROVIDER}
     */
    public static @Nonnull CreatorProvider creatorProvider(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(Option.findValue(ConvertOption.CREATOR_PROVIDER, options), creatorProvider());
    }

    private ConvertKit() {
    }
}
