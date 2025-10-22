package xyz.sunqian.common.app;

import xyz.sunqian.annotations.Nonnull;

import java.lang.reflect.Type;

/**
 * Represents resource of a {@link SimpleApp}.
 *
 * @author sunqian
 */
public interface SimpleResource {

    /**
     * Returns the type of this resource.
     *
     * @return the type of this resource
     */
    @Nonnull
    Type type();
}
