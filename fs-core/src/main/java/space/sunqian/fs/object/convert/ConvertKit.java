package space.sunqian.fs.object.convert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.number.NumFormatter;
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
     * Returns the {@link DateFormatter} for the given pattern and zone id. This method is based on a soft-reference
     * cache (from {@link SimpleCache#ofSoft()}), so the same {@link DateFormatter} instance will be returned for the
     * same pattern and zone id.
     *
     * @param pattern the pattern of the date formatter
     * @param zoneId  the zone id of the date formatter
     * @return the {@link DateFormatter} for the given pattern and zone id
     */
    public static @Nonnull DateFormatter getDateFormatter(@Nonnull String pattern, @Nonnull ZoneId zoneId) {
        return DateFormatterCache.INST.get(pattern, zoneId);
    }

    /**
     * Returns the {@link NumFormatter} for the given pattern. This method is based on a soft-reference cache (from
     * {@link SimpleCache#ofSoft()}), so the same {@link NumFormatter} instance will be returned for the same pattern.
     *
     * @param pattern the pattern of the number formatter
     * @return the {@link NumFormatter} for the given pattern
     */
    public static @Nonnull NumFormatter getNumFormatter(@Nonnull String pattern) {
        return NumFormatterCache.INST.get(pattern);
    }

    private enum DateFormatterCache {
        INST;

        private final @Nonnull SimpleCache<@Nonnull DateFormatterKey, @Nonnull DateFormatter> cache =
            SimpleCache.ofSoft();

        public @Nonnull DateFormatter get(@Nonnull String pattern, @Nonnull ZoneId zoneId) {
            return cache.get(
                new DateFormatterKey(pattern, zoneId),
                key -> DateFormatter.ofPattern(pattern, zoneId)
            );
        }

        @Data
        @EqualsAndHashCode(callSuper = false)
        private static final class DateFormatterKey {
            private final String pattern;
            private final ZoneId zoneId;
        }
    }

    private enum NumFormatterCache {
        INST;

        private final @Nonnull SimpleCache<@Nonnull String, @Nonnull NumFormatter> cache =
            SimpleCache.ofSoft();

        public @Nonnull NumFormatter get(@Nonnull String pattern) {
            return cache.get(
                pattern,
                p -> NumFormatter.ofPattern(pattern)
            );
        }
    }

    private ConvertKit() {
    }
}
