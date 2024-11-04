package xyz.sunqian.annotations;

import java.lang.annotation.*;

/**
 * Marks current codes don't be included in unit test coverage.
 *
 * @author fredsuvn
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
    ElementType.ANNOTATION_TYPE,
    ElementType.PACKAGE,
})
public @interface ExcludeTest {

    /**
     * Comment for this mark.
     *
     * @return comment for this mark
     */
    String value() default "";
}
