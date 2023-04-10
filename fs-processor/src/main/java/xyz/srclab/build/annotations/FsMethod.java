package xyz.srclab.build.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks this static method will be created a shortcut method in the class Fs in compile processing.
 *
 * @author fredsuvn
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FsMethod {

    /**
     * The name in Fs.
     */
    String value();
}
