package space.sunqian.fs.app.di;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.dynamic.aop.AspectHandler;

import java.lang.reflect.Type;

/**
 * The aspect-oriented programming interface for {@link InjectedApp}, extends {@link AspectHandler}. See
 * {@linkplain space.sunqian.fs.app.di DI package documentation} for more information.
 *
 * @author sunqian
 */
public interface InjectedAspect extends AspectHandler {

    /**
     * Returns whether the aspect-oriented programming should be applied to the given type.
     *
     * @param type the given type
     * @return whether the aspect-oriented programming should be applied to the given type
     */
    boolean needsAspect(@Nonnull Type type);
}
// /**
//  * Enables or disables the aspect-oriented programming (AOP) functionality.
//  * <p>
//  * When enabled, this builder will first start standard dependency injection, creating one instance for each
//  * resource type. Resource objects which are instances of the {@link InjectedAspect} will be treated as aspect
//  * handlers (excluding instances from dependency apps), and the other resource instances (also excluding
//  * instances from dependency apps) will be evaluated by {@link InjectedAspect#needsAspect(Type)} of each aspect
//  * handler in an unspecified order. The evaluation stops at the first handler where the {@code needsAspect}
//  * returns {@code true} for the resource type. When a match is found, the resource instance will be advised by
//  * that handler, and no further aspect handlers will be evaluated for that type.
//  * <p>
//  * If no aspect handler matches a resource type, the resource type will not be advised. And if a resource
//  * instance is advised and replaced by the advised instance, the Post-Construct and Pre-Destroy methods (if any)
//  * will execute on the advised instance rather than the original instance.
//  *
//  * @param aspect {@code true} to enable AOP functionality, {@code false} to disable it
//  * @return this builder
//  */
// public @Nonnull Builder aspect(boolean aspect) {
//     this.enableAspect = aspect;
//     return this;
// }