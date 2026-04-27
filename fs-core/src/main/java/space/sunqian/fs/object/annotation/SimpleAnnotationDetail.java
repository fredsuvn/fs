package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;

import java.lang.annotation.Annotation;

/**
 * Simple annotation detail to wrap the given annotation type.
 *
 * @param <T> the type of the annotation instance
 * @author sunqian
 */
public class SimpleAnnotationDetail<T extends Annotation> implements AnnotationDetail<T> {

    private final @Nonnull T annotation;

    /**
     * Constructs with the specified annotation instance.
     *
     * @param annotation the specified annotation instance
     */
    public SimpleAnnotationDetail(@Nonnull T annotation) {
        this.annotation = annotation;
    }

    @Override
    public @Nonnull T annotation() {
        return annotation;
    }
}
