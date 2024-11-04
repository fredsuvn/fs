package xyz.sunqian.common.bean.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.bean.*;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.invoke.Invoker;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Base abstract bean resolving handler, provides a skeletal implementation of the {@link BeanResolver.Handler} to
 * minimize the effort required to implement the interface.
 * <p>
 * This abstract handler uses {@link Class#getMethods()} to find out all methods, then put each of them into
 * {@link #resolveGetter(Method)} and {@link #resolveSetter(Method)} to determine whether it is a getter/setter. If it
 * is, it will be resolved to a property descriptor, else it will be resolved to be a {@link MethodInfo}. Subtypes can
 * use/override {@link #buildGetter(String, Method)} and {@link #buildSetter(String, Method)} to create getter/setter.
 * <p>
 * It uses {@link #buildInvoker(Method)} to generate invokers for getters and setters. By default, it is implemented
 * through the {@link Invoker#handle(Method)}, and can be overridden.
 *
 * @author fredsuvn
 */
public abstract class AbstractBeanResolverHandler implements BeanResolver.Handler {

    @Override
    public @Nullable Flag resolve(BeanResolver.Context context) {
        Type type = context.getType();
        Class<?> rawType = JieReflect.getRawType(type);
        if (rawType == null) {
            throw new BeanResolvingException("Not a Class or ParameterizedType: " + type + ".");
        }
        Method[] methods = rawType.getMethods();
        Map<String, Method> getters = new LinkedHashMap<>();
        Map<String, Method> setters = new LinkedHashMap<>();
        List<Method> extraMethods = new LinkedList<>();
        for (Method method : methods) {
            if (method.isBridge()) {
                continue;
            }
            GetterInfo getterInfo = resolveGetter(method);
            if (getterInfo != null) {
                getters.put(getterInfo.getName(), method);
                continue;
            }
            SetterInfo setterInfo = resolveSetter(method);
            if (setterInfo != null) {
                setters.put(setterInfo.getName(), method);
                continue;
            }
            context.getMethods().add(new BaseMethodInfoImpl(method));
        }
        if (JieColl.isNotEmpty(getters) || JieColl.isNotEmpty(setters)) {
            mergeAccessors(context, getters, setters, rawType, extraMethods);
        }
        if (JieColl.isNotEmpty(extraMethods)) {
            for (Method extraMethod : extraMethods) {
                context.getMethods().add(new BaseMethodInfoImpl(extraMethod));
            }
        }
        return null;
    }

    private void mergeAccessors(
        BeanResolver.Context context,
        Map<String, Method> getters,
        Map<String, Method> setters,
        Class<?> rawType,
        List<Method> extraMethods
    ) {
        Map<TypeVariable<?>, Type> typeParameterMapping = JieReflect.getTypeParameterMapping(context.getType());
        Set<Type> stack = new HashSet<>();
        getters.forEach((name, getter) -> {
            Method setter = setters.get(name);
            // Type of getter and setter must be same.
            // But field's type will not be checked,
            // because field is only a name holder for adding Annotations conveniently.
            Type returnType = getter.getGenericReturnType();
            returnType = getActualType(returnType, typeParameterMapping, stack);
            if (setter != null) {
                Type setType = setter.getGenericParameterTypes()[0];
                setType = getActualType(setType, typeParameterMapping, stack);
                if (!Objects.equals(returnType, setType)) {
                    // Different returned type and set type.
                    setters.remove(name);
                    extraMethods.add(getter);
                    extraMethods.add(setter);
                    return;
                }
            }
            context.getProperties().put(name, new BasePropertyInfoImpl(name, getter, setter, returnType, rawType));
            setters.remove(name);
        });
        setters.forEach((name, setter) -> {
            Type setType = setter.getGenericParameterTypes()[0];
            setType = getActualType(setType, typeParameterMapping, stack);
            context.getProperties().put(name, new BasePropertyInfoImpl(name, null, setter, setType, rawType));
        });
    }

    /**
     * Resolves and returns given method as a getter, or null if given method is not a getter.
     *
     * @param method given method
     * @return given method as a getter
     */
    @Nullable
    protected abstract AbstractBeanResolverHandler.GetterInfo resolveGetter(Method method);

    /**
     * Resolves and returns given method as a setter, or null if given method is not a setter.
     *
     * @param method given method
     * @return given method as a setter
     */
    @Nullable
    protected abstract AbstractBeanResolverHandler.SetterInfo resolveSetter(Method method);

    /**
     * Builds a getter.
     *
     * @param name   name of getter
     * @param method source method
     * @return a getter
     */
    protected GetterInfo buildGetter(String name, Method method) {
        return () -> name;
    }

    /**
     * Builds a setter.
     *
     * @param name   name of setter
     * @param method source method
     * @return a setter
     */
    protected SetterInfo buildSetter(String name, Method method) {
        return () -> name;
    }

    /**
     * Generates invoker for specified method. Default using {@link Invoker#handle(Method)}.
     *
     * @param method specified method
     * @return invoker for specified method
     */
    protected Invoker buildInvoker(Method method) {
        return Invoker.handle(method);
    }

    private Type getActualType(Type type, Map<TypeVariable<?>, Type> typeParameterMapping, Set<Type> stack) {
        if (type instanceof Class) {
            return type;
        }
        stack.clear();
        Type result = JieColl.getRecursive(typeParameterMapping, type, stack);
        if (result != null) {
            return result;
        }
        return type;
    }

    /**
     * Getter info.
     */
    public interface GetterInfo {
        /**
         * Returns name of this getter.
         *
         * @return name of this getter
         */
        String getName();
    }

    /**
     * Setter info.
     */
    public interface SetterInfo {
        /**
         * Returns name of this setter.
         *
         * @return name of this setter
         */
        String getName();
    }

    private final class BasePropertyInfoImpl implements BasePropertyInfo {

        private final String name;
        private final @Nullable Method getter;
        private final @Nullable Method setter;
        private final Type type;
        private final @Nullable Invoker getterInvoker;
        private final @Nullable Invoker setterInvoker;
        private final @Nullable Field field;
        private final List<Annotation> getterAnnotations;
        private final List<Annotation> setterAnnotations;
        private final List<Annotation> fieldAnnotations;
        private final List<Annotation> allAnnotations;

        private BasePropertyInfoImpl(
            String name, @Nullable Method getter, @Nullable Method setter, Type type, Class<?> rawType) {
            this.name = name;
            this.getter = getter;
            this.getterInvoker = getter == null ? null : buildInvoker(getter);
            this.setter = setter;
            this.setterInvoker = setter == null ? null : buildInvoker(setter);
            this.type = type;
            this.field = findField(name, rawType);
            this.getterAnnotations = getter == null ? Collections.emptyList() : JieArray.asList(getter.getAnnotations());
            this.setterAnnotations = setter == null ? Collections.emptyList() : JieArray.asList(setter.getAnnotations());
            this.fieldAnnotations = field == null ? Collections.emptyList() : JieArray.asList(field.getAnnotations());
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
            this.allAnnotations = JieArray.asList(array);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public @Nullable Object getValue(Object bean) {
            if (getterInvoker == null) {
                throw new BeanException("PropertyInfo is not readable: " + name + ".");
            }
            return getterInvoker.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) {
            if (setterInvoker == null) {
                throw new BeanException("PropertyInfo is not writeable: " + name + ".");
            }
            setterInvoker.invoke(bean, value);
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
            return getterInvoker != null;
        }

        @Override
        public boolean isWriteable() {
            return setterInvoker != null;
        }
    }

    private final class BaseMethodInfoImpl implements BaseMethodInfo {

        private final Method method;
        private final List<Annotation> annotations;
        private final Invoker invoker;

        private BaseMethodInfoImpl(Method method) {
            this.method = method;
            this.annotations = JieArray.asList(method.getAnnotations());
            this.invoker = buildInvoker(method);
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public Object invoke(Object bean, Object... args) {
            return invoker.invoke(bean, args);
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public List<Annotation> getAnnotations() {
            return annotations;
        }
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
}
