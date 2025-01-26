package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a static utilities class provides utilities for definitions such as {@link ObjectDef}, {@link PropertyDef}
 * and {@link MethodDef}.
 *
 * @author fredsuvn
 */
public class JieDef {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link ObjectDef}. This method
     * uses result of {@link ObjectDef#getType()} to compare. The code is similar to the following:
     * <pre>
     *     return Objects.equals(bean.getType(), other.getType());
     * </pre>
     * And it works in conjunction with {@link #hashCode(ObjectDef)}.
     *
     * @param objectDef comparing bean info
     * @param o         object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(ObjectDef objectDef, @Nullable Object o) {
        if (objectDef == o) {
            return true;
        }
        if (o == null || !objectDef.getClass().equals(o.getClass())) {
            return false;
        }
        ObjectDef other = (ObjectDef) o;
        return Objects.equals(objectDef.getType(), other.getType());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link PropertyDef}. This
     * method compares result of {@link Object#getClass()}, {@link PropertyDef#getName()} and
     * {@link PropertyDef#getOwner()}. The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getClass(), other.getClass())
     *         && Objects.equals(info.getName(), other.getName())
     *         && Objects.equals(info.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(PropertyDef)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(PropertyDef info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        PropertyDef other = (PropertyDef) o;
        return Objects.equals(info.getName(), other.getName()) &&
            Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link MethodDef}. This method
     * compares result of {@link Object#getClass()} and {@link MethodDef#getMethod()}. The code is similar to the
     * following:
     * <pre>
     *     return Objects.equals(info.getMethod(), other.getMethod())
     *         && Objects.equals(info.getName(), other.getName());
     * </pre>
     * And it works in conjunction with {@link #hashCode(MethodDef)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(MethodDef info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        MethodDef other = (MethodDef) o;
        return Objects.equals(info.getMethod(), other.getMethod())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link ObjectDef}. This method
     * uses {@link Object#hashCode()} of {@link ObjectDef#getType()} to compute. The code is similar to the following:
     * <pre>
     *     return bean.getType().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(ObjectDef, Object)}.
     *
     * @param objectDef bean info to be hashed
     * @return hash code of given bean
     */
    public static int hashCode(ObjectDef objectDef) {
        return objectDef.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link PropertyDef}. This method
     * uses {@link Object#hashCode()} of {@link PropertyDef#getName()} to compute. The code is similar to the
     * following:
     * <pre>
     *     return info.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(PropertyDef, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(PropertyDef info) {
        return info.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link MethodDef}. This method
     * uses {@link Object#hashCode()} of {@link MethodDef#getMethod()} to compute. The code is similar to the
     * following:
     * <pre>
     *     return info.getMethod().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(MethodDef, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(MethodDef info) {
        return info.getMethod().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link PropertyDef}. The code is
     * similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "[" + info.getType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    public static String toString(PropertyDef info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "[" + info.getType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link MethodDef}. The code is
     * similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "(" + Arrays.stream(info.getMethod().getGenericParameterTypes())
     *         .map(Type::getTypeName).collect(Collectors.joining(", ")) + ")["
     *         + info.getMethod().getGenericReturnType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    public static String toString(MethodDef info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "(" + Arrays.stream(info.getMethod().getGenericParameterTypes())
            .map(Type::getTypeName).collect(Collectors.joining(", ")) + ")["
            + info.getMethod().getGenericReturnType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link ObjectDef}. The code is
     * similar to the following:
     * <pre>
     *     return beanInfo.getType().getTypeName();
     * </pre>
     *
     * @param objectDef bean info to be string description
     * @return a string description for given descriptor
     */
    public static String toString(ObjectDef objectDef) {
        return objectDef.getType().getTypeName();
    }

    /**
     * Tries to map unresolved type variables of properties of given bean info with extra type variable mapping. If no
     * mapping found, given bean info itself will be returned. Otherwise, a new bean info with extra mapping will be
     * returned.
     *
     * @param objectDef           given bean info
     * @param extraTypeVarMapping extra type variable mapping
     * @return iven bean info itself or a new bean info with extra mapping
     * @throws ObjectIntrospectionException if any problem occurs when resolving
     */
    public static ObjectDef withExtraTypeVariableMapping(
        ObjectDef objectDef, @Nullable Map<TypeVariable<?>, Type> extraTypeVarMapping
    ) throws ObjectIntrospectionException {
        if (JieColl.isNotEmpty(extraTypeVarMapping)) {
            Map<PropertyDef, Type> mapping = new HashMap<>();
            Set<Type> stack = new HashSet<>();
            objectDef.getProperties().forEach((n, p) -> {
                Type pt = p.getType();
                if (pt instanceof TypeVariable) {
                    stack.clear();
                    Type newType = JieColl.getRecursive(extraTypeVarMapping, pt, stack);
                    if (newType != null) {
                        mapping.put(p, newType);
                    }
                }
            });
            if (!mapping.isEmpty()) {
                return new ObjectDefWrapper(objectDef, mapping);
            }
        }
        return objectDef;
    }

    private static final class ObjectDefWrapper implements ObjectDef {

        private final ObjectDef origin;
        private final Map<String, PropertyDef> props;

        private ObjectDefWrapper(ObjectDef origin, Map<PropertyDef, Type> mapping) {
            this.origin = origin;
            Map<String, PropertyDef> newProps = new LinkedHashMap<>();
            origin.getProperties().forEach((n, p) -> {
                Type newType = mapping.get(p);
                if (newType != null) {
                    newProps.put(n, new PropertyDefWrapper(p, newType));
                    return;
                }
                newProps.put(n, new PropertyDefWrapper(p, p.getType()));
            });
            this.props = Collections.unmodifiableMap(newProps);
        }

        @Override
        public ObjectIntrospector getIntrospector() {
            return origin.getIntrospector();
        }

        @Override
        public Type getType() {
            return origin.getType();
        }

        @Override
        public Class<?> getRawType() {
            return origin.getRawType();
        }

        @Override
        public @Immutable Map<String, PropertyDef> getProperties() {
            return props;
        }

        @Override
        public @Nullable PropertyDef getProperty(String name) {
            return props.get(name);
        }

        @Override
        public @Immutable List<MethodDef> getMethods() {
            return origin.getMethods();
        }

        @Override
        public @Nullable MethodDef getMethod(String name, Class<?>... parameterTypes) {
            return origin.getMethod(name, parameterTypes);
        }

        @Override
        public boolean equals(Object o) {
            return JieDef.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieDef.hashCode(this);
        }

        @Override
        public String toString() {
            return JieDef.toString(this);
        }

        private final class PropertyDefWrapper implements PropertyDef {

            private final PropertyDef prop;
            private final Type type;

            private PropertyDefWrapper(PropertyDef prop, Type type) {
                this.prop = prop;
                this.type = type;
            }

            @Override
            public ObjectDef getOwner() {
                return ObjectDefWrapper.this;
            }

            @Override
            public String getName() {
                return prop.getName();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return prop.getAnnotations();
            }

            @Override
            public <A extends Annotation> @Nullable A getAnnotation(Class<A> type) {
                return prop.getAnnotation(type);
            }

            @Override
            public @Nullable Object getValue(Object inst) {
                return prop.getValue(inst);
            }

            @Override
            public void setValue(Object inst, @Nullable Object value) {
                prop.setValue(inst, value);
            }

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public @Nullable Class<?> getRawType() {
                return JieReflect.getRawType(type);
            }

            @Override
            public @Nullable Method getGetter() {
                return prop.getGetter();
            }

            @Override
            public @Nullable Method getSetter() {
                return prop.getSetter();
            }

            @Override
            public @Nullable Field getField() {
                return prop.getField();
            }

            @Override
            public List<Annotation> getFieldAnnotations() {
                return prop.getFieldAnnotations();
            }

            @Override
            public List<Annotation> getGetterAnnotations() {
                return prop.getGetterAnnotations();
            }

            @Override
            public List<Annotation> getSetterAnnotations() {
                return prop.getSetterAnnotations();
            }

            @Override
            public boolean isReadable() {
                return prop.isReadable();
            }

            @Override
            public boolean isWriteable() {
                return prop.isWriteable();
            }

            @Override
            public boolean equals(Object o) {
                return JieDef.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieDef.hashCode(this);
            }

            @Override
            public String toString() {
                return JieDef.toString(this);
            }
        }
    }
}
