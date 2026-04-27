package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.CachedResult;
import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * Represents a collection of annotations along with their detailed information.
 *
 * @author sunqian
 */
public interface AnnotationSet {

    /**
     * Returns an instance of {@link AnnotationSet} from the given {@link AnnotatedElement}.
     *
     * @param annotatedElement the given {@link AnnotatedElement}
     * @return an instance of {@link AnnotationSet} from the given {@link AnnotatedElement}
     */
    @CachedResult
    static @Nonnull AnnotationSet from(@Nonnull AnnotatedElement annotatedElement) {
        return AnnotationBack.Cache.get(annotatedElement, AnnotationSet::newSet);
    }

    /**
     * Returns a new {@link AnnotationSet} from the given {@link AnnotatedElement}.
     *
     * @param annotatedElement the given {@link AnnotatedElement}
     * @return a new {@link AnnotationSet} from the given {@link AnnotatedElement}
     */
    static @Nonnull AnnotationSet newSet(@Nonnull AnnotatedElement annotatedElement) {
        return AnnotationBack.newSet(annotatedElement);
    }

    /**
     * Returns all annotation instances contained in this collection.
     *
     * @return an immutable list containing all annotation instances in this collection
     */
    @Nonnull
    @Immutable
    List<@Nonnull Annotation> annotations();

    /**
     * Returns the annotation instance of the specified type from this collection.
     * <p>
     * If the annotation of the specified type is not present in this collection, returns {@code null}.
     *
     * @param <T>             the annotation type
     * @param annotationClass the class object representing the annotation type to retrieve
     * @return the annotation instance of the specified type, or {@code null} if not present
     */
    <T extends Annotation> @Nullable T get(@Nonnull Class<T> annotationClass);

    /**
     * Returns all annotation detail objects contained in this collection.
     *
     * @return an immutable list containing all annotation detail objects in this collection
     */
    @Nonnull
    @Immutable
    List<@Nonnull AnnotationDetail<?>> details();

    /**
     * Returns the annotation detail object of the specified type from this collection.
     * <p>
     * If the annotation detail of the specified type is not present in this collection, returns {@code null}.
     *
     * @param <D>         the annotation detail type
     * @param detailClass the class object representing the annotation detail type to retrieve
     * @return the annotation detail object of the specified type, or {@code null} if not present
     */
    <D extends AnnotationDetail<?>> @Nullable D getDetail(@Nonnull Class<D> detailClass);

    /**
     * Returns the annotation detail object of the specified annotation type from this collection.
     * <p>
     * If the annotation detail of the specified annotation type is not present in this collection, returns
     * {@code null}.
     *
     * @param <T>             the annotation type
     * @param <D>             the annotation detail type
     * @param annotationClass the class object representing the annotation type to retrieve
     * @return the annotation detail object of the specified annotation type, or {@code null} if not present
     */
    <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D getDetailByAnnotationType(
        @Nonnull Class<T> annotationClass
    );
}