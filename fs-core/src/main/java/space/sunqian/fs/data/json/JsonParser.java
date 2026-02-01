package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.data.DataParser;

import java.util.List;

/**
 * Represents the parser that parses properties data a {@code PropertiesData} object.
 *
 * @author sunqian
 */
public interface JsonParser extends DataParser<JsonData> {

    /**
     * Returns the default {@link JsonParser}.
     *
     * @return the default {@link JsonParser}
     */
    static @Nonnull JsonParser defaultParser() {
        return JsonParserImpl.DEFAULT;
    }

    // /**
    //  * Parses the given object into a {@link JsonData} object.
    //  * <p>
    //  * If the given object is a string, it will be parsed as a JSON string; if the given object is a number, it will be
    //  * parsed as a JSON number; if the given object is a boolean, it will be parsed as a JSON boolean; if the given
    //  * object is a {@link List} or array, it will be parsed as a JSON array; if the given object is a {@link JsonNull},
    //  * it will be parsed as a JSON null; otherwise, it will be parsed as a JSON object.
    //  *
    //  * @param object the given object
    //  * @return the wrapped object
    //  */
    // @Nonnull
    // JsonData parse(@Nonnull Object object);
}
