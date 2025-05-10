package xyz.sunqian.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    ElementType.TYPE_USE,
})
public @interface RetainedParam {
}
