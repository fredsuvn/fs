package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * This interface is used to map a JSON property to another object when formatting JSON data.
 *
 * @author sunqian
 */
public interface JsonPropertyMapper {

    /**
     * Maps a JSON property to another object when formatting JSON data.
     *
     * @param name  the specified name of the JSON property
     * @param value the value of the JSON property, may be {@code null}
     * @param type  the java type of the JSON property, if the type of value cannot be determined, it should be
     *              {@code Object.class}
     * @return the mapped object
     */
    @Nullable
    Object map(@Nonnull String name, @Nullable Object value, @Nonnull Type type);
}
