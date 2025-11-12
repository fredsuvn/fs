package space.sunqian.common.object.data;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.Fs;
import space.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Top interface of {@link MapSchema} and {@link ObjectSchema}, presents the schema of a data object.
 * <p>
 * A data object may be an instance of {@link Map} parsed by {@link MapSchemaParser}, or a non-map object parsed by
 * {@link ObjectSchemaParser}.
 *
 * @author sunqian
 */
@Immutable
public interface DataSchema {

    /**
     * Returns the type of the data object described by this {@link DataSchema}, typically is an instance of
     * {@link Class} or {@link ParameterizedType}.
     *
     * @return the type of the data object described by this {@link DataSchema}
     */
    @Nonnull
    Type type();

    /**
     * Returns the raw type of the {@link #type()}. The default implementation is:
     * <pre>{@code
     * return Kit.nonnull(TypeKit.getRawClass(type()), Object.class);
     * }</pre>
     *
     * @return the raw type of the {@link #type()}
     */
    default @Nonnull Class<?> rawType() {
        return Fs.nonnull(TypeKit.getRawClass(type()), Object.class);
    }

    /**
     * Returns whether this schema is an instance of {@link MapSchema}.
     *
     * @return whether this schema is an instance of {@link MapSchema}.
     */
    boolean isMapSchema();

    /**
     * Returns this instance as an instance of {@link MapSchema}.
     *
     * @return this instance as an instance of {@link MapSchema}
     * @throws ClassCastException if this schema is not an instance of {@link MapSchema}
     */
    default @Nonnull MapSchema asMapSchema() throws ClassCastException {
        return (MapSchema) this;
    }

    /**
     * Returns whether this schema is an instance of {@link ObjectSchema}.
     *
     * @return whether this schema is an instance of {@link ObjectSchema}.
     */
    boolean isObjectSchema();

    /**
     * Returns this instance as an instance of {@link ObjectSchema}.
     *
     * @return this instance as an instance of{@link ObjectSchema}
     * @throws ClassCastException if this schema is not an instance of {@link ObjectSchema}
     */
    default @Nonnull ObjectSchema asObjectSchema() throws ClassCastException {
        return (ObjectSchema) this;
    }
}
