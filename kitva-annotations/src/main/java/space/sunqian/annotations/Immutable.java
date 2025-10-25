package space.sunqian.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the annotated element is immutable.
 *
 * @author sunqian
 */
@Documented
@javax.annotation.concurrent.Immutable
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface Immutable {
}