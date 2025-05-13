package xyz.sunqian.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the annotated element must be non-null.
 *
 * @author sunqian
 */
@Documented
@javax.annotation.Nonnull
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_USE,
})
public @interface Nonnull {
}