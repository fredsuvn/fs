package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * This annotation is used on method parameters to indicate that the parameter will be used directly for a long time or
 * held by its return value, any modification for this parameter may affect the behavior of the method or its return
 * value.
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
