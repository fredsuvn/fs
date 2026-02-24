package space.sunqian.fs.object.annotation;

import space.sunqian.fs.base.number.NumKit;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the number pattern to be used:
 * <pre>{@code
 * @NumPattern("#.000")
 * private BigDecimal number;
 * }</pre>
 * It is typically available for the default {@link ObjectConverter} and {@link ObjectCopier} and their handlers when
 * converting to a non-map object.
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_USE,
})
public @interface NumPattern {

    /**
     * The number pattern to be used, default is {@link NumKit#DEFAULT_PATTERN}.
     *
     * @return the number pattern to be used, default is {@link NumKit#DEFAULT_PATTERN}
     */
    String value() default NumKit.DEFAULT_PATTERN;
}
