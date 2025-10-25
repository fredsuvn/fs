package space.sunqian.common.object.data.handlers;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.collect.MapKit;
import space.sunqian.common.object.data.ObjectPropertyBase;
import space.sunqian.common.object.data.ObjectSchemaParser;
import space.sunqian.common.runtime.invoke.Invocable;
import space.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a skeletal implementation of the {@link ObjectSchemaParser.Handler} to minimize the effort required to
 * implement the interface.
 * <p>
 * This class uses {@link Class#getMethods()} to find out all methods (the synthetic method will be filtered out), then
 * passes each of them to {@link #resolveAccessor(Method)} to resolve property accessor infos. This class will perform
 * subsequent parsing based on those property accessor infos, the subclasses only needs to implement the
 * {@link #resolveAccessor(Method)}.
 *
 * @author sunqian
 */
public abstract class AbstractObjectSchemaHandler implements ObjectSchemaParser.Handler {

    private static @Nonnull Type findActualType(
        @Nonnull Type type,
        @Nonnull Map<@Nonnull TypeVariable<?>, @Nonnull Type> typeParameterMapping,
        @Nonnull Set<@Nonnull Type> stack
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

    private static @Nullable Field findField(@Nonnull String name, @Nonnull Class<?> type) {
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
    public boolean parse(@Nonnull ObjectSchemaParser.Context context) throws Exception {
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
        Map<TypeVariable<?>, Type> typeParameterMapping = TypeKit.typeParametersMapping(context.dataType());
        Set<Type> stack = new HashSet<>();
        propertyInfoMap.forEach((propertyName, propertyInfo) -> {
            Method getterMethod = propertyInfo.getterMethod;
            Type propertyType = getterMethod == null ? null :
                findActualType(getterMethod.getGenericReturnType(), typeParameterMapping, stack);
            Method setterMethod = propertyInfo.setterMethod;
            if (propertyType == null) {
                propertyType = findActualType(
                    Kit.asNonnull(setterMethod).getGenericParameterTypes()[0], typeParameterMapping, stack);
            }
            /*
            The property's type is the return type of getter or the parameter type of setter.
            If the getter's return type and the setter's parameter type are not equal, the getter's return type is used,
            and the setter method will no longer be considered as the setter for this property.
             */
            Field field = findField(propertyName, rawType);
            propertyInfo.type = propertyType;
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

    private static final class PropertyInfo implements ObjectPropertyBase {

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
