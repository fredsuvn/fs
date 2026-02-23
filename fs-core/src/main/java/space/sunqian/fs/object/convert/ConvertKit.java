package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.number.NumFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.value.SimpleKey;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;

import java.time.ZoneId;

/**
 * Utilities for object conversion.
 *
 * @author sunqian
 */
public class ConvertKit {

    /**
     * Returns the {@link MapParser} for default object conversion. It is same as {@link MapParser#defaultParser()},
     *
     * @return the default {@link MapParser} for object conversion
     */
    public static @Nonnull MapParser mapParser() {
        return MapParser.defaultCachedParser();
    }

    /**
     * Returns the {@link ObjectParser} for default object conversion. It is same as
     * {@link ObjectParser#defaultParser()},
     *
     * @return the default {@link ObjectParser} for object conversion
     */
    public static @Nonnull ObjectParser objectParser() {
        return ObjectParser.defaultCachedParser();
    }

    /**
     * Returns the {@link BuilderProvider} for default object conversion. It is same as
     * {@link BuilderProvider#defaultProvider()},
     *
     * @return the default {@link BuilderProvider} for object conversion
     */
    public static @Nonnull BuilderProvider builderProvider() {
        return BuilderProvider.defaultCachedProvider();
    }

    /**
     * Returns a {@link Option} of {@link DateFormatter} for the given pattern and zone id. This method is based on a
     * soft-reference cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned
     * for the same pattern and zone id.
     *
     * @param pattern the pattern of the date formatter
     * @param zoneId  the zone id of the date formatter
     * @return the {@link Option} of {@link DateFormatter} for the given pattern and zone id
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter> getDateFormatterOption(
        @Nonnull String pattern, @Nonnull ZoneId zoneId) {
        return DateFormatterCache.INST.get(pattern, zoneId);
    }

    /**
     * Returns a {@link Option} of {@link NumFormatter} for the given pattern. This method is based on a soft-reference
     * cache (from {@link SimpleCache#ofSoft()}), so the same {@link Option} instance could be returned for the same
     * pattern.
     *
     * @param pattern the pattern of the number formatter
     * @return the {@link Option} of {@link NumFormatter} for the given pattern
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter> getNumFormatterOption(
        @Nonnull String pattern) {
        return NumFormatterCache.INST.get(pattern);
    }

    private enum DateFormatterCache {
        INST;

        private final @Nonnull SimpleCache<
            @Nonnull SimpleKey,
            @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter>
            > cache = SimpleCache.ofSoft();

        public @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter> get(
            @Nonnull String pattern, @Nonnull ZoneId zoneId
        ) {
            return cache.get(
                SimpleKey.of(pattern, zoneId),
                k -> ConvertOption.dateFormatter(DateFormatter.ofPattern(k.getAs(0), k.getAs(1))
                )
            );
        }
    }

    private enum NumFormatterCache {
        INST;

        private final @Nonnull SimpleCache<
            @Nonnull String,
            @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter>
            > cache = SimpleCache.ofSoft();

        public @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter> get(@Nonnull String pattern) {
            return cache.get(
                pattern,
                p -> ConvertOption.numFormatter(NumFormatter.ofPattern(p))
            );
        }
    }

    private ConvertKit() {
    }
}
