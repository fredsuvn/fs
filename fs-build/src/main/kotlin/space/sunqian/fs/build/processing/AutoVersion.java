package space.sunqian.fs.build.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the annotated field as auto version.
 *
 * @author sunqian
 */
//@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({
    ElementType.FIELD,
})
public @interface AutoVersion {
}