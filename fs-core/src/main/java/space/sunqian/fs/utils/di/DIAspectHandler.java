package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.dynamic.aop.AspectHandler;

import java.lang.reflect.Type;

/**
 * The aspect-oriented programming interface for {@link DIContainer}, extends {@link AspectHandler}. See
 * {@linkplain space.sunqian.fs.di DI documentation} for more information.
 *
 * @author sunqian
 */
public interface DIAspectHandler extends AspectHandler {

    /**
     * Returns whether the aspect-oriented programming should be applied to the given type.
     *
     * @param type the given type
     * @return whether the aspect-oriented programming should be applied to the given type
     */
    boolean needsAspect(@Nonnull Type type);
}