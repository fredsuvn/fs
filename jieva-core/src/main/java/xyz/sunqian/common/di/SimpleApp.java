package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.runtime.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
     *     Invokes pre-destroy methods, which are typically annotated with {@code PreDestroy}
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
     *         Stopping this app will not result in stopping sub apps, but shared resources of this app may be invalid.
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
     * Returns the resources generated and managed by this app (excluding the instance shared from the parent apps).
     *
     * @return the resources generated and managed by this app (excluding the instance shared from the parent apps)
     */
    @Nonnull
    List<@Nonnull SimpleResource> localResources();

    /**
     * Returns all resources this app depends (including the instance shared from parents).
     *
     * @return all resources this app depends (including the instance shared from parents)
     */
    @Nonnull
    List<@Nonnull SimpleResource> allResources();

    /**
     * Returns a resource instance that can be assigned to the specified type from {@link #allResources()}, or
     * {@code null} if no such resource exists.
     * <p>
     * This method will first attempt to precisely match the type using {@link Object#equals(Object)}. If there is no
     * exact matching resource type, it will return the first instance that can be assigned to the specified type.
     *
     * @param type the specified type that can be assigned from the returned instance
     * @return a resource instance that can be assigned to the specified type
     */
    default <T> @Nullable T getResource(@Nonnull Class<T> type) {
        return Jie.as(getResource((Type) type));
    }

    /**
     * Returns a resource instance that can be assigned to the specified type from {@link #allResources()}, or
     * {@code null} if no such resource exists.
     * <p>
     * This method will first attempt to precisely match the type using {@link Object#equals(Object)}. If there is no
     * exact matching resource type, it will return the first instance that can be assigned to the specified type.
     *
     * @param type the {@link TypeRef} of the specified type that can be assigned from the returned instance
     * @return a resource instance that can be assigned to the specified type
     */
    default <T> @Nullable T getResource(@Nonnull TypeRef<T> type) {
        return Jie.as(getResource(type.type()));
    }

    /**
     * Returns a resource instance that can be assigned to the specified type from {@link #allResources()}, or
     * {@code null} if no such resource exists.
     * <p>
     * This method will first attempt to precisely match the type using {@link Object#equals(Object)}. If there is no
     * exact matching resource type, it will return the first instance that can be assigned to the specified type.
     *
     * @param type the specified type that can be assigned from the returned instance
     * @return a resource instance that can be assigned to the specified type
     */
    @Nullable
    Object getResource(@Nonnull Type type);

    /**
     * Builder for {@link SimpleApp}.
     */
    class Builder {

        private static final @Nonnull String @Nonnull [] RESOURCE_ANNOTATIONS =
            {"javax.annotation.Resource", "jakarta.annotation.Resource"};
        private static final @Nonnull String @Nonnull [] POST_CONSTRUCT_ANNOTATIONS =
            {"javax.annotation.Resource", "jakarta.annotation.Resource"};
        private static final @Nonnull String @Nonnull [] PRE_DESTROY_ANNOTATIONS =
            {"javax.annotation.Resource", "jakarta.annotation.Resource"};

        private final @Nonnull List<Type> resourceTypes = new ArrayList<>();
        private final @Nonnull List<@Nonnull SimpleApp> parentApps = new ArrayList<>();
        private final @Nonnull List<@Nonnull String> resourceAnnotations = new ArrayList<>();
        private final @Nonnull List<@Nonnull String> postConstructAnnotations = new ArrayList<>();
        private final @Nonnull List<@Nonnull String> preDestroyAnnotations = new ArrayList<>();
        private boolean enableAspect = false;

        /**
         * Adds an annotation applied to a {@link Field} indicate that it references a resource instance. There can be
         * multiple annotations serve as resource annotation, and a {@link Field} is considered annotated if it has any
         * one of those annotations.
         * <p>
         * If no annotation is specified, the default annotations will be used: {@code javax.annotation.Resource} and
         * {@code jakarta.annotation.Resource}.
         *
         * @param resourceAnnotation the annotation applied to a {@link Field} indicate that it references a resource
         *                           instance
         * @return this builder
         */
        public @Nonnull Builder resourceAnnotation(@Nonnull Class<? extends Annotation> resourceAnnotation) {
            this.resourceAnnotations.add(resourceAnnotation.getName());
            return this;
        }

        /**
         * Adds an annotation applied to a {@link Method} indicate that it is a post-construct method. There can be
         * multiple annotations serve as post-construct annotation, and a {@link Method} is considered annotated if it
         * has any one of those annotations.
         * <p>
         * If no annotation is specified, the default annotations will be used: {@code javax.annotation.PostConstruct}
         * and {@code jakarta.annotation.PostConstruct}.
         *
         * @param postConstructAnnotation the annotation applied to a {@link Method} indicate that it is a
         *                                post-construct method
         * @return this builder
         */
        public @Nonnull Builder postConstructAnnotation(@Nonnull Class<? extends Annotation> postConstructAnnotation) {
            this.postConstructAnnotations.add(postConstructAnnotation.getName());
            return this;
        }

        /**
         * Adds an annotation applied to a {@link Method} indicate that it is a pre-destroy method. There can be
         * multiple annotations serve as pre-destroy annotation, and a {@link Method} is considered annotated if it has
         * any one of those annotations.
         * <p>
         * If no annotation is specified, the default annotations will be used: {@code javax.annotation.PostConstruct}
         * and {@code jakarta.annotation.PostConstruct}.
         *
         * @param preDestroyAnnotation the annotation applied to a {@link Method} indicate that it is a pre-destroy
         *                             method
         * @return this builder
         */
        public @Nonnull Builder preDestroyAnnotation(@Nonnull Class<? extends Annotation> preDestroyAnnotation) {
            this.preDestroyAnnotations.add(preDestroyAnnotation.getName());
            return this;
        }

        /**
         * Adds types of the resources to this builder, each type should be a {@link Class} or
         * {@link ParameterizedType}.
         * <p>
         * These types serve as root types for dependency injection. The dependency resolver will recursively analyze
         * fields of these types to build the complete dependency graph.
         * <p>
         * Note if there are equal types, only one will be retained, and each type will only generate one instance.
         *
         * @param resourceTypes the types of the resources to be added
         * @return this builder
         */
        public @Nonnull Builder resources(@Nonnull Type @Nonnull ... resourceTypes) {
            CollectKit.addAll(this.resourceTypes, resourceTypes);
            return this;
        }

        /**
         * Adds types of the resources to this builder, each type should be a {@link Class} or
         * {@link ParameterizedType}.
         * <p>
         * These types serve as root types for dependency injection. The dependency resolver will recursively analyze
         * fields of these types to build the complete dependency graph.
         * <p>
         * Note if there are equal types, only one will be retained, and each type will only generate one instance.
         *
         * @param resourceTypes the types of the resources to be added
         * @return this builder
         */
        public @Nonnull Builder resources(@Nonnull Iterable<@Nonnull Type> resourceTypes) {
            CollectKit.addAll(this.resourceTypes, resourceTypes);
            return this;
        }

        /**
         * Adds parent apps for resource sharing.
         * <p>
         * When a parent app is provided, the current app will attempt to reuse resource instances from the parent app
         * instead of creating new ones. Specifically, if a resource type required by the current app already exists in
         * the parent app, the instance from the parent app will be shared and used directly.
         *
         * @param parents the parent apps
         * @return this builder
         */
        public @Nonnull Builder parent(@Nullable SimpleApp @Nonnull ... parents) {
            CollectKit.addAll(this.parentApps, parents);
            return this;
        }

        /**
         * Adds parent apps for resource sharing.
         * <p>
         * When a parent app is provided, the current app will attempt to reuse resource instances from the parent app
         * instead of creating new ones. Specifically, if a resource type required by the current app already exists in
         * the parent app, the instance from the parent app will be shared and used directly.
         *
         * @param parents the parent apps
         * @return this builder
         */
        public @Nonnull Builder parent(@Nonnull Iterable<@Nonnull SimpleApp> parents) {
            CollectKit.addAll(this.parentApps, parents);
            return this;
        }

        /**
         * Enables or disables the aspect-oriented programming (AOP) functionality.
         * <p>
         * When enabled, this builder will first start standard dependency injection, creating one instance for each
         * resource type. Resource objects which are instances of the {@link SimpleAppAspect} will be treated as aspect
         * handlers (excluding instances from parent apps), and the other resource instances (also excluding instances
         * from parent apps) will be evaluated by {@link SimpleAppAspect#needsAspect(Type)} of each aspect handler in an
         * unspecified order. The evaluation stops at the first handler where the {@code needsAspect} returns
         * {@code true} for the resource type. When a match is found, the resource instance will be advised by that
         * handler, and no further aspect handlers will be evaluated for that type.
         * <p>
         * If no aspect handler matches a resource type, the resource type will not be advised. And if a resource
         * instance is advised and replaced by the advised instance, the Post-Construct and Pre-Destroy methods (if any)
         * will execute on the advised instance rather than the original instance.
         *
         * @param aspect {@code true} to enable AOP functionality, {@code false} to disable it
         * @return this builder
         */
        public @Nonnull Builder aspect(boolean aspect) {
            this.enableAspect = aspect;
            return this;
        }

        /**
         * Builds and starts this app, following steps in order:
         * <ol>
         *     <li><b>Dependency Injection (DI) Processing</b>:
         *     Creates and manages object instances, excluding resources shared from the parent apps,
         *     and only one instance per {@link Type} (singleton scope).
         *     </li>
         *     <li><b>Aspect-Oriented Programming (AOP) Processing</b>:
         *     Applies aspect handlers to relevant classes if AOP is enabled.</li>
         *     <li><b>Initialization Method Execution</b>:
         *     Invokes initialization methods which are typically annotated with {@code PostConstruct},
         *     excluding resources shared from the parent apps. And the order is unspecified.</li>
         * </ol>
         * <p>
         * Note:
         * <ul>
         *     <li>
         *         This method will block the current thread until all steps have completed.
         *     </li>
         *     <li>
         *         A {@link SimpleApp} can only start or shutdown once, A new {@link SimpleApp} instance must be created
         *         to resume functionality.
         *     </li>
         *     <li>
         *         A {@link SimpleApp} can only be started if all parent {@link SimpleApp} are valid.
         *         The behavior is undefined if a parent {@link SimpleApp} is invalid for this app.
         *     </li>
         * </ul>
         *
         * @throws SimpleAppException if an error occurs during startup, or if the app is already running or has been
         *                            stopped
         */
        public @Nonnull SimpleApp build() throws SimpleAppException {
            return new SimpleAppImpl(
                resourceTypes,
                parentApps.isEmpty() ? new SimpleApp[0] : parentApps.toArray(new SimpleApp[0]),
                enableAspect,
                resourceAnnotations.isEmpty() ? RESOURCE_ANNOTATIONS : resourceAnnotations.toArray(new String[0]),
                postConstructAnnotations.isEmpty() ? POST_CONSTRUCT_ANNOTATIONS : postConstructAnnotations.toArray(new String[0]),
                preDestroyAnnotations.isEmpty() ? PRE_DESTROY_ANNOTATIONS : preDestroyAnnotations.toArray(new String[0])
            );
        }
    }
}
