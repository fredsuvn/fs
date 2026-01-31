package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.data.DataParser;

import java.util.Properties;

/**
 * Represents the parser that parses properties data a {@code PropertiesData} object.
 *
 * @author sunqian
 */
public interface PropertiesParser extends DataParser<PropertiesData> {

    /**
     * Returns the default {@link PropertiesParser}.
     *
     * @return the default {@link PropertiesParser}
     */
    static @Nonnull PropertiesParser defaultParser() {
        return PropertiesParserImpl.INST;
    }

    /**
     * Wraps the given {@link Properties} object into a {@link PropertiesData} object.
     *
     * @param properties the given {@link Properties} object
     * @return the wrapped {@link PropertiesData} object
     */
    default @Nonnull PropertiesData wrap(@Nonnull Properties properties) {
        return () -> properties;
    }
}
