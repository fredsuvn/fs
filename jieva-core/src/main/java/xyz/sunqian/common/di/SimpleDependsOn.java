package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the annotated post-construct or pre-destroy methods depends on the resources specified by
 * {@link #value()}, and the DI system ensures that the post-construct or pre-destroy methods of the dependent resource
 * is executed before they are executed.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
})
public @interface SimpleDependsOn {

    /**
     * The classes of the dependency.
     *
     * @return the classes of the dependency.
     */
    @Nonnull Class<?> @Nonnull [] value();
}
