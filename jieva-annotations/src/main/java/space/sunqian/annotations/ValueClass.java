package space.sunqian.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the annotated class is a value class in the supported JVM.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
})
public @interface ValueClass {
}