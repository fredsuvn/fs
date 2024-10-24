package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a static utilities class provides utilities for {@code bean}.
 *
 * @author fredsuvn
 */
public class JieBean {

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link BeanInfo}. This method
     * uses result of {@link BeanInfo#getType()} to compare. The code is similar to the following:
     * <pre>
     *     return Objects.equals(bean.getType(), other.getType());
     * </pre>
     * And it works in conjunction with {@link #hashCode(BeanInfo)}.
     *
     * @param beanInfo comparing bean info
     * @param o        object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(BeanInfo beanInfo, @Nullable Object o) {
        if (beanInfo == o) {
            return true;
        }
        if (o == null || !beanInfo.getClass().equals(o.getClass())) {
            return false;
        }
        BeanInfo other = (BeanInfo) o;
        return Objects.equals(beanInfo.getType(), other.getType());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link PropertyInfo}. This
     * method compares result of {@link Object#getClass()}, {@link PropertyInfo#getName()} and
     * {@link PropertyInfo#getOwner()}. The code is similar to the following:
     * <pre>
     *     return Objects.equals(info.getClass(), other.getClass())
     *         && Objects.equals(info.getName(), other.getName())
     *         && Objects.equals(info.getOwner(), other.getOwner());
     * </pre>
     * And it works in conjunction with {@link #hashCode(PropertyInfo)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(PropertyInfo info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        PropertyInfo other = (PropertyInfo) o;
        return Objects.equals(info.getName(), other.getName()) &&
            Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link MethodInfo}. This
     * method compares result of {@link Object#getClass()} and {@link MethodInfo#getMethod()}. The code is similar to
     * the following:
     * <pre>
     *     return Objects.equals(info.getMethod(), other.getMethod())
     *         && Objects.equals(info.getName(), other.getName());
     * </pre>
     * And it works in conjunction with {@link #hashCode(MethodInfo)}.
     *
     * @param info info to be compared
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    public static boolean equals(MethodInfo info, @Nullable Object o) {
        if (info == o) {
            return true;
        }
        if (o == null || !info.getClass().equals(o.getClass())) {
            return false;
        }
        MethodInfo other = (MethodInfo) o;
        return Objects.equals(info.getMethod(), other.getMethod())
            && Objects.equals(info.getOwner(), other.getOwner());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link BeanInfo}. This method uses
     * {@link Object#hashCode()} of {@link BeanInfo#getType()} to compute. The code is similar to the following:
     * <pre>
     *     return bean.getType().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(BeanInfo, Object)}.
     *
     * @param beanInfo bean info to be hashed
     * @return hash code of given bean
     */
    public static int hashCode(BeanInfo beanInfo) {
        return beanInfo.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link PropertyInfo}. This method
     * uses {@link Object#hashCode()} of {@link PropertyInfo#getName()} to compute. The code is similar to the
     * following:
     * <pre>
     *     return info.getName().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(PropertyInfo, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(PropertyInfo info) {
        return info.getName().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link MethodInfo}. This method
     * uses {@link Object#hashCode()} of {@link MethodInfo#getMethod()} to compute. The code is similar to the
     * following:
     * <pre>
     *     return info.getMethod().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(MethodInfo, Object)}.
     *
     * @param info info to be hashed
     * @return hash code of given info
     */
    public static int hashCode(MethodInfo info) {
        return info.getMethod().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link PropertyInfo}. The code is
     * similar to the following:
     * <pre>
     *     return info.getOwner().getType().getTypeName() + "." + info.getName()
     *         + "[" + info.getType().getTypeName() + "]";
     * </pre>
     *
     * @param info info to be string description
     * @return a string description for given info
     */
    public static String toString(PropertyInfo info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "[" + info.getType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link MethodInfo}. The code is
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
    public static String toString(MethodInfo info) {
        return info.getOwner().getType().getTypeName() + "." + info.getName()
            + "(" + Arrays.stream(info.getMethod().getGenericParameterTypes())
            .map(Type::getTypeName).collect(Collectors.joining(", ")) + ")["
            + info.getMethod().getGenericReturnType().getTypeName() + "]";
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link BeanInfo}. The code is
     * similar to the following:
     * <pre>
     *     return beanInfo.getType().getTypeName();
     * </pre>
     *
     * @param beanInfo bean info to be string description
     * @return a string description for given descriptor
     */
    public static String toString(BeanInfo beanInfo) {
        return beanInfo.getType().getTypeName();
    }

    /**
     * Tries to map unresolved type variables of properties of given bean info with extra type variable mapping. If no
     * mapping found, given bean info itself will be returned. Otherwise, a new bean info with extra mapping will be
     * returned.
     *
     * @param beanInfo            given bean info
     * @param extraTypeVarMapping extra type variable mapping
     * @return iven bean info itself or a new bean info with extra mapping
     * @throws BeanResolvingException if any problem occurs when resolving
     */
    public static BeanInfo withExtraTypeVariableMapping(
        BeanInfo beanInfo, @Nullable Map<TypeVariable<?>, Type> extraTypeVarMapping
    ) throws BeanResolvingException {
        if (JieColl.isNotEmpty(extraTypeVarMapping)) {
            Map<PropertyInfo, Type> mapping = new HashMap<>();
            Set<Type> stack = new HashSet<>();
            beanInfo.getProperties().forEach((n, p) -> {
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
                return new BeanInfoWrapper(beanInfo, mapping);
            }
        }
        return beanInfo;
    }

    private static final class BeanInfoWrapper implements BeanInfo {

        private final BeanInfo origin;
        private final Map<String, PropertyInfo> props;

        private BeanInfoWrapper(BeanInfo origin, Map<PropertyInfo, Type> mapping) {
            this.origin = origin;
            Map<String, PropertyInfo> newProps = new LinkedHashMap<>();
            origin.getProperties().forEach((n, p) -> {
                Type newType = mapping.get(p);
                if (newType != null) {
                    newProps.put(n, new PropertyInfoWrapper(p, newType));
                    return;
                }
                newProps.put(n, new PropertyInfoWrapper(p, p.getType()));
            });
            this.props = Collections.unmodifiableMap(newProps);
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
        public @Immutable Map<String, PropertyInfo> getProperties() {
            return props;
        }

        @Override
        public @Nullable PropertyInfo getProperty(String name) {
            return props.get(name);
        }

        @Override
        public @Immutable List<MethodInfo> getMethods() {
            return origin.getMethods();
        }

        @Override
        public @Nullable MethodInfo getMethod(String name, Class<?>... parameterTypes) {
            return origin.getMethod(name, parameterTypes);
        }

        @Override
        public boolean equals(Object o) {
            return JieBean.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieBean.hashCode(this);
        }

        @Override
        public String toString() {
            return JieBean.toString(this);
        }

        private final class PropertyInfoWrapper implements PropertyInfo {

            private final PropertyInfo prop;
            private final Type type;

            private PropertyInfoWrapper(PropertyInfo prop, Type type) {
                this.prop = prop;
                this.type = type;
            }

            @Override
            public BeanInfo getOwner() {
                return BeanInfoWrapper.this;
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
            public <A extends Annotation> @Nullable A getAnnotation(Class<A> annotationType) {
                return prop.getAnnotation(annotationType);
            }

            @Override
            public @Nullable Object getValue(Object bean) {
                return prop.getValue(bean);
            }

            @Override
            public void setValue(Object bean, @Nullable Object value) {
                prop.setValue(bean, value);
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
                return JieBean.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieBean.hashCode(this);
            }

            @Override
            public String toString() {
                return JieBean.toString(this);
            }
        }
    }
}
