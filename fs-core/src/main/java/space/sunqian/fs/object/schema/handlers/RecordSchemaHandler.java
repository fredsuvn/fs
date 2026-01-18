package space.sunqian.fs.object.schema.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.schema.ObjectParser;

import java.lang.reflect.Type;

/**
 * This implementation of {@link ObjectParser.Handler} is used to parse {@code record} classes, and it is automatically
 * loaded if the current JVM version supports {@code record} classes (typically JVM version 16 or higher).
 * <p>
 * An instance {@link #INSTANCE} is provided for convenience and less memory usage.
 *
 * @author sunqian
 */
public class RecordSchemaHandler implements ObjectParser.Handler {

    /**
     * An instance of this handler.
     */
    public static final @Nonnull RecordSchemaHandler INSTANCE = new RecordSchemaHandler();

    @Override
    public boolean parse(ObjectParser.@Nonnull Context context) throws Exception {
        Type type = context.parsedType();
        //Class<?>
        return false;
    }
}
