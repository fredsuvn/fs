package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Top interface of {@link MapMeta} and {@link ObjectMeta}, presents the meta info of a data object.
 * <p>
 * A data object may be an instance of {@link Map} which can be introspected by {@link MapMetaManager}, or a non-map
 * object which can be introspected by {@link ObjectMetaManager}.
 *
 * @author sunqian
 */
@Immutable
public interface DataMeta {

    /**
     * Returns the type of the data object described by this {@link DataMeta}, typically is an instance of {@link Class}
     * or {@link ParameterizedType}.
     *
     * @return the type of the data object described by this {@link DataMeta}
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
     * Returns whether this meta is an instance of {@link MapMeta}.
     *
     * @return whether this meta is an instance of {@link MapMeta}.
     */
    boolean isMapMeta();

    /**
     * Returns this instance as an instance of {@link MapMeta}.
     *
     * @return this instance as an instance of {@link MapMeta}
     * @throws ClassCastException if this meta is not an instance of {@link MapMeta}
     */
    default @Nonnull MapMeta asMapMeta() throws ClassCastException {
        return (MapMeta) this;
    }

    /**
     * Returns whether this meta is an instance of {@link ObjectMeta}.
     *
     * @return whether this meta is an instance of {@link ObjectMeta}.
     */
    boolean isObjectMeta();

    /**
     * Returns this instance as an instance of {@link ObjectMeta}.
     *
     * @return this instance as an instance of{@link ObjectMeta}
     * @throws ClassCastException if this meta is not an instance of {@link ObjectMeta}
     */
    default @Nonnull ObjectMeta asObjectMeta() throws ClassCastException {
        return (ObjectMeta) this;
    }
}
