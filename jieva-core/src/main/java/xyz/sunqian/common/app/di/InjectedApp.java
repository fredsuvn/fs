package xyz.sunqian.common.app.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.app.SimpleApp;
import xyz.sunqian.common.base.Kit;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.runtime.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A dependency injection-based sub-interface of {@link SimpleApp}, built via {@link #newBuilder()}. See
 * {@linkplain xyz.sunqian.common.app.di DI package documentation} for more information.
 *
 * @author sunqian
 */
public interface InjectedApp extends SimpleApp {

    /**
     * Returns a new builder for {@link InjectedApp}.
     *
     * @return a new builder for {@link InjectedApp}
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Shuts down this app.
     * <p>
     * All {@code pre-destroy} methods in this app's resources are executed sequentially according to their dependency
     * relationships. If an exception occurs during the execution, an {@link InjectedResourceDestructionException} will
     * be thrown and the shutdown process will terminate immediately, and the remaining {@code pre-destroy} methods will
     * not be executed. See {@linkplain xyz.sunqian.common.app.di DI package documentation} for more information.
     * <p>
     * Once this app is shut down, it becomes invalid and cannot be restarted. However, its sub-apps are not
     * automatically shut down along with it. Sub-apps that depend on resources from this app will generally continue to
     * function normally, as long as those resources haven't been destroyed (by {@code pre-destroy} methods) during this
     * shutdown process.
     * <p>
     * This method blocks current thread until the shutdown operation is completed.
     *
     * @throws InjectedResourceDestructionException if any error occurs during resource destruction
     * @throws InjectedAppException                 if any other error occurs during shutdown operation
     */
    void shutdown() throws InjectedResourceDestructionException, InjectedAppException;

    /**
     * Returns all parent apps of this app.
     *
     * @return all parent apps of this app
     */
    @Nonnull
    List<@Nonnull InjectedApp> parentApps();

    /**
     * Returns the resources that are directly owned by this app, excluding the resources inherited from parent apps.
     *
     * @return the resources that are directly owned by this app, excluding the resources inherited from parent apps
     */
    @Nonnull
    List<@Nonnull InjectedResource> localResources();

    /**
     * Returns all resources that constitute this app, including the resources inherited from parent apps.
     *
     * @return all resources that constitute this app, including the resources inherited from parent apps
     */
    @Nonnull
    List<@Nonnull InjectedResource> resources();

    /**
     * Returns a resource instance whose type is assignable to the specified type, or {@code null} if no such resource
     * exists.
     * <p>
     * This method first attempts to find a resource whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one resource instance that can
     * be assigned to the specified type from all resources that constitute this app.
     *
     * @param type the specified type
     * @return a resource instance whose type is assignable to the specified type, or {@code null} if no such resource
     * exists
     */
    default <T> @Nullable T getResource(@Nonnull Class<T> type) {
        return Kit.as(getResource((Type) type));
    }

    /**
     * Returns a resource instance whose type is assignable to the specified type, or {@code null} if no such resource
     * exists.
     * <p>
     * This method first attempts to find a resource whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one resource instance that can
     * be assigned to the specified type from all resources that constitute this app.
     *
     * @param type the {@link TypeRef} for the specified type
     * @return a resource instance whose type is assignable to the specified type, or {@code null} if no such resource
     * exists
     */
    default <T> @Nullable T getResource(@Nonnull TypeRef<T> type) {
        return Kit.as(getResource(type.type()));
    }

    /**
     * Returns a resource instance whose type is assignable to the specified type, or {@code null} if no such resource
     * exists.
     * <p>
     * This method first attempts to find a resource whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one resource instance that can
     * be assigned to the specified type from all resources that constitute this app.
     *
     * @param type the specified type
     * @return a resource instance whose type is assignable to the specified type, or {@code null} if no such resource
     * exists
     */
    @Nullable
    Object getResource(@Nonnull Type type);

    /**
     * Builder for {@link InjectedApp}.
     */
    class Builder {

        private static final @Nonnull String @Nonnull [] RESOURCE_ANNOTATIONS =
            {"javax.annotation.Resource", "jakarta.annotation.Resource"};
        private static final @Nonnull String @Nonnull [] POST_CONSTRUCT_ANNOTATIONS =
            {"javax.annotation.PostConstruct", "jakarta.annotation.PostConstruct"};
        private static final @Nonnull String @Nonnull [] PRE_DESTROY_ANNOTATIONS =
            {"javax.annotation.PreDestroy", "jakarta.annotation.PreDestroy"};

        private final @Nonnull Set<Type> resourceTypes = new LinkedHashSet<>();
        private final @Nonnull Set<@Nonnull InjectedApp> parentApps = new LinkedHashSet<>();
        private final @Nonnull Set<@Nonnull String> resourceAnnotations = new LinkedHashSet<>();
        private final @Nonnull Set<@Nonnull String> postConstructAnnotations = new LinkedHashSet<>();
        private final @Nonnull Set<@Nonnull String> preDestroyAnnotations = new LinkedHashSet<>();

        /**
         * Adds a resource annotation type that indicates a {@link Field} references a resource from the app.
         * <p>
         * Multiple resource annotation types can be specified, and any of them will be recognized as marking a resource
         * dependency. If no resource annotation types are explicitly specified, the default annotations used are:
         * {@code javax.annotation.Resource} and {@code jakarta.annotation.Resource}.
         *
         * @param resourceAnnotation a resource annotation type that marks a field as referencing an app resource
         * @return this builder
         */
        public @Nonnull Builder resourceAnnotation(@Nonnull Class<? extends Annotation> resourceAnnotation) {
            this.resourceAnnotations.add(resourceAnnotation.getName());
            return this;
        }

        /**
         * Adds a post-construct annotation type that indicates a {@link Method} will be executed after its resource
         * construction is completed.
         * <p>
         * Multiple post-construct annotation types can be specified, and any of them will be recognized as marking a
         * post-construct method. If no post-construct annotation types are explicitly specified, the default
         * annotations used are: {@code javax.annotation.PostConstruct} and {@code jakarta.annotation.PostConstruct}.
         *
         * @param postConstructAnnotation a post-construct annotation type that marks a method to be executed after its
         *                                resource construction is completed
         * @return this builder
         */
        public @Nonnull Builder postConstructAnnotation(@Nonnull Class<? extends Annotation> postConstructAnnotation) {
            this.postConstructAnnotations.add(postConstructAnnotation.getName());
            return this;
        }

        /**
         * Adds a pre-destroy annotation type that indicates a {@link Method} will be executed before its resource is
         * destroyed.
         * <p>
         * Multiple pre-destroy annotation types can be specified, and any of them will be recognized as marking a
         * pre-destroy method. If no pre-destroy annotation types are explicitly specified, the default annotations used
         * are: {@code javax.annotation.PreDestroy} and {@code jakarta.annotation.PreDestroy}.
         *
         * @param preDestroyAnnotation a pre-destroy annotation type that marks a method to be executed before its
         *                             resource is destroyed
         * @return this builder
         */
        public @Nonnull Builder preDestroyAnnotation(@Nonnull Class<? extends Annotation> preDestroyAnnotation) {
            this.preDestroyAnnotations.add(preDestroyAnnotation.getName());
            return this;
        }

        /**
         * Adds root resource types to this builder, each type should be a {@link Class} or {@link ParameterizedType}.
         * And previously added types will be ignored.
         * <p>
         * These types serve as root types for dependency injection. The dependency resolver will recursively analyze
         * {@link Field}s of these types to build the complete dependency graph. Note each type only generates singleton
         * resource instance.
         *
         * @param resourceTypes the resource types to be added
         * @return this builder
         */
        public @Nonnull Builder resourceTypes(@Nonnull Type @Nonnull ... resourceTypes) {
            CollectKit.addAll(this.resourceTypes, resourceTypes);
            return this;
        }

        /**
         * Adds root resource types to this builder, each type should be a {@link Class} or {@link ParameterizedType}.
         * And previously added types will be ignored.
         * <p>
         * These types serve as root types for dependency injection. The dependency resolver will recursively analyze
         * {@link Field}s of these types to build the complete dependency graph. Note each type only generates singleton
         * resource instance.
         *
         * @param resourceTypes the resource types to be added
         * @return this builder
         */
        public @Nonnull Builder resourceTypes(@Nonnull Iterable<@Nonnull Type> resourceTypes) {
            CollectKit.addAll(this.resourceTypes, resourceTypes);
            return this;
        }

        /**
         * Adds parent apps to inherit and share its resources. If a resource type already exists in the parent app, the
         * sub-app will not generate another instance of that type.
         * <p>
         * Once a parent app is shut down, its sub-apps are not automatically shut down along with it. The inherited
         * resources will still be held by the sub-apps, but theirs pre-destroy methods will be executed.
         *
         * @param parentApps the parent apps
         * @return this builder
         */
        public @Nonnull Builder parentApps(@Nullable InjectedApp @Nonnull ... parentApps) {
            CollectKit.addAll(this.parentApps, parentApps);
            return this;
        }

        /**
         * Adds parent apps to inherit and share its resources. If a resource type already exists in the parent app, the
         * sub-app will not generate another instance of that type.
         * <p>
         * Once a parent app is shut down, its sub-apps are not automatically shut down along with it. The inherited
         * resources will still be held by the sub-apps, but theirs pre-destroy methods will be executed.
         *
         * @param parentApps the parent apps
         * @return this builder
         */
        public @Nonnull Builder parentApps(@Nonnull Iterable<@Nonnull InjectedApp> parentApps) {
            CollectKit.addAll(this.parentApps, parentApps);
            return this;
        }

        /**
         * Builds and starts a new app instance. The startup process performs the following operations in sequence:
         * <ol>
         *   <li>Generating instances for all resource types with singleton mode;</li>
         *   <li>Dependency injection for all resource instances;</li>
         *   <li>AOP processing if {@link InjectedAspect} instances exist in resource instances;</li>
         *   <li>Re-injection of dependencies for resource instances affected by AOP processing;</li>
         *   <li>Executing all {@code post-construct} methods of resource instances in dependency order</li>
         * </ol>
         * If any {@code post-construct} method fails, an {@link InjectedResourceInitializationException} is thrown
         * immediately. Resources that have already successfully executed {@code post-construct} methods will not be
         * rolled back. See {@linkplain xyz.sunqian.common.app.di DI package documentation} for more information.
         * <p>
         * This method blocks current thread until the startup operation is completed.
         *
         * @return the newly built and started app instance
         * @throws InjectedResourceInitializationException if any {@code post-construct} method execution fails
         * @throws InjectedAppException                    if any other error occurs during app construction or startup
         */
        public @Nonnull InjectedApp build()
            throws InjectedResourceInitializationException, InjectedAppException {
            return new InjectedAppImpl(
                resourceTypes,
                parentApps.isEmpty() ? new InjectedApp[0] : parentApps.toArray(new InjectedApp[0]),
                resourceAnnotations.isEmpty() ? RESOURCE_ANNOTATIONS : resourceAnnotations.toArray(new String[0]),
                postConstructAnnotations.isEmpty() ? POST_CONSTRUCT_ANNOTATIONS : postConstructAnnotations.toArray(new String[0]),
                preDestroyAnnotations.isEmpty() ? PRE_DESTROY_ANNOTATIONS : preDestroyAnnotations.toArray(new String[0])
            );
        }
    }
}
