package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;

import java.util.List;
import java.util.Map;

/**
 * The abstract class for implementing {@link JsonData}.
 * <p>
 * This method doesn't override the {@link #type()}, and all {@code asXxx()} methods throw the
 * {@link JsonDataException}. So it only needs to implement {@link #type()} and corresponding {@code asXxx()} method for
 * a JSON type.
 *
 * @author sunqian
 */
public abstract class AbstractJsonData implements JsonData {

    @Override
    public @Nonnull String asString() throws JsonDataException {
        throw new JsonDataException("The current type is not JSON string: " + type().name() + ".");
    }

    @Override
    public @Nonnull Map<String, Object> asObject() throws JsonDataException {
        throw new JsonDataException("The current type is not JSON object: " + type().name() + ".");
    }

    @Override
    public @Nonnull List<Object> asArray() throws JsonDataException {
        throw new JsonDataException("The current type is not JSON array: " + type().name() + ".");
    }

    @Override
    public @Nonnull Number asNumber() throws JsonDataException {
        throw new JsonDataException("The current type is not JSON number: " + type().name() + ".");
    }

    @Override
    public boolean asBoolean() throws JsonDataException {
        throw new JsonDataException("The current type is not JSON boolean: " + type().name() + ".");
    }

    @Override
    public @Nonnull JsonNull asNUll() throws JsonDataException {
        throw new JsonDataException("The current type is not JSON null: " + type().name() + ".");
    }
}
