package xyz.sunqian.common.objects.data.handlers;

import lombok.Data;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieMap;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.objects.data.DataObjectException;
import xyz.sunqian.common.objects.data.DataPropertyBase;
import xyz.sunqian.common.objects.data.DataSchemaParser;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This is a skeletal implementation of the {@link DataSchemaParser.Handler} to minimize the effort required to
 * implement the interface.
 * <p>
 * This abstract implementation uses {@link Class#getMethods()} to find out all methods, then puts each of them into
 * {@link #resolveAccessor(Method)} to resolve the property accessor info, and finally generates the
 * {@link DataPropertyBase} objects. The property's type is primarily determined by the getter's return type. If the
 * getter is {@code null}, the setter's parameter type is used instead. If the getter's return type and the setter's
 * parameter type are not equal, the getter's return type is used, and the setter method will no longer be considered as
 * the setter for this property.
 * <p>
 * It uses {@link #buildInvoker(Method)} to generate invokers for property accessors. By default, it is implemented
 * through the {@link Invocable#handle(Method)}, and can be overridden.
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
        Type result = JieMap.resolveChain(typeParameterMapping, type, stack);
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
    public @Nullable boolean doParse(DataSchemaParser.Context context) {
        Type type = context.getType();
        Class<?> rawType = JieReflect.getRawType(type);
        if (rawType == null) {
            throw new DataObjectException("Not a Class or ParameterizedType: " + type + ".");
        }
        Method[] methods = rawType.getMethods();
        Map<String, PropertyInfo> propertyInfoMap = new LinkedHashMap<>();

        // Builds property info for each method.
        for (Method method : methods) {
            if (method.isBridge()) {
                continue;
            }
            AccessorInfo accessorInfo = resolveAccessor(method);
            if (accessorInfo == null) {
                continue;
            }
            String propertyName = accessorInfo.getPropertyName();
            PropertyInfo propertyInfo = propertyInfoMap.get(propertyName);
            if (propertyInfo != null) {
                // Checks duplicate getter or setter
                if (accessorInfo.isGetter() && propertyInfo.getGetter() != null) {
                    throw new DataObjectException("Duplicate property getter: " + propertyName + ".");
                }
                if (!accessorInfo.isGetter() && propertyInfo.getSetter() != null) {
                    throw new DataObjectException("Duplicate property setter: " + propertyName + ".");
                }
            } else {
                propertyInfo = new PropertyInfo();
                propertyInfo.setName(propertyName);
                propertyInfoMap.put(propertyName, propertyInfo);
            }
            // Sets accessor method
            if (accessorInfo.isGetter()) {
                propertyInfo.setGetter(method);
            } else {
                propertyInfo.setSetter(method);
            }
        }

        // Builds property base for each property info.
        Map<TypeVariable<?>, Type> typeParameterMapping = JieReflect.mapTypeParameters(context.getType());
        Set<Type> stack = new HashSet<>();
        propertyInfoMap.forEach((propertyName, propertyInfo) -> {
            Method getter = propertyInfo.getGetter();
            Type getterType = getter == null ? null :
                findActualType(getter.getGenericReturnType(), typeParameterMapping, stack);
            Method setter = propertyInfo.getSetter();
            Type setterType = setter == null ? null :
                findActualType(setter.getGenericParameterTypes()[0], typeParameterMapping, stack);
            /*
            The property's type is the return type of getter or the parameter type of setter.
            If the getter's return type and the setter's parameter type are not equal, the getter's return type is used,
            and the setter method will no longer be considered as the setter for this property.
             */
            DataPropertyBase propertyBase;
            if (getterType != null) {
                if (Objects.equals(getterType, setterType)) {
                    propertyBase = new DataPropertyBaseImpl(propertyName, getterType, getter, setter, rawType);
                } else {
                    propertyBase = new DataPropertyBaseImpl(propertyName, getterType, getter, null, rawType);
                }
            } else {
                propertyBase = new DataPropertyBaseImpl(propertyName, setterType, null, setter, rawType);
            }
            context.getPropertyBaseMap().put(propertyName, propertyBase);
        });
        return true;
    }

    /**
     * Resolves and returns given method to data property accessor info, or {@code null} if given method is not a
     * property accessor.
     *
     * @param method given method
     * @return property accessor info
     */
    @Nullable
    protected abstract AccessorInfo resolveAccessor(Method method);

    /**
     * Generates invoker for specified method. Default using {@link Invocable#of(Method)}.
     *
     * @param method specified method
     * @return invoker for specified method
     */
    protected Invocable buildInvoker(Method method) {
        return Invocable.of(method);
    }

    /**
     * Data property accessor info, resolved from specified method.
     *
     * @author sunqian
     */
    public interface AccessorInfo {

        /**
         * Returns data property name.
         *
         * @return data property name
         */
        String getPropertyName();

        /**
         * Returns whether the method is a getter, {@code true} for getter, {@code false} for setter.
         *
         * @return {@code true} for getter, {@code false} for setter
         */
        boolean isGetter();
    }

    @Data
    private static final class PropertyInfo {
        private String name;
        private @Nullable Method getter;
        private @Nullable Method setter;
    }

    private final class DataPropertyBaseImpl implements DataPropertyBase {

        private final String name;
        private final Type type;

        private final @Nullable Method getter;
        private final @Nullable Method setter;
        private final @Nullable Invocable getterInvocable;
        private final @Nullable Invocable setterInvocable;
        private final @Nullable Field field;
        private final List<Annotation> getterAnnotations;
        private final List<Annotation> setterAnnotations;
        private final List<Annotation> fieldAnnotations;
        private final List<Annotation> allAnnotations;

        private DataPropertyBaseImpl(
            String name,
            Type type,
            @Nullable Method getter,
            @Nullable Method setter,
            Class<?> rawType
        ) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.getterInvocable = getter == null ? null : buildInvoker(getter);
            this.setter = setter;
            this.setterInvocable = setter == null ? null : buildInvoker(setter);
            this.field = findField(name, rawType);
            this.getterAnnotations = getter == null ? Collections.emptyList() : Jie.list(getter.getAnnotations());
            this.setterAnnotations = setter == null ? Collections.emptyList() : Jie.list(setter.getAnnotations());
            this.fieldAnnotations = field == null ? Collections.emptyList() : Jie.list(field.getAnnotations());
            int size = getGetterAnnotations().size() + getSetterAnnotations().size() + getFieldAnnotations().size();
            Annotation[] array = new Annotation[size];
            int i = 0;
            for (Annotation annotation : getterAnnotations) {
                array[i++] = annotation;
            }
            for (Annotation annotation : setterAnnotations) {
                array[i++] = annotation;
            }
            for (Annotation annotation : fieldAnnotations) {
                array[i++] = annotation;
            }
            this.allAnnotations = Jie.list(array);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public @Nullable Method getGetter() {
            return getter;
        }

        @Override
        public @Nullable Method getSetter() {
            return setter;
        }

        @Override
        public @Nullable Field getField() {
            return field;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return getterAnnotations;
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return setterAnnotations;
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public List<Annotation> getAnnotations() {
            return allAnnotations;
        }

        @Override
        public boolean isReadable() {
            return getterInvocable != null;
        }

        @Override
        public boolean isWriteable() {
            return setterInvocable != null;
        }

        @Override
        public @Nullable Object getValue(Object bean) {
            if (getterInvocable == null) {
                throw new DataObjectException("Data property is not readable: " + name + ".");
            }
            return getterInvocable.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) {
            if (setterInvocable == null) {
                throw new DataObjectException("Data property is not writeable: " + name + ".");
            }
            setterInvocable.invoke(bean, value);
        }
    }
}
