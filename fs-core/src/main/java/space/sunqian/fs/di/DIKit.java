package space.sunqian.fs.di;

import space.sunqian.annotation.Nonnull;

import java.util.LinkedHashSet;
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
    public static <T extends DependentComponent<T>> void checkCycleDependencies(
        @Nonnull T component
    ) throws DIException {
        if (component.dependencies().isEmpty()) {
            return;
        }
        Set<T> stack = new LinkedHashSet<>();
        stack.add(component);
        checkCycleDependencies(component, stack);
    }

    private static <T extends DependentComponent<T>> void checkCycleDependencies(
        @Nonnull T component,
        @Nonnull Set<@Nonnull T> stack
    ) throws DIException {
        if (component.dependencies().isEmpty()) {
            return;
        }
        for (T dependency : component.dependencies()) {
            if (dependency.equals(component)) {
                continue;
            }
            if (stack.contains(dependency)) {
                throw new DIException("Cycle dependency: " + toCycleString(dependency, stack) + ".");
            }
            stack.add(dependency);
            checkCycleDependencies(dependency, stack);
            stack.remove(dependency);
        }
    }

    private static @Nonnull String toCycleString(
        @Nonnull DependentComponent<?> component,
        @Nonnull Set<? extends @Nonnull DependentComponent<?>> stack
    ) {
        return stack.stream()
            .map(DependentComponent::toString)
            .collect(Collectors.joining(" -> "))
            + " -> " + component;
    }
}