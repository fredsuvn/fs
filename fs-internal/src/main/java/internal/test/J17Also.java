package internal.test;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks the test not only needs to run on JDK8, but also on JDK17.
 *
 * @author sunqian
 */
@Tag("J17Also")
@Retention(RetentionPolicy.RUNTIME)
public @interface J17Also {
}
