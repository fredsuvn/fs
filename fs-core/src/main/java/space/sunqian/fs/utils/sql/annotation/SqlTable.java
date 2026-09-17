package space.sunqian.fs.utils.sql.annotation;

import space.sunqian.fs.utils.sql.SqlNameMapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to map a table in the database to a class in the Java object:
 * <pre>{@code
 * @SqlTable("T_USER")
 * public class User{
 *     ...
 * }
 * }</pre>
 *
 * @author sunqian
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
})
public @interface SqlTable {

    /**
     * The table name to be mapped, default is empty, which means using the default policy to map. The actual policy
     * depends on the actual executor, the system default policy is as in {@link SqlNameMapper#defaultMapper()}.
     *
     * @return the table name to be mapped
     */
    String value() default "";
}
