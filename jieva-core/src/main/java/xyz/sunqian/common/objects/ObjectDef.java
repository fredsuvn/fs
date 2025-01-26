package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.reflect.JieReflect;

import java.beans.BeanInfo;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface represents the definition of an object, introspected by {@link ObjectIntrospector}, and provides
 * definitions of its properties and methods.
 * <p>
 * It is very similar to {@link BeanInfo}, which describes
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but it follows simpler
 * rules. An {@link ObjectDef} typically consists of a set of properties and methods, and does not include indexed
 * properties, listeners, events, or additional specific constraints.
 *
 * @author sunqian
 */
@Immutable
@ThreadSafe
public interface ObjectDef {

    /**
     * Returns an {@link ObjectDef} of specified type, with default introspector.
     * <p>
     * This method caches the results of introspection and, on next invocations, returns the cached result if it is
     * still valid.
     *
     * @param type specified type
     * @return an {@link ObjectDef} of specified type, with default introspector
     */
    static ObjectDef get(Type type) {
        return get(type, null);
    }

    /**
     * Returns an {@link ObjectDef} of specified type, with specified introspector.
     * <p>
     * This method caches the results of introspection and, on next invocations, returns the cached result if it is
     * still valid.
     *
     * @param type         specified type
     * @param introspector specified introspector
     * @return an {@link ObjectDef} of specified type, with specified introspector
     */
    static ObjectDef get(Type type, @Nullable ObjectIntrospector introspector) {
        return Impls.getObjectDef(type, introspector);
    }

    /**
     * Returns the introspector of this {@link ObjectDef}.
     *
     * @return introspector of this {@link ObjectDef}
     */
    ObjectIntrospector getIntrospector();

    /**
     * Returns type of this {@link ObjectDef}, should be a {@link Class} or a {@link ParameterizedType}.
     *
     * @return type of this {@link ObjectDef}, should be a {@link Class} or a {@link ParameterizedType}
     */
    Type getType();

    /**
     * Returns raw type of this {@link ObjectDef}.
     *
     * @return raw type of this {@link ObjectDef}
     */
    default Class<?> getRawType() {
        return JieReflect.getRawType(getType());
    }

    /**
     * Returns definition map of properties of this {@link ObjectDef}.
     *
     * @return definition map of properties of this {@link ObjectDef}
     */
    @Immutable
    Map<String, PropertyDef> getProperties();

    /**
     * Returns property definition of this {@link ObjectDef}, with specified name.
     *
     * @param name specified name
     * @return property definition of this {@link ObjectDef}, with specified name
     */
    @Nullable
    default PropertyDef getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * Returns definition list of methods of this {@link ObjectDef}.
     *
     * @return definition list of methods of this {@link ObjectDef}
     */
    @Immutable
    List<MethodDef> getMethods();

    /**
     * Returns method definition of this {@link ObjectDef}, with given name and parameter types.
     *
     * @param name           given name
     * @param parameterTypes given parameter types
     * @return method definition of this {@link ObjectDef}, with given name and parameter types
     */
    @Nullable
    MethodDef getMethod(String name, Class<?>... parameterTypes);
}
