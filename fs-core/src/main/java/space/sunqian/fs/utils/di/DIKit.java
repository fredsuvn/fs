package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dependency injection utilities.
 *
 * @author sunqian
 */
public class DIKit {

    /**
     * Checks whether the cycle dependencies exist in the given component.
     *
     * @param component the component to check
     * @throws DIException if the cycle dependencies exist
     */
    public static <T> void checkCycleDependencies(
        @Nonnull T component,
        @Nonnull DependenciesFunction<@Nonnull T> dependenciesFunction
    ) throws DIException {
        if (dependenciesFunction.dependencies(component).isEmpty()) {
            return;
        }
        Set<T> stack = new LinkedHashSet<>();
        stack.add(component);
        checkCycleDependencies(component, stack, dependenciesFunction);
    }

    private static <T> void checkCycleDependencies(
        @Nonnull T component,
        @Nonnull Set<@Nonnull T> stack,
        @Nonnull DependenciesFunction<@Nonnull T> dependenciesFunction
    ) throws DIException {
        if (dependenciesFunction.dependencies(component).isEmpty()) {
            return;
        }
        for (T dependency : dependenciesFunction.dependencies(component)) {
            if (dependency.equals(component)) {
                continue;
            }
            if (stack.contains(dependency)) {
                throw new DIException("Cycle dependency: " + toCycleString(dependency, stack) + ".");
            }
            stack.add(dependency);
            checkCycleDependencies(dependency, stack, dependenciesFunction);
            stack.remove(dependency);
        }
    }

    private static @Nonnull String toCycleString(
        @Nonnull Object component,
        @Nonnull Set<?> stack
    ) {
        return stack.stream()
            .map(Object::toString)
            .collect(Collectors.joining(" -> "))
            + " -> " + component;
    }

    /**
     * Represents function to get dependencies of a component.
     *
     * @param <T> the type of the component
     */
    public interface DependenciesFunction<T> {

        /**
         * Returns the dependencies of the given component.
         *
         * @param component the component to get dependencies
         * @return the dependencies of the given component
         */
        @Nonnull
        @Immutable
        List<? extends @Nonnull T> dependencies(@Nonnull T component);
    }

    private DIKit() {
    }
}