package space.sunqian.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares all methods, fields, parameters, local variables, type parameters, and type uses under the scope of the
 * annotated element is nullable.
 * <p>
 * It is equivalent to use {@link Nullable} on all those points.
 *
 * @author sunqian
 */
@Documented
@Nonnull(
    when = When.MAYBE
)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
@Target({
    ElementType.TYPE,
    ElementType.METHOD,
    ElementType.CONSTRUCTOR,
    ElementType.PACKAGE,
})
@TypeQualifierDefault({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface DefaultNullable {
}