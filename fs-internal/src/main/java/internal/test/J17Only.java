package internal.test;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks the test only runs on JDK17.
 *
 * @author sunqian
 */
@Tag("J17Only")
@Retention(RetentionPolicy.RUNTIME)
public @interface J17Only {
}
