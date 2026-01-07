package space.sunqian.fs.di;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;

import java.util.List;

/**
 * Represents a component that depends on other components.
 *
 * @param <T> the type of the component
 * @author sunqian
 */
public interface DependentComponent<T extends DependentComponent<T>> {

    /**
     * Returns the dependencies of this component.
     *
     * @return the dependencies of this component
     */
    @Nonnull
    @Immutable
    List<@Nonnull T> dependencies();
}
