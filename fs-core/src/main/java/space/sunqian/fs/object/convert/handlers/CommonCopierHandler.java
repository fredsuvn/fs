package space.sunqian.fs.object.convert.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectCopier;

/**
 * The common implementation of {@link ObjectCopier.Handler}, also be the default last handler of
 * {@link ObjectCopier#defaultCopier()}. Using {@link #getInstance()} can get a same one instance of this handler.
 * <p>
 * This handler uses the default implementations of {@link ObjectCopier.Handler}, which directly copies properties from
 * the source object to the target object, following the rules of the specified options defined in
 * {@link ConvertOption}.
 *
 * @author sunqian
 */
public class CommonCopierHandler implements ObjectCopier.Handler {

    private static final @Nonnull CommonCopierHandler INST = new CommonCopierHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull CommonCopierHandler getInstance() {
        return INST;
    }
}
