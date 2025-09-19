package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.runtime.aspect.AspectHandler;
import xyz.sunqian.common.runtime.reflect.TypeRef;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple application interface that managed by dependency injection and aspect-oriented programming (AOP) support.
 * Its usage can refer to the current package document: {@link xyz.sunqian.common.di}.
 *
 * @author sunqian
 */
public interface SimpleApp {

    /**
     * Returns a new builder for {@link SimpleApp}.
     *
     * @return a new builder for {@link SimpleApp}
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Shuts down this app by performing the following cleanup operations in order:
     * <ol>
     *     <li><b>Pre-Destroy Method Execution</b>:
     *     Invokes pre-destroy methods, which are typically annotated with {@link PreDestroy}
     *     on managed objects, allowing for custom cleanup logic before object destruction.
     *     And the order is unspecified.</li>
     *     <li><b>Resource Release</b>:
     *     Removes all resources from the dependency injection container (excluding resources shared from the parent
     *     apps).
     *     </li>
     * </ol>
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         This method will block the current thread until all steps have completed.
     *     </li>
     *     <li>
     *         A {@link SimpleApp} can only start or shutdown once, A new {@link SimpleApp} instance must be created to
     *         resume functionality.
     *     </li>
     *     <li>
     *         Stopping this app will not result in stopping sub apps, but shared resources of this app may be removed.
     *     </li>
     * </ul>
     *
     * @throws SimpleAppException if an error occurs during shutdown, or if the caller does not have permission to shut
     *                            down the application
     */
    void shutdown() throws SimpleAppException;

    /**
     * Returns the parent {@link SimpleApp} instances.
     *
     * @return the parent {@link SimpleApp} instances
     */
    @Nonnull
    List<@Nonnull SimpleApp> parents();

    /**
     * Returns the resources managed by this app (excluding resources shared from the parent apps).
     *
     * @return the resources managed by this app (excluding resources shared from the parent apps)
     */
    @Nonnull
    Map<@Nonnull Type, @Nonnull Object> resources();

    /**
     * Returns the resource of the specified type, or {@code null} if no such resource exists.
     *
     * @param type the specified type of the resource
     * @param <T>  the type of the resource
     * @return the resource of the specified type, or {@code null} if no such resource exists
     */
    default <T> @Nullable T getResource(@Nonnull Class<T> type) {
        return getResource((Type) type);
    }

    /**
     * Returns the resource of the specified type, or {@code null} if no such resource exists.
     *
     * @param type the {@link TypeRef} of the specified type of the resource
     * @param <T>  the type of the resource
     * @return the resource of the specified type, or {@code null} if no such resource exists
     */
    default <T> @Nullable T getResource(@Nonnull TypeRef<T> type) {
        return getResource(type.type());
    }

    /**
     * Returns the resource of the specified type, or {@code null} if no such resource exists.
     *
     * @param type the specified type of the resource
     * @param <T>  the type of the resource
     * @return the resource of the specified type, or {@code null} if no such resource exists
     */
    <T> @Nullable T getResource(@Nonnull Type type);

    /**
     * Builder for {@link SimpleApp}.
     */
    class Builder {

        private final @Nonnull List<Type> resourceClasses = new ArrayList<>();
        private final @Nonnull List<@Nonnull SimpleApp> parentApps = new ArrayList<>();
        private boolean enableAspect = false;
        private @Nonnull Class<? extends @Nonnull Annotation> resourceAnnotation = Resource.class;
        private @Nonnull Class<? extends @Nonnull Annotation> postConstructAnnotation = PostConstruct.class;
        private @Nonnull Class<? extends @Nonnull Annotation> preDestroyAnnotation = PreDestroy.class;

        /**
         * Sets the annotation on a {@link Field} to specify this field is a resource needs to be managed by the
         * dependency injection container.
         * <p>
         * The default annotation is: {@link Resource}.
         *
         * @param resourceAnnotation the annotation on a {@link Field} to specify this field is a resource needs to be
         *                           managed by the dependency injection container
         * @return this builder
         */
        public @Nonnull Builder resourceAnnotation(
            @Nonnull Class<? extends @Nonnull Annotation> resourceAnnotation
        ) {
            this.resourceAnnotation = resourceAnnotation;
            return this;
        }

        /**
         * Sets the annotation on a {@link Method} to specify this method is a post-construct method.
         * <p>
         * The default annotation is: {@link PostConstruct}.
         *
         * @param postConstructAnnotation the annotation on a {@link Method} to specify this method is a post-construct
         *                                method
         * @return this builder
         */
        public @Nonnull Builder postConstructAnnotation(
            @Nonnull Class<? extends @Nonnull Annotation> postConstructAnnotation
        ) {
            this.postConstructAnnotation = postConstructAnnotation;
            return this;
        }

