package space.sunqian.fs.object.meta.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.meta.ObjectMetaManager;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a skeletal implementation of {@link ObjectMetaManager.Handler} to minimize the effort required to implement
 * the interface.
 * <p>
 * This class uses {@link Class#getMethods()} to find out all methods (the synthetic method will be filtered out), then
 * passes each of them to {@link #resolveAccessor(Method)} to resolve property accessor infos. This class will perform
 * subsequent parsing based on those property accessor infos, the subclasses only needs to implement the
 * {@link #resolveAccessor(Method)}.
 *
 * @author sunqian
 */
public abstract class AbstractObjectMetaHandler implements ObjectMetaManager.Handler {

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
    public boolean parse(ObjectMetaManager.@Nonnull Context context) throws Exception {
        Type type = context.parsedType();
        Class<?> rawType = TypeKit.getRawClass(type);
        if (rawType == null) {
            throw new UnsupportedOperationException("Not a Class or ParameterizedType: " + type + ".");
        }
        Method[] methods = rawType.getMethods();
        Map<String, PropertyMetaBase> propertyInfoMap = new LinkedHashMap<>();

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
            PropertyMetaBase propertyBase = propertyInfoMap.computeIfAbsent(propertyName, PropertyMetaBase::new);
            if (accessorInfo.isGetter()) {
                propertyBase.getterMethod = method;
                propertyBase.getter = accessorInfo.accessor();
            } else {
                propertyBase.setterMethod = method;
                propertyBase.setter = accessorInfo.accessor();
            }
        }

        // Builds property base for each property info.
        Map<TypeVariable<?>, Type> typeParameterMapping = TypeKit.typeParametersMapping(context.parsedType());
        Set<Type> stack = new HashSet<>();
        propertyInfoMap.forEach((propertyName, propertyBase) -> {
            Method getterMethod = propertyBase.getterMethod;
            Type propertyType = getterMethod == null ? null :
                findActualType(getterMethod.getGenericReturnType(), typeParameterMapping, stack);
            Method setterMethod = propertyBase.setterMethod;
            if (propertyType == null) {
                propertyType = findActualType(
                    Fs.asNonnull(setterMethod).getGenericParameterTypes()[0], typeParameterMapping, stack);
            }
            /*
            The property's type is the return type of getter or the parameter type of setter.
            If the getter's return type and the setter's parameter type are not equal, the getter's return type is used,
            and the setter method will no longer be considered as the setter for this property.
             */
            Field field = findField(propertyName, rawType);
            propertyBase.type = propertyType;
            propertyBase.field = field;
            context.propertyBaseMap().put(propertyName, propertyBase);
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

    private static final class PropertyMetaBase implements space.sunqian.fs.object.meta.PropertyMetaBase {

        private final @Nonnull String name;
        private @Nonnull Type type = Object.class;
        private @Nullable Field field;

        private @Nullable Method getterMethod;
        private @Nullable Invocable getter;
        private @Nullable Method setterMethod;
        private @Nullable Invocable setter;

        private PropertyMetaBase(@Nonnull String name) {
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
