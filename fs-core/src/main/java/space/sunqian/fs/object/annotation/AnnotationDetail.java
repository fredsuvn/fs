package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.lang.annotation.Annotation;

/**
 * Represents the detail info for an annotation, including the original annotation instance and parsed objects from the
 * annotation attributes. Here are the detail classes and its original annotation instance:
 * <table>
 *     <tr>
 *         <th>Detail Class</th>
 *         <th>Original Annotation</th>
 *     </tr>
 *     <tr>
 *         <td>{@link DatePatternDetail}</td>
 *         <td>{@link DatePattern}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link NumberPatternDetail}</td>
 *         <td>{@link NumberPattern}</td>
 *     </tr>
 * </table>
 *
 * @param <T> the type of the annotation instance
 * @author sunqian
 */
public interface AnnotationDetail<T extends Annotation> {

    /**
     * Returns a new instance of {@link AnnotationDetail} wraps the given annotation.
     * <p>
     * The following annotation types can be wrapped by the specific detail type:
     * <table>
     *     <tr>
     *         <th>Annotation Type</th>
     *         <th>Specific Detail Type</th>
     *     </tr>
     *     <tr>
     *         <td>{@link DatePattern}</td>
     *         <td>{@link DatePatternDetail}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link NumberPattern}</td>
     *         <td>{@link NumberPatternDetail}</td>
     *     </tr>
     * </table>
     * Other annotation types will be wrapped by {@link SimpleAnnotationDetail}.
     *
     * @param annotation the given annotation
     * @param <T>        the type of the given annotation
     * @return a new instance of {@link AnnotationDetail} wraps the given annotation
     */
    static <T extends Annotation> @Nonnull AnnotationDetail<T> newDetail(@Nonnull T annotation) {
        if (annotation.annotationType().equals(DatePattern.class)) {
            return Fs.as(new DatePatternDetail((DatePattern) annotation));
        }
        if (annotation.annotationType().equals(NumberPattern.class)) {
            return Fs.as(new NumberPatternDetail((NumberPattern) annotation));
        }
        return new SimpleAnnotationDetail<>(annotation);
    }

    /**
     * Returns the original annotation instance.
     *
     * @return the original annotation instance
     */
    @Nonnull
    T annotation();
}