        /**
         * Sets the annotation on a {@link Method} to specify this method is a pre-destroy method.
         * <p>
         * The default annotation is: {@link PreDestroy}.
         *
         * @param preDestroyAnnotation the annotation on a {@link Method} to specify this method is a pre-destroy
         *                             method
         * @return this builder
         */
        public @Nonnull Builder preDestroyAnnotation(
            @Nonnull Class<? extends @Nonnull Annotation> preDestroyAnnotation
        ) {
            this.preDestroyAnnotation = preDestroyAnnotation;
            return this;
        }

        /**
         * Adds resource types, which will be managed by the dependency injection container, to this builder.
         *
         * @param resourceClasses the resource types to be added
         * @return this builder
         */
        public @Nonnull Builder resources(@Nonnull Type @Nonnull ... resourceClasses) {
            CollectKit.addAll(this.resourceClasses, resourceClasses);
            return this;
        }

        /**
         * Adds resource types, which will be managed by the dependency injection container, to this builder.
         *
         * @param resourceClasses the resource types to be added
         * @return this builder
         */
        public @Nonnull Builder resources(@Nonnull Iterable<@Nonnull Type> resourceClasses) {
            CollectKit.addAll(this.resourceClasses, resourceClasses);
            return this;
        }

        /**
         * Enables or disables the aspect-oriented programming (AOP) functionality.
         * <p>
         * When enabled, resource classes implementing the {@link AspectHandler} interface will be treated as aspect
         * handlers. These handlers are themselves managed by the dependency injection container and can have their own
         * dependencies injected.
         * <p>
         * Methods in other resource classes will be evaluated by the {@link AspectHandler#needsAspect(Method)} of each
         * aspect handler in an unspecified order. The evaluation stops at the first handler where the
         * {@code needsAspect} returns {@code true} for any method in the target class. When a match is found, the
         * entire class (not just the matching method) will be advised by that handler, and no further aspect handlers
         * will be evaluated for that class.
         * <p>
         * If no aspect handler matches any method in a class, the class will not be advised.
         *
         * @param enableAspect {@code true} to enable AOP functionality, {@code false} to disable it
         * @return this builder
         */
        public @Nonnull Builder enableAspect(boolean enableAspect) {
            this.enableAspect = enableAspect;
            return this;
        }

        /**
         * Adds the parent {@link SimpleApp}s for resource sharing.
         * <p>
         * When a parent {@link SimpleApp} is provided, the current {@link SimpleApp} will attempt to reuse resource
         * instances from the parent {@link SimpleApp} instead of creating new ones. Specifically, if a resource class
         * required by the current {@link SimpleApp} already exists in the parent {@link SimpleApp}, the instance from
         * the parent {@link SimpleApp} will be shared and used directly.
         *
         * @param parents the parent {@link SimpleApp}s
         * @return this builder
         */
        public @Nonnull Builder parent(@Nullable SimpleApp @Nonnull ... parents) {
            CollectKit.addAll(this.parentApps, parents);
            return this;
        }

        /**
         * Adds the parent {@link SimpleApp}s for resource sharing.
         * <p>
         * When a parent {@link SimpleApp} is provided, the current {@link SimpleApp} will attempt to reuse resource
         * instances from the parent {@link SimpleApp} instead of creating new ones. Specifically, if a resource class
         * required by the current {@link SimpleApp} already exists in the parent {@link SimpleApp}, the instance from
         * the parent {@link SimpleApp} will be shared and used directly.
         *
         * @param parents the parent {@link SimpleApp}s
         * @return this builder
         */
        public @Nonnull Builder parent(@Nonnull Iterable<@Nonnull SimpleApp> parents) {
            CollectKit.addAll(this.parentApps, parents);
            return this;
        }

        /**
         * Builds and starts this app with the settings, following steps in order:
         * <ol>
         *     <li><b>Dependency Injection (DI) Processing</b>:
         *     Creates and manages object instances, excluding resources shared from the parent apps,
         *     and only one instance per {@link Type} (singleton scope).
         *     </li>
         *     <li><b>Aspect-Oriented Programming (AOP) Processing</b>:
         *     Applies aspect handlers to relevant classes if AOP is enabled.</li>
         *     <li><b>Initialization Method Execution</b>:
         *     Invokes initialization methods, which are typically annotated with {@link PostConstruct},
         *     following the dependency injection completion. And the order is unspecified.</li>
         * </ol>
         * <p>
         * Note:
         * <ul>
         *     <li>
         *         This method will block the current thread until all steps have completed.
         *     </li>
         *     <li>
         *         A {@link SimpleApp} can only start or shutdown once, A new {@link SimpleApp} instance must be created to
         *         resume functionality.
         *     </li>
         *     <li>
         *         A {@link SimpleApp} can only be started after all parent {@link SimpleApp} have been started.
         *         The behavior is undefined if
         *     </li>
         * </ul>
         *
         * @throws SimpleAppException if an error occurs during startup, or if the app is already running or has been
         *                            stopped
         */
        public @Nonnull SimpleApp build() throws SimpleAppException {
            return new SimpleAppImpl(
                resourceClasses,
                parentApps,
                enableAspect,
                resourceAnnotation,
                postConstructAnnotation,
                preDestroyAnnotation
            );
        }
    }
}
