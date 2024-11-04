package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * Declares the annotated element is an output parameter which can be modified.
 *
 * @author fredsuvn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.PARAMETER,
})
public @interface OutParam {
}