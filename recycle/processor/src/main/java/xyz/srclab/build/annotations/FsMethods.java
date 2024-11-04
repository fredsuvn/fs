package xyz.srclab.build.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks all public static methods of this class will be created shortcut methods in the class Fs in compile processing.
 *
 * @author fredsuvn
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface FsMethods {
}
