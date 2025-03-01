package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * This annotation indicates that the parameter will be modified by the current method.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.PARAMETER,
})
public @interface OutParam {
}