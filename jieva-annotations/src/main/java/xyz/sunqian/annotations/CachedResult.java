package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the return value of annotated method has already been cached using an appropriate
 * caching strategy, and typically does not require additional caching.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
})
public @interface CachedResult {
}
