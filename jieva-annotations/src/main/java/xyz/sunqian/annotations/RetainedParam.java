package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the parameter will be used and held directly, any modification for this parameter may
 * affect the behavior of the method or its return value.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.PARAMETER,
})
public @interface RetainedParam {
}
