package xyz.sunqian.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the annotated method has different implementations based on different JDKs.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD, ElementType.TYPE
})
public @interface JdkDependent {

    /**
     * Returns the JDK version.
     *
     * @return the JDK version
     */
    String value() default "JDK8";
}
