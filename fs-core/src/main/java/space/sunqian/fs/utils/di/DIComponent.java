package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.InvocationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Represents component of {@link DIContainer}, consisting of a type and a singleton instance of that type.
 * <p>
 * {@link DIComponent} is distinguished by {@link #type()}, two {@link DIComponent} instances with same raw class but
 * different generic type parameters are considered as two different components.
 *
 * @author sunqian
 */
public interface DIComponent {

    /**
     * Returns the default resolver for {@link DIComponent}.
     *
     * @return the default resolver for {@link DIComponent}
     */
    static @Nonnull Resolver defaultResolver() {
        return DIContainerImpl.Resolver.INST;
    }

    /**
     * Returns the default field setter for {@link DIComponent}.
     *
     * @return the default field setter for {@link DIComponent}
     */
    static @Nonnull FieldSetter defaultFieldSetter() {
        return DIContainerImpl.FieldSetter.INST;
    }

    /**
     * Returns the type of this component.
     *
     * @return the type of this component
     */
    @Nonnull
    Type type();

    /**
     * Returns the singleton instance of this component's type.
     *
     * @return the singleton instance of this component's type
     */
    @Nonnull
    Object instance();

    /**
     * Returns whether this component is directly owned by the current container, rather than inherited from parent
     * containers.
     *
     * @return {@code true} if this component is directly owned by the current container, rather than inherited from
     * parent containers; {@code false} otherwise
     */
    boolean isLocal();

    /**
     * Returns the dependencies of this component.
     *
     * @return the dependencies of this component
     */
    @Nonnull
    @Immutable
    List<@Nonnull DIComponent> dependencies();

    /**
     * Returns the post-construct method of this component.
     *
     * @return the post-construct method of this component
     */
    @Nullable
    Method postConstructMethod();

    /**
     * Returns the dependencies of this component for post-construct method.
     *
     * @return the dependencies of this component for post-construct method
     */
    @Nonnull
    @Immutable
    List<@Nonnull DIComponent> postConstructDependencies();

    /**
     * Invokes the post-construct method of this component if the post-construct method is not {@code null}, otherwise
     * this method has no effect. Note this method ignore the dependencies of the post-construct method.
     *
     * @throws InvocationException for wrapping any error during execution of the method
     */
    void postConstruct() throws InvocationException;

    /**
     * Returns whether this component is initialized, the post-construct method has also been executed normally.
     *
     * @return {@code true} if this component is initialized; {@code false} otherwise
     */
    boolean isInitialized();

    /**
     * Returns the pre-destroy method of this component.
     *
     * @return the pre-destroy method of this component
     */
    @Nullable
    Method preDestroyMethod();

    /**
     * Returns the dependencies of this component for pre-destroy method.
     *
     * @return the dependencies of this component for pre-destroy method
     */
    @Nonnull
    @Immutable
    List<@Nonnull DIComponent> preDestroyDependencies();

    /**
     * Invokes the pre-destroy method of this component if the pre-destroy method is not {@code null}, otherwise this
     * method has no effect. Note this method ignore the dependencies of the pre-destroy method.
     *
     * @throws InvocationException for wrapping any error during execution of the method
     */
    void preDestroy() throws InvocationException;

    /**
     * Returns whether this component is destroyed, the pre-destroy method has also been executed normally.
     *
     * @return {@code true} if this component is destroyed; {@code false} otherwise
     */
    boolean isDestroyed();

    /**
     * Represents resolver for {@link DIComponent}.
     */
    interface Resolver {

        /**
         * Resolves the descriptor of the component.
         *
         * @param type                     the type of the component
         * @param componentAnnotations     The class name of the annotations to mark the component, such as:
         *                                 {@code javax.annotation.Resource}, {@code jakarta.annotation.Resource}.
         * @param postConstructAnnotations The class name of the annotations to mark the post-construct method, such as:
         *                                 {@code javax.annotation.PostConstruct},
         *                                 {@code jakarta.annotation.PostConstruct}.
         * @param preDestroyAnnotations    The class name of the annotations to mark the pre-destroy method, such as:
         *                                 {@code javax.annotation.PreDestroy}, {@code jakarta.annotation.PreDestroy}.
         * @return the descriptor of the component
         * @throws Exception for any error during resolving
         */
        @Nonnull
        Descriptor resolve(
            @Nonnull Type type,
            @Nonnull Collection<@Nonnull String> componentAnnotations,
            @Nonnull Collection<@Nonnull String> postConstructAnnotations,
            @Nonnull Collection<@Nonnull String> preDestroyAnnotations
        ) throws Exception;
    }

    /**
     * Represents descriptor of component type.
     */
    interface Descriptor {

        /**
         * Returns the type of the component.
         *
         * @return the type of the component
         */
        @Nonnull
        Type type();

        /**
         * Returns the raw class of the component.
         *
         * @return the raw class of the component
         */
        @Nonnull
        Class<?> rawClass();

        /**
         * Returns the post-construct method of the component, may be {@code null} if there is no post-construct
         * method.
         *
         * @return the post-construct method of the component, may be {@code null} if there is no post-construct method
         */
        @Nullable
        Method postConstructMethod();

        /**
         * Returns the pre-destroy method of the component, may be {@code null} if there is no pre-destroy method.
         *
         * @return the pre-destroy method of the component, may be {@code null} if there is no pre-destroy method
         */
        @Nullable
        Method preDestroyMethod();

        /**
         * Returns the dependency fields of the component.
         *
         * @return the dependency fields of the component
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