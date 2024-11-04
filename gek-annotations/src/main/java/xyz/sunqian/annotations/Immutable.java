package xyz.sunqian.annotations;

import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

/**
 * Declares the annotated element is immutable.
 * <p>
 * In general, this annotation only indicates that the current object is immutable. Whether the other objects it
 * references are mutable is determined by the {@link #deep()} field.
 *
 * @author fredsuvn
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

    /**
     * To specify whether the other objects it references are mutable recursively (deep-immutable).
     *
     * @return whether the other objects it references are mutable recursively (deep-immutable)
     */
    boolean deep() default false;
}