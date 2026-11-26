package space.sunqian.fs.utils.sql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Java bean that can be mapped to a SQL table or view.
 * <p>
 * The following example marks a class to be mapped to a SQL table "T_USER":
 * <pre>{@code
 * @SqlTable("T_USER")
 * public class User {
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
     * The table/view name to be mapped.
     * <p>
     * The default is empty, which means the table/view name is derived from the default naming policy.
     *
     * @return the table/view name to be mapped
     */
    String value() default "";
}
