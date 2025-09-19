package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.runtime.aspect.AspectHandler;

import java.lang.reflect.Type;

/**
 * Aspect interface for {@link SimpleApp}, extends {@link AspectHandler}.
 *
 * @author sunqian
 */
public interface SimpleAppAspect extends AspectHandler {

    /**
     * Returns whether the aspect-oriented programming should be applied to the given type.
     *
     * @param type the given type
     * @return whether the aspect-oriented programming should be applied to the given type
     */
    boolean needsAspect(@Nonnull Type type);
}
