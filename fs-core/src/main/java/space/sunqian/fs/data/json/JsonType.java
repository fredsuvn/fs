package space.sunqian.fs.data.json;

/**
 * Represents the type of JSON data.
 *
 * @author sunqian
 */
public enum JsonType {

    /**
     * Represents the type of JSON object.
     */
    OBJECT,
    /**
     * Represents the type of JSON array.
     */
    ARRAY,
    /**
     * Represents the type of JSON string.
     */
    STRING,
    /**
     * Represents the type of JSON number.
     */
    NUMBER,
    /**
     * Represents the type of JSON boolean.
     */
    BOOLEAN,
    /**
     * Represents the type of JSON null. Note that the only one java type can represent JSON null is {@link JsonNull}.
     */
    NULL,
}
