package space.sunqian.fs.object.schema.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.lang.FsLoader;
import space.sunqian.fs.object.schema.ObjectParser;

/**
 * This implementation of {@link ObjectParser.Handler} is used to parse {@code record} classes, and it is automatically
 * loaded if the current JVM version supports {@code record} classes (typically JVM version 16 or higher).
 * <p>
 * Using {@link #getInstance()} method can obtain the same instance.
 *
 * @author sunqian
 */
public class RecordSchemaHandler implements ObjectParser.Handler {

    private static final @Nonnull RecordSchemaHandler INST = new RecordSchemaHandler();

    private static final ObjectParser.@Nonnull Handler HANDLER = FsLoader.loadImplByJvm(
        RecordSchemaHandler.class, 16
    );

    /**
     * Returns an instance of this handler. This method always returns the same instance.
     *
     * @return an instance of this handler
     */
    public static @Nonnull RecordSchemaHandler getInstance() {
        return INST;
    }

    @Override
    public boolean parse(ObjectParser.@Nonnull Context context) throws Exception {
        return HANDLER.parse(context);
    }
}
