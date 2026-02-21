package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;

/**
 * Utilities for object conversion.
 *
 * @author sunqian
 */
public class ConvertKit {

    /**
     * Returns the {@link MapParser} for object conversion. By default, it is same as
     * {@link MapParser#defaultParser()},
     *
     * @return the default {@link MapParser} for object conversion
     */
    public static @Nonnull MapParser mapParser() {
        return MapParser.defaultCachedParser();
    }

    /**
     * Returns the {@link ObjectParser} for object conversion. By default, it is same as
     * {@link ObjectParser#defaultParser()},
     *
     * @return the default {@link ObjectParser} for object conversion
     */
    public static @Nonnull ObjectParser objectParser() {
        return ObjectParser.defaultCachedParser();
    }

    /**
     * Returns the {@link BuilderProvider} for object conversion. By default, it is same as
     * {@link BuilderProvider#defaultProvider()},
     *
     * @return the default {@link BuilderProvider} for object conversion
     */
    public static @Nonnull BuilderProvider builderProvider() {
        return BuilderProvider.defaultCachedProvider();
    }

    private ConvertKit() {
    }
}
