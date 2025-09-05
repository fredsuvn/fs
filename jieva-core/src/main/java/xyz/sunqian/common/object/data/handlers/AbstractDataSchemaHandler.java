package xyz.sunqian.common.object.data.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.MapKit;
import xyz.sunqian.common.object.data.DataPropertyBase;
import xyz.sunqian.common.object.data.DataSchemaParser;
import xyz.sunqian.common.runtime.invoke.Invocable;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a skeletal implementation of the {@link DataSchemaParser.Handler} to minimize the effort required to
 * implement the interface.
 * <p>
 * This class uses {@link Class#getMethods()} to find out all methods (the synthetic method will be filtered out), then
 * passes each of them to {@link #resolveAccessor(Method)} to resolve property accessor infos. This class will perform
 * subsequent parsing based on those property accessor infos, the subclasses only needs to implement the
 * {@link #resolveAccessor(Method)}.
 *
 * @author sunqian
 */
public abstract class AbstractDataSchemaHandler implements DataSchemaParser.Handler {

    private static Type findActualType(
        Type type,
        Map<TypeVariable<?>, Type> typeParameterMapping,
        Set<Type> stack
    ) {
        if (type instanceof Class) {
            return type;
        }
        stack.clear();
        Type result = MapKit.resolveChain(typeParameterMapping, type, stack);
        if (result != null) {
            return result;
        }
        return type;
    }

    @Nullable
    private static Field findField(String name, Class<?> type) {
        try {
            return type.getField(name);
        } catch (NoSuchFieldException e) {
            Class<?> cur = type;
            while (cur != null) {
                try {
                    return cur.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    cur = cur.getSuperclass();
                }
            }
        }
        return null;
    }

    @Override
    public boolean parse(@Nonnull DataSchemaParser.Context context) throws Exception {
        Type type = context.dataType();
        Class<?> rawType = TypeKit.getRawClass(type);
        if (rawType == null) {
            throw new UnsupportedOperationException("Not a Class or ParameterizedType: " + type + ".");
        }
        Method[] methods = rawType.getMethods();
        Map<String, PropertyInfo> propertyInfoMap = new LinkedHashMap<>();

        // Builds property info for each method.
        for (Method method : methods) {
            if (method.isSynthetic()) {
                continue;
            }
            AccessorInfo accessorInfo = resolveAccessor(method);
            if (accessorInfo == null) {
                continue;
            }
            String propertyName = accessorInfo.propertyName();
            PropertyInfo propertyInfo = propertyInfoMap.computeIfAbsent(propertyName, PropertyInfo::new);
            if (accessorInfo.isGetter()) {
                propertyInfo.getterMethod = method;
                propertyInfo.getter = accessorInfo.accessor();
            } else {
                propertyInfo.setterMethod = method;
                propertyInfo.setter = accessorInfo.accessor();
            }
        }

        // Builds property base for each property info.
        Map<TypeVariable<?>, Type> typeParameterMapping = TypeKit.mapTypeParameters(context.dataType());
        Set<Type> stack = new HashSet<>();
        propertyInfoMap.forEach((propertyName, propertyInfo) -> {
            Method getterMethod = propertyInfo.getterMethod;
            Type getterType = getterMethod == null ? null :
                findActualType(getterMethod.getGenericReturnType(), typeParameterMapping, stack);
            Method setterMethod = propertyInfo.setterMethod;
            Type setterType = setterMethod == null ? null :
                findActualType(setterMethod.getGenericParameterTypes()[0], typeParameterMapping, stack);
            /*
            The property's type is the return type of getter or the parameter type of setter.
            If the getter's return type and the setter's parameter type are not equal, the getter's return type is used,
            and the setter method will no longer be considered as the setter for this property.
             */
            Type propertyType = getterType != null ? getterType : setterType;
            Field field = findField(propertyName, rawType);
            propertyInfo.type = Jie.nonnull(propertyType, Object.class);
            propertyInfo.field = field;
            context.propertyBaseMap().put(propertyName, propertyInfo);
        });
        return true;
    }

    /**
     * Resolves and returns the given method to an accessor info, or {@code null} if the given method is not a data
     * property.
     *
     * @param method the given method
     * @return the accessor info resolved from the given method, or {@code null} if the given method is not a data
     * property
     */
    protected abstract @Nullable AccessorInfo resolveAccessor(@Nonnull Method method);

    /**
     * Property accessor info, resolved from the specified {@link Method}.
     *
     * @author sunqian
     */
    public interface AccessorInfo {

        /**
         * Returns the property name.
         *
         * @return the property name
         */
        @Nonnull
        String propertyName();

        /**
         * Returns the accessor for the specified {@link Method}.
         *
         * @return the accessor for the specified {@link Method}
         */
        @Nonnull
        Invocable accessor();

        /**
         * Returns {@code true} if the specified {@link Method} is a getter method, {@code false} for setter.
         *
         * @return {@code true} if the specified {@link Method} is a getter method, {@code false} for setter.
         */
        boolean isGetter();
    }

    private static final class PropertyInfo implements DataPropertyBase {

        private final @Nonnull String name;
        private @Nonnull Type type = Object.class;
        private @Nullable Field field;

        private @Nullable Method getterMethod;
        private @Nullable Invocable getter;
        private @Nullable Method setterMethod;
        private @Nullable Invocable setter;

        private PropertyInfo(@Nonnull String name) {
            this.name = name;
        }

        @Override
        public @Nonnull String name() {
            return name;
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nullable Method getterMethod() {
            return getterMethod;
        }

        @Override
        public @Nullable Method setterMethod() {
            return setterMethod;
        }

        @Override
        public @Nullable Field field() {
            return field;
        }

        @Override
        public @Nullable Invocable getter() {
            return getter;
        }

        @Override
        public @Nullable Invocable setter() {
            return setter;
        }
    }
}
