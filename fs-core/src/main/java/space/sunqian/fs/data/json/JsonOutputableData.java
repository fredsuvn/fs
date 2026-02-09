package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.data.OutputableData;
import space.sunqian.fs.io.IOKit;

/**
 * This interface represents a JSON data, which can be formatted to a string.
 *
 * @author sunqian
 */
public interface JsonOutputableData extends OutputableData {

    /**
     * Formats and returns the string representation of the current JSON data.
     *
     * @return the formatted string representation of the current JSON data
     * @throws JsonDataException if the current JSON data is not a valid JSON
     */
    default @Nonnull String toJsonString() throws JsonDataException {
        StringBuilder sb = new StringBuilder();
        writeTo(IOKit.newOutputStream(sb));
        return sb.toString();
    }
}
