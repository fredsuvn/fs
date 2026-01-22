package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.CollectKit;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a DI container that manages the injection of dependencies into objects.
 * <p>
 * A DI container is typically created via {@link #newBuilder()}, which is used to configure the container's components
 * and behaviors. See {@linkplain space.sunqian.fs.di DI documentation} for more information.
 *
 * @author sunqian
 */
public interface DIContainer {

    /**
     * Returns a new builder for {@link DIContainer}.
     *
     * @return a new builder for {@link DIContainer}
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Initializes this DI container.
     * <p>
     * All {@code post-construct} methods of this DI container's components are executed sequentially according to their
     * dependency relationships. If an exception occurs during the execution, an {@link DIInitializeException} will be
     * thrown and the initialization process will terminate immediately, successfully initialized components will not be
     * rolled back and the uninitialized components will not be initialized. See
     * {@linkplain space.sunqian.fs.di DI documentation} for more information. A DI container can only initialize once
     * and cannot re-initialize.
     * <p>
     * This method blocks current thread until the initialization operation is completed.
     *
     * @return this DI container
     * @throws DIInitializeException if any {@code post-construct} method execution fails
     * @throws DIException           if any other error occurs during initialization operation
     */
    DIContainer initialize() throws DIInitializeException, DIException;

    /**
     * Returns whether this DI container is initialized.
     *
     * @return {@code true} if this DI container is initialized; {@code false} otherwise
     */
    boolean isInitialized();

    /**
     * Shuts down this DI container.
     * <p>
     * All {@code pre-destroy} methods of this DI container's components are executed sequentially according to their
     * dependency relationships. If an exception occurs during the execution, an {@link DIShutdownException} will be
     * thrown and the shutdown process will terminate immediately, successfully destroyed components will not be rolled
     * back and the un-destroyed components will not be destroyed. See {@linkplain space.sunqian.fs.di DI documentation}
     * for more information. A DI container can only shut down once and cannot re-shutdown.
     * <p>
     * Sub-containers are not automatically shut down along with this container, but components that sub-containers
     * depend on from this DI container will be destroyed by this DI container.
     * <p>
     * This method blocks current thread until the shutdown operation is completed.
     *
     * @return this DI container
     * @throws DIShutdownException if any {@code pre-destroy} method execution fails
     * @throws DIException         if any other error occurs during shutdown operation
     */
    DIContainer shutdown() throws DIShutdownException, DIException;

    /**
     * Returns whether this DI container is shut down.
     *
     * @return {@code true} if this DI container is shut down; {@code false} otherwise
     */
    boolean isShutdown();

    /**
     * Returns all parent containers of this container.
     *
     * @return all parent containers of this container
     */
    @Nonnull
    List<@Nonnull DIContainer> parentContainers();

    /**
     * Returns the component map that are directly owned by this container, excluding the components inherited from
     * parent containers.
     *
     * @return the component map that are directly owned by this container, excluding the components inherited from
     * parent containers
     */
    @Nonnull
    Map<@Nonnull Type, @Nonnull DIComponent> localComponents();

    /**
     * Returns all component map that constitute this container, including the components inherited from parent
     * containers.
     *
     * @return all component map that constitute this container, including the components inherited from parent
     * containers
     */
    @Nonnull
    Map<@Nonnull Type, @Nonnull DIComponent> components();

    /**
     * Returns the component whose type is assignable to the specified type, or {@code null} if no such component
     * exists.
     * <p>
     * This method first attempts to find a component whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one component that can be
     * assigned to the specified type from all components that constitute this container.
     *
     * @param type the specified type
     * @return the component whose type is assignable to the specified type, or {@code null} if no such component exists
     */
    @Nullable
    DIComponent getComponent(@Nonnull Type type);

    /**
     * Returns the component object whose type is assignable to the specified type, or {@code null} if no such component
     * exists.
     * <p>
     * This method first attempts to find a component whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one component object that can
     * be assigned to the specified type from all components that constitute this container.
     *
     * @param <T>  the specified type
     * @param type the specified type
     * @return the component object whose type is assignable to the specified type, or {@code null} if no such component
     * exists
     */
    default <T> @Nullable T getObject(@Nonnull Class<T> type) {
        return Fs.as(getObject((Type) type));
    }

    /**
     * Returns the component object whose type is assignable to the specified type, or {@code null} if no such component
     * exists.
     * <p>
     * This method first attempts to find a component whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one component object that can
     * be assigned to the specified type from all components that constitute this container.
     *
     * @param <T>  the specified type
     * @param type the {@link TypeRef} for the specified type
     * @return the component object whose type is assignable to the specified type, or {@code null} if no such component
     * exists
     */
    default <T> @Nullable T getObject(@Nonnull TypeRef<T> type) {
        return Fs.as(getObject(type.type()));
    }

    /**
     * Returns the component object whose type is assignable to the specified type, or {@code null} if no such component
     * exists.
     * <p>
     * This method first attempts to find a component whose type exactly matches the specified type using
     * {@link Object#equals(Object)}. If no exact match is found, it will randomly select one component object that can
     * be assigned to the specified type from all components that constitute this container.
     *
     * @param type the specified type
     * @return the component object whose type is assignable to the specified type, or {@code null} if no such component
     * exists
     */
    default @Nullable Object getObject(@Nonnull Type type) {
        DIComponent component = getComponent(type);
        if (component != null) {
            return component.instance();
        }
        return null;
    }

    /**
     * Builder for {@link DIContainer}.
     */
    class Builder {

        private static final @Nonnull List<@Nonnull String> RESOURCE_ANNOTATIONS =
            ListKit.list("javax.annotation.Resource", "jakarta.annotation.Resource");
        private static final @Nonnull List<@Nonnull String> POST_CONSTRUCT_ANNOTATIONS =
            ListKit.list("javax.annotation.PostConstruct", "jakarta.annotation.PostConstruct");
        private static final @Nonnull List<@Nonnull String> PRE_DESTROY_ANNOTATIONS =
            ListKit.list("javax.annotation.PreDestroy", "jakarta.annotation.PreDestroy");

        private final @Nonnull Collection<Type> componentTypes = new LinkedHashSet<>();
        private final @Nonnull Collection<@Nonnull DIContainer> parentContainers = new LinkedHashSet<>();
        private final @Nonnull Collection<@Nonnull String> componentAnnotations = new LinkedHashSet<>();
        private final @Nonnull Collection<@Nonnull String> postConstructAnnotations = new LinkedHashSet<>();
        private final @Nonnull Collection<@Nonnull String> preDestroyAnnotations = new LinkedHashSet<>();

        private @Nonnull DIComponent.Resolver componentResolver = DIComponent.defaultResolver();
        private @Nonnull DIComponent.FieldSetter fieldSetter = DIComponent.defaultFieldSetter();

        /**
         * Adds a component annotation type that marks a {@link Field} references a component from the container.
         * <p>
         * Multiple component annotation types can be specified, and any of them will be recognized as marking a
         * component dependency. If no component annotation types are explicitly specified, the default annotations used
         * are:
         * <lu>
         * <li>{@code javax.annotation.Resource}</li>
         * <li>{@code jakarta.annotation.Resource}</li>
         * </lu>
         *
         * @param componentAnnotation a component annotation type that marks a {@link Field} as referencing a component
         *                            from the container
         * @return this builder
         */
        public @Nonnull Builder componentAnnotation(@Nonnull Class<? extends Annotation> componentAnnotation) {
            this.componentAnnotations.add(componentAnnotation.getName());
            return this;
        }

        /**
         * Adds a post-construct annotation type that marks a {@link Method} is a post-construct method that will be
         * executed in dependency order in initialization process of the container.
         * <p>
         * Multiple post-construct annotation types can be specified, and any of them will be recognized as marking a
         * post-construct method. If no post-construct annotation types are explicitly specified, the default
         * annotations used are:
         * <lu>
         * <li>{@code javax.annotation.PostConstruct}</li>
         * <li>{@code jakarta.annotation.PostConstruct}</li>
         * </lu>
         *
         * @param postConstructAnnotation a post-construct annotation type that marks a {@link Method} is a
         *                                post-construct method
         * @return this builder
         */
        public @Nonnull Builder postConstructAnnotation(@Nonnull Class<? extends Annotation> postConstructAnnotation) {
            this.postConstructAnnotations.add(postConstructAnnotation.getName());
            return this;
        }

        /**
         * Adds a pre-destroy annotation type that marks a {@link Method} is a pre-destroy method that will be executed
         * in dependency order in shutdown process of the container.
         * <p>
         * Multiple pre-destroy annotation types can be specified, and any of them will be recognized as marking a
         * pre-destroy method. If no pre-destroy annotation types are explicitly specified, the default annotations used
         * are:
         * <lu>
         * <li>{@code javax.annotation.PreDestroy}</li>
         * <li>{@code jakarta.annotation.PreDestroy}</li>
         * </lu>
         *
         * @param preDestroyAnnotation a pre-destroy annotation type that marks a {@link Method} is a pre-destroy
         *                             method
         * @return this builder
         */
        public @Nonnull Builder preDestroyAnnotation(@Nonnull Class<? extends Annotation> preDestroyAnnotation) {
            this.preDestroyAnnotations.add(preDestroyAnnotation.getName());
            return this;
        }

        /**
         * Adds root component types to this builder, each type should be a {@link Class} or {@link ParameterizedType}.
         * Any previously-added type will be ignored.
         * <p>
         * These types serve as root types for dependency injection. The dependency resolver will recursively analyze
         * all {@link Field}s of these types to build the dependency injection graph.
         * <p>
         * Note each type only generates singleton component instance.
         *
         * @param componentTypes the component types to be added
         * @return this builder
         */
        public @Nonnull Builder componentTypes(@Nonnull Type @Nonnull ... componentTypes) {
            CollectKit.addAll(this.componentTypes, componentTypes);
            return this;
        }

        /**
         * Adds root component types to this builder, each type should be a {@link Class} or {@link ParameterizedType}.
         * Any previously-added type will be ignored.
         * <p>
         * These types serve as root types for dependency injection. The dependency resolver will recursively analyze
         * all {@link Field}s of these types to build the dependency injection graph.
         * <p>
         * Note each type only generates singleton component instance.
         *
         * @param componentTypes the component types to be added
         * @return this builder
         */
        public @Nonnull Builder componentTypes(@Nonnull Iterable<@Nonnull Type> componentTypes) {
            CollectKit.addAll(this.componentTypes, componentTypes);
            return this;
        }

        /**
         * Adds parent containers to inherit and share their components. If a component type has already been registered
         * in the parent container, the sub-container will not generate another instance of that type.
         * <p>
         * Once a parent container is shut down, its sub-containers are not automatically shut down along with it. The
         * inherited components will still be held by the sub-containers, but theirs pre-destroy methods will be
         * executed.
         *
         * @param parentContainers the parent containers
         * @return this builder
         */
        public @Nonnull Builder parentContainers(@Nullable DIContainer @Nonnull ... parentContainers) {
            CollectKit.addAll(this.parentContainers, parentContainers);
            return this;
        }

        /**
         * Adds parent containers to inherit and share their components. If a component type has already been registered
         * in the parent container, the sub-container will not generate another instance of that type.
         * <p>
         * Once a parent container is shut down, its sub-containers are not automatically shut down along with it. The
         * inherited components will still be held by the sub-containers, but theirs pre-destroy methods will be
         * executed.
         *
         * @param parentContainers the parent containers
         * @return this builder
         */
        public @Nonnull Builder parentContainers(@Nonnull Iterable<@Nonnull DIContainer> parentContainers) {
            CollectKit.addAll(this.parentContainers, parentContainers);
            return this;
        }

        /**
         * Sets the component resolver for this container. The default is {@link DIComponent#defaultResolver()}.
         *
         * @param componentResolver the component resolver
         * @return this builder
         */
        public @Nonnull Builder componentResolver(@Nonnull DIComponent.Resolver componentResolver) {
            this.componentResolver = componentResolver;
            return this;
        }

        /**
         * Sets the field setter for this container. The default is {@link DIComponent#defaultFieldSetter()}.
         *
         * @param fieldSetter the field setter
         * @return this builder
         */
        public @Nonnull Builder fieldSetter(@Nonnull DIComponent.FieldSetter fieldSetter) {
            this.fieldSetter = fieldSetter;
            return this;
        }

        /**
         * Builds and returns a new DI container instance, the returned instance is not initialized.
         *
         * @return a new DI container instance
         * @throws DIException if any error occurs
         */
        public @Nonnull DIContainer build() throws DIException {
            if (componentAnnotations.isEmpty()) {
                componentAnnotations.addAll(RESOURCE_ANNOTATIONS);
            }
            if (postConstructAnnotations.isEmpty()) {
                postConstructAnnotations.addAll(POST_CONSTRUCT_ANNOTATIONS);
            }
            if (preDestroyAnnotations.isEmpty()) {
                preDestroyAnnotations.addAll(PRE_DESTROY_ANNOTATIONS);
            }
            return new DIContainerImpl(
                componentTypes,
                parentContainers,
                componentAnnotations,
                postConstructAnnotations,
                preDestroyAnnotations,
                componentResolver,
                fieldSetter
            );
        }
    }
}
