package space.sunqian.fs.object.meta.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.object.meta.ObjectMetaManager;

/**
 * This implementation of {@link ObjectMetaManager.Handler} is used to parse {@code record} classes, and it is
 * automatically loaded if the current JVM version supports {@code record} classes (typically JVM version 16 or
 * higher).
 * <p>
 * Using {@link #getInstance()} method can obtain the same instance.
 *
 * @author sunqian
 */
public class RecordMetaHandler implements ObjectMetaManager.Handler {

    private static final @Nonnull RecordMetaHandler INST = new RecordMetaHandler();

    private static final ObjectMetaManager.@Nonnull Handler HANDLER = FsLoader.loadImplByJvm(
        RecordMetaHandler.class, 16
    );

    /**
     * Returns an instance of this handler. This method always returns the same instance.
     *
     * @return an instance of this handler
     */
    public static @Nonnull RecordMetaHandler getInstance() {
        return INST;
    }

    @Override
    public boolean parse(ObjectMetaManager.@Nonnull Context context) throws Exception {
        return HANDLER.parse(context);
    }
}
