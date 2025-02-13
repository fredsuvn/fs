package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * This annotation is used to indicate that the annotated method has a low performance overhead and returns quickly. It
 * implies that the method is likely to be lightweight.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
})
public @interface FastReturn {
}
