package javax.annotation;

import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotated element must not be null.
 * <p>
 * Annotated fields must not be null after construction has completed.
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 */
@Documented
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {

    /**
     * The contexts that this annotation is applicable to.
     *
     * @return the contexts that this annotation is applicable to
     */
    When when() default When.ALWAYS;

    /**
     * A validator for {@link Nonnull} annotations.
     */
    class Checker implements TypeQualifierValidator<Nonnull> {

        /**
         * Checks whether the given value is valid for the given qualifier.
         *
         * @param qualifierArgument the qualifier to check
         * @param value             the value to check
         * @return the validity of the value
         */
        public @Nonnull When forConstantValue(@Nonnull Nonnull qualifierArgument, Object value) {
            if (value == null)
                return When.NEVER;
            return When.ALWAYS;
        }
    }
}
