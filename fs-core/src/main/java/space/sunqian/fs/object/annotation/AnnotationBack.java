package space.sunqian.fs.object.annotation;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.SimpleCache;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class AnnotationBack {

    static @Nonnull AnnotationSet getSet(
        @Nonnull AnnotatedElement annotatedElement,
        @Nonnull Function<@Nonnull AnnotatedElement, @Nonnull AnnotationSet> function
    ) {
        return Cache.get(annotatedElement, function);
    }

    static @Nonnull AnnotationSet newSet(@Nonnull AnnotatedElement annotatedElement) {
        return new AnnotationSetImpl(annotatedElement);
    }

    static @Nonnull AnnotationSet multiSet(@Nonnull AnnotationSet @Nonnull @RetainedParam [] annotationSets) {
        return new MultiAnnotationSet(annotationSets);
    }

    static @Nonnull AnnotationSet emptySet() {
        return EmptySet.INST;
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

    private static final class MultiAnnotationSet implements AnnotationSet {

        private final @Nonnull AnnotationSet @Nonnull [] annotationSets;

        private MultiAnnotationSet(@Nonnull AnnotationSet @Nonnull @RetainedParam [] annotationSets) {
            this.annotationSets = annotationSets;
        }

        @Override
        public @Nonnull List<@Nonnull Annotation> annotations() {
            ArrayList<Annotation> annotationList = new ArrayList<>();
            for (AnnotationSet annotationSet : annotationSets) {
                annotationList.addAll(annotationSet.annotations());
            }
            annotationList.trimToSize();
            return annotationList;
        }

        @Override
        public <T extends Annotation> @Nullable T get(@Nonnull Class<T> annotationClass) {
            for (AnnotationSet annotationSet : annotationSets) {
                T ret = annotationSet.get(annotationClass);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        @Override
        public @Nonnull List<@Nonnull AnnotationDetail<?>> details() {
            ArrayList<AnnotationDetail<?>> annotationDetailList = new ArrayList<>();
            for (AnnotationSet annotationSet : annotationSets) {
                annotationDetailList.addAll(annotationSet.details());
            }
            annotationDetailList.trimToSize();
            return annotationDetailList;
        }

        @Override
        public <D extends AnnotationDetail<?>> @Nullable D getDetail(@Nonnull Class<D> detailClass) {
            for (AnnotationSet annotationSet : annotationSets) {
                D ret = annotationSet.getDetail(detailClass);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }

        @Override
        public <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D getDetailByAnnotationType(
            @Nonnull Class<T> annotationClass
        ) {
            for (AnnotationSet annotationSet : annotationSets) {
                D ret = annotationSet.getDetailByAnnotationType(annotationClass);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }
    }

    private enum EmptySet implements AnnotationSet {
        INST;

        @Override
        public @Nonnull List<@Nonnull Annotation> annotations() {
            return Collections.emptyList();
        }

        @Override
        public <T extends Annotation> @Nullable T get(@Nonnull Class<T> annotationClass) {
            return null;
        }

        @Override
        public @Nonnull List<@Nonnull AnnotationDetail<?>> details() {
            return Collections.emptyList();
        }

        @Override
        public <D extends AnnotationDetail<?>> @Nullable D getDetail(@Nonnull Class<D> detailClass) {
            return null;
        }

        @Override
        public <T extends Annotation, D extends AnnotationDetail<T>> @Nullable D getDetailByAnnotationType(@Nonnull Class<T> annotationClass) {
            return null;
        }
    }

    private static final class Cache {

        private static final @Nonnull SimpleCache<
            @Nonnull AnnotatedElement,
            @Nonnull AnnotationSet
            > CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        private static @Nonnull AnnotationSet get(
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
