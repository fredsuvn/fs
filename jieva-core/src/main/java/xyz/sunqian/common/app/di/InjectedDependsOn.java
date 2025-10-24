package xyz.sunqian.common.app.di;

import xyz.sunqian.annotations.Nonnull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used for post-construct or pre-destroy methods, to declare dependent resources specified by
 * {@link #value()}. The current DI system ensures that post-construct methods will be executed after their dependent
 * resources have been initialized, and pre-destroy methods will be executed before their dependent resources have been
 * destroyed.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
})
public @interface InjectedDependsOn {

    /**
     * The classes of the dependent resources.
     *
     * @return classes of the dependent resources
     */
    @Nonnull Class<?> @Nonnull [] value();
}
