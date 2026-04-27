package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.SimpleCache;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

final class AnnotationBack {

    static @Nonnull AnnotationSet newSet(@Nonnull AnnotatedElement annotatedElement) {
        return new AnnotationSetImpl(annotatedElement);
    }

    private static final class AnnotationSetImpl implements AnnotationSet {

        private final @Nonnull Annotation @Nonnull [] annotations;
        private final @Nonnull List<@Nonnull Annotation> annotationList;
        private final @Nonnull AnnotationDetail<?> @Nonnull [] details;
        private final @Nonnull List<@Nonnull AnnotationDetail<?>> detailList;

        private AnnotationSetImpl(@Nonnull AnnotatedElement annotatedElement) {
            this.annotations = annotatedElement.getAnnotations();
            this.annotationList = Fs.list(annotations);
            this.details = new AnnotationDetail<?>[this.annotations.length];
            for (int i = 0; i < details.length; i++) {
                details[i] = AnnotationDetail.newDetail(this.annotations[i]);
            }
            this.detailList = Fs.list(details);
        }

        @Override
        public @Nonnull List<@Nonnull Annotation> annotations() {
            return annotationList;
        }

        @Override
        public <T extends Annotation> @Nullable T get(@Nonnull Class<T> annotationClass) {
            return Fs.as(
                annotationList.stream()
                    .filter(a -> a.annotationType().equals(annotationClass))
                    .findFirst()
                    .orElse(null)
            );
        }

        @Override
        public @Nonnull List<@Nonnull AnnotationDetail<?>> details() {
            return detailList;
        }

        @Override
        public <D extends AnnotationDetail<?>> @Nullable D getDetail(@Nonnull Class<D> detailClass) {
            return Fs.as(
                detailList.stream()
                    .filter(a -> a.getClass().equals(detailClass))
                    .findFirst()
                    .orElse(null)
            );
        }

        @Override
        public <T extends Annotation, D extends AnnotationDetail<T>> D getDetailByAnnotationType(
            @Nonnull Class<T> annotationClass
        ) {
            for (int i = 0; i < annotations.length; i++) {
                if (annotations[i].annotationType().equals(annotationClass)) {
                    return Fs.as(details[i]);
                }
            }
            return null;
        }
    }

    static final class Cache {

        private static final @Nonnull SimpleCache<
            @Nonnull AnnotatedElement,
            @Nonnull AnnotationSet
            > CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        static @Nonnull AnnotationSet get(
            @Nonnull AnnotatedElement annotatedElement,
            @Nonnull Function<@Nonnull AnnotatedElement, @Nonnull AnnotationSet> function
        ) {
            return CACHE.get(annotatedElement, function);
        }

        private Cache() {
        }
    }

    private AnnotationBack() {
    }
}
