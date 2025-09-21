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
     * Returns the resources managed by this app (excluding the instance shared from the parent apps).
     *
     * @return the resources managed by this app (excluding the instance shared from the parent apps)
     */
    @Nonnull
    Map<@Nonnull Type, @Nonnull Object> resources();

    /**
     * Returns all resources this app depends (including the instance shared from parents).
     *
     * @return all resources this app depends (including the instance shared from parents)
     */
    @Nonnull
    Map<@Nonnull Type, @Nonnull Object> allResources();

    /**
     * Returns the resource instance of the specified type (including the instance shared from parents), or {@code null}
     * if no such resource exists.
     *
     * @param type the specified type of the resource
     * @param <T>  the type of the resource
     * @return the resource of the specified type, or {@code null} if no such resource exists
     */
    default <T> @Nullable T getResource(@Nonnull Class<T> type) {
        return getResource((Type) type);
    }

    /**
     * Returns the resource instance of the specified type (including the instance shared from parents), or {@code null}
     * if no such resource exists.
     *
     * @param type the {@link TypeRef} of the specified type of the resource
     * @param <T>  the type of the resource
     * @return the resource of the specified type, or {@code null} if no such resource exists
     */
    default <T> @Nullable T getResource(@Nonnull TypeRef<T> type) {
        return getResource(type.type());
    }

    /**
     * Returns the resource instance of the specified type (including the instance shared from parents), or {@code null}
     * if no such resource exists.
     *
     * @param type the specified type of the resource
     * @param <T>  the type of the resource
     * @return the resource of the specified type, or {@code null} if no such resource exists
     */
    default <T> @Nullable T getResource(@Nonnull Type type) {
        return Jie.as(allResources().get(type));
    }

    /**
     * Builder for {@link SimpleApp}.
     */
    class Builder {

        private final @Nonnull List<Type> resourceTypes = new ArrayList<>();
        private final @Nonnull List<@Nonnull SimpleApp> parentApps = new ArrayList<>();
        private boolean enableAspect = false;
        private @Nonnull String @Nonnull [] resourceAnnotations = {"javax.annotation.Resource", "jakarta.annotation.Resource"};
        private @Nonnull String @Nonnull [] postConstructAnnotations = {"javax.annotation.PostConstruct", "jakarta.annotation.PostConstruct"};
        private @Nonnull String @Nonnull [] preDestroyAnnotations = {"javax.annotation.PreDestroy", "jakarta.annotation.PreDestroy"};

        /**
         * Sets the annotations applied to a {@link Field} indicate that it references a resource instance. The
         * {@link Field} is considered annotated if it has any one of those annotations.
         * <p>
         * The default annotations are: {@code javax.annotation.Resource} and {@code jakarta.annotation.Resource}.
         *
         * @param resourceAnnotations the annotations applied to a {@link Field} indicate that it references a resource
         *                            instance
         * @return this builder
         */
        public @Nonnull Builder resourceAnnotations(
            @Nonnull Class<? extends @Nonnull Annotation> @Nonnull ... resourceAnnotations
        ) {
            this.resourceAnnotations = toStringArray(resourceAnnotations);
            return this;
        }

        /**
         * Sets the annotations applied to a {@link Method} indicate that it is a post-construct method. The
         * {@link Method} is considered annotated if it has any one of those annotations.
         * <p>
         * The default annotations are: {@code javax.annotation.PostConstruct} and
         * {@code jakarta.annotation.PostConstruct}.
         *
         * @param postConstructAnnotations the annotations applied to a {@link Method} indicate that it is a
         *                                 post-construct method
         * @return this builder
         */
        public @Nonnull Builder postConstructAnnotations(
            @Nonnull Class<? extends @Nonnull Annotation> @Nonnull ... postConstructAnnotations
        ) {
            this.postConstructAnnotations = toStringArray(postConstructAnnotations);
            return this;
        }

        /**
         * Sets the annotations applied to a {@link Method} indicate that it is a pre-destroy method. The {@link Method}
         * is considered annotated if it has any one of those annotations.
         * <p>
         * The default annotations are: {@code javax.annotation.PreDestroy} and {@code jakarta.annotation.PreDestroy}.
         *
         * @param preDestroyAnnotations the annotations applied to a {@link Method} indicate that it is a pre-destroy
         *                              method
         * @return this builder
         */
        public @Nonnull Builder preDestroyAnnotations(
            @Nonnull Class<? extends @Nonnull Annotation> @Nonnull ... preDestroyAnnotations
        ) {
            this.preDestroyAnnotations = toStringArray(preDestroyAnnotations);
            return this;
        }

        private @Nonnull String @Nonnull [] toStringArray(
            @Nonnull Class<? extends @Nonnull Annotation> @Nonnull [] annotations
        ) {
            String[] annotationNames = new String[annotations.length];
            for (int i = 0; i < annotations.length; i++) {
                annotationNames[i] = annotations[i].getName();
            }
            return annotationNames;
        }

        /**
         * Adds types of the resources to this builder, each type must be a {@link Class} or {@link ParameterizedType}.
         * <p>
         * These types will serve as the root types from which dependency injection instances are generated, based on
         * both the types themselves and the fields declared within them.
         * <p>
         * Note if there are identical types, only one will be retained, and each type will only generate one instance.
         *
         * @param resourceTypes the types of the resources to be added
         * @return this builder
         */
        public @Nonnull Builder resourceTypes(@Nonnull Type @Nonnull ... resourceTypes) {
            CollectKit.addAll(this.resourceTypes, resourceTypes);
            return this;
        }

        /**
         * Adds types of the resources to this builder, each type must be a {@link Class} or {@link ParameterizedType}.
         * <p>
         * These types will serve as the root types from which dependency injection instances are generated, based on
         * both the types themselves and the fields declared within them.
         * <p>
         * Note if there are identical types, only one will be retained, and each type will only generate one instance.
         *
         * @param resourceTypes the types of the resources to be added
         * @return this builder
         */
        public @Nonnull Builder resourceTypes(@Nonnull Iterable<@Nonnull Type> resourceTypes) {
            CollectKit.addAll(this.resourceTypes, resourceTypes);
            return this;
        }

        /**
         * Adds the parent {@link SimpleApp}s for resource sharing.
         * <p>
         * When a parent {@link SimpleApp} is provided, the current {@link SimpleApp} will attempt to reuse resource
         * instances from the parent {@link SimpleApp} instead of creating new ones. Specifically, if a resource type
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
         * instances from the parent {@link SimpleApp} instead of creating new ones. Specifically, if a resource type
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
         * Enables or disables the aspect-oriented programming (AOP) functionality.
         * <p>
         * When enabled, this builder will first start standard dependency injection, creating one instance for each
         * resource type. Resource objects which are instances of the {@link SimpleAppAspect} will be treated as aspect
         * handlers, and the other resource instances (excluding resources shared from the parent apps) will be
         * evaluated by {@link SimpleAppAspect#needsAspect(Type)} of each aspect handler in an unspecified order. The
         * evaluation stops at the first handler where the {@code needsAspect} returns {@code true} for the resource
         * type. When a match is found, the resource instance will be advised by that handler, and no further aspect
         * handlers will be evaluated for that type.
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
                parentApps,
                enableAspect,
                resourceAnnotation,
                postConstructAnnotation,
                preDestroyAnnotation
            );
        }
    }
}
