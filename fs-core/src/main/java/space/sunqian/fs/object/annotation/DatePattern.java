package space.sunqian.fs.object.annotation;

import space.sunqian.fs.base.date.DateKit;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.ObjectCopier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.ZoneId;

/**
 * This annotation is used to specify the date pattern to be used:
 * <pre>{@code
 * @DatePattern("yyyy-MM-dd HH:mm:ss")
 * private String date;
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
public @interface DatePattern {

    /**
     * The date pattern to be used, default is {@link DateKit#DEFAULT_PATTERN}.
     *
     * @return the date pattern to be used, default is {@link DateKit#DEFAULT_PATTERN}
     */
    String value() default DateKit.DEFAULT_PATTERN;

    /**
     * The zone id to be used, default is {@link ZoneId#systemDefault()}.
     *
     * @return the zone id to be used, default is {@link ZoneId#systemDefault()}
     */
    String zoneId() default "";
}
