package space.sunqian.common.app.di;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.invoke.InvocationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Represents resource of {@link InjectedApp}, consisting of a type and a singleton instance of that type.
 * InjectedResources with the same raw class but different generic type parameters are considered distinct and are not
 * equal to each other.
 *
 * @author sunqian
 */
public interface InjectedResource {

    /**
     * Returns the default resolver for {@link InjectedResource}.
     *
     * @return the default resolver for {@link InjectedResource}
     */
    static @Nonnull Resolver defaultResolver() {
        return InjectedAppImpl.Resolver.INST;
    }

    /**
     * Returns the default field setter for {@link InjectedResource}.
     *
     * @return the default field setter for {@link InjectedResource}
     */
    static @Nonnull FieldSetter defaultFieldSetter() {
        return InjectedAppImpl.FieldSetter.INST;
    }

    /**
     * Returns the type of this resource.
     *
     * @return the type of this resource
     */
    @Nonnull
    Type type();

    /**
     * Returns the singleton instance of this resource's type.
     *
     * @return the singleton instance of this resource's type
     */
    @Nonnull
    Object instance();

    /**
     * Returns whether this resource is directly owned by the current app, rather than inherited from parent apps
     *
     * @return {@code true} if this resource is directly owned by the current app, rather than inherited from parent
     * apps; {@code false} otherwise
     */
    boolean isLocal();

    /**
     * Returns the post-construct method of this resource.
     *
     * @return the post-construct method of this resource
     */
    @Nullable
    Method postConstructMethod();

    /**
     * Invokes the post-construct method of this resource if the post-construct method is not {@code null}, otherwise
     * this method has no effect.
     *
     * @throws InvocationException for wrapping any error during execution of the method
     */
    void postConstruct() throws InvocationException;

    /**
     * Returns whether this resource is initialized, the post-construct method has also been executed normally.
     *
     * @return {@code true} if this resource is initialized; {@code false} otherwise
     */
    boolean isInitialized();

    /**
     * Returns the pre-destroy method of this resource.
     *
     * @return the pre-destroy method of this resource
     */
    @Nullable
    Method preDestroyMethod();

    /**
     * Invokes the pre-destroy method of this resource if the pre-destroy method is not {@code null}, otherwise this
     * method has no effect.
     *
     * @throws InvocationException for wrapping any error during execution of the method
     */
    void preDestroy() throws InvocationException;

    /**
     * Returns whether this resource is destroyed, the pre-destroy method has also been executed normally.
     *
     * @return {@code true} if this resource is destroyed; {@code false} otherwise
     */
    boolean isDestroyed();

    /**
     * Represents resolver for {@link InjectedResource}.
     */
    interface Resolver {

        /**
         * Resolves the descriptor of the resource.
         *
         * @param type                     the type of the resource
         * @param resourceAnnotations      the annotation class names of the resource, such as
         *                                 {@code {"javax.annotation.Resource", "jakarta.annotation.Resource"}}
         * @param postConstructAnnotations the annotation class names of the post-construct method, such as
         *                                 {@code {"javax.annotation.PostConstruct",
         *                                 "jakarta.annotation.PostConstruct"}}
         * @param preDestroyAnnotations    the annotation class names of the pre-destroy method, such as
         *                                 {@code {"javax.annotation.PreDestroy", "jakarta.annotation.PreDestroy"}}
         * @return the descriptor of the resource
         * @throws Exception for any error during resolving
         */
        @Nonnull
        Descriptor resolve(
            @Nonnull Type type,
            @Nonnull Collection<@Nonnull String> resourceAnnotations,
            @Nonnull Collection<@Nonnull String> postConstructAnnotations,
            @Nonnull Collection<@Nonnull String> preDestroyAnnotations
        ) throws Exception;
    }

    /**
     * Represents descriptor of resource type.
     */
    interface Descriptor {

        /**
         * Returns the type of the resource.
         *
         * @return the type of the resource
         */
        @Nonnull
        Type type();

        /**
         * Returns the raw class of the resource.
         *
         * @return the raw class of the resource
         */
        @Nonnull
        Class<?> rawClass();

        /**
         * Returns the post-construct method of the resource, may be {@code null} if there is no ost-construct method.
         *
         * @return the post-construct method of the resource, may be {@code null} if there is no ost-construct method
         */
        @Nullable
        Method postConstructMethod();

        /**
         * Returns the pre-destroy method of the resource, may be {@code null} if there is no pre-destroy method.
         *
         * @return the pre-destroy method of the resource, may be {@code null} if there is no pre-destroy method
         */
        @Nullable
        Method preDestroyMethod();

        /**
         * Returns the dependency fields of the resource.
         *
         * @return the dependency fields of the resource
         */
        @Nonnull
        @Immutable
        List<@Nonnull Field> dependencyFields();
    }

    /**
     * Represents setter for dependency field.
     */
    interface FieldSetter {

        /**
         * Sets the value to the dependency field.
         *
         * @param field the dependency field
         * @param owner the owner instance of the dependency field
         * @param value the value to be set
         * @throws Exception for any error during setting
         */
        void set(@Nonnull Field field, @Nonnull Object owner, @Nonnull Object value) throws Exception;
    }
}