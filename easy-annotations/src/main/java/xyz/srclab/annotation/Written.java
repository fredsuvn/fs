package xyz.srclab.annotation;

import java.lang.annotation.*;

/**
 * Represents the parameter would be write.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.PARAMETER,
})
public @interface Written {
}
