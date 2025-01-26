package xyz.sunqian.common.data.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.objects.PropertyIntro;
import xyz.sunqian.common.objects.BeanException;
import xyz.sunqian.common.objects.ObjectIntrospector;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.invoke.Invoker;
import xyz.sunqian.common.mapping.MappingException;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link ObjectIntrospector.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * <p>
 * Note this handler depends on {@code protobuf libs} in the runtime.
 *
 * @author fredsuvn
 */
public class ProtobufBeanResolveHandler implements ObjectIntrospector.Handler {

    @Override
    public @Nullable Flag introspect(ObjectIntrospector.Context context) {
        try {
            Class<?> rawType = JieReflect.getRawType(context.getObjectType());
            if (rawType == null) {
                return null;
            }
            // Check whether it is a protobuf object
            boolean isProtobuf = false;
            boolean isBuilder = false;
            if (Message.class.isAssignableFrom(rawType)) {
                isProtobuf = true;
            }
            if (Message.Builder.class.isAssignableFrom(rawType)) {
                isProtobuf = true;
                isBuilder = true;
            }
            if (!isProtobuf) {
                return null;
            }
            Method getDescriptorMethod = rawType.getMethod("getDescriptor");
            Descriptors.Descriptor descriptor = (Descriptors.Descriptor) getDescriptorMethod.invoke(null);
            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                PropertyIntro propertyIntro = buildProperty(context, field, rawType, isBuilder);
                context.propertyIntros().put(propertyIntro.getName(), propertyIntro);
            }
            return Flag.BREAK;
        } catch (MappingException e) {
            throw e;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private PropertyIntro buildProperty(
        ObjectIntrospector.Context builder,
        Descriptors.FieldDescriptor field,
        Class<?> rawClass,
        boolean isBuilder
    ) throws Exception {

        String rawName = field.getName();

        // map
        if (field.isMapField()) {
            String name = rawName + "Map";
            Method getterMethod = rawClass.getMethod("get" + JieString.capitalize(name));
            List<Type> argsTypes = JieReflect.getActualTypeArguments(getterMethod.getGenericReturnType(), Map.class);
            if (JieColl.isEmpty(argsTypes)) {
                throw new BeanException("Cannot get actual argument type for " + getterMethod.getGenericReturnType() + ".");
            }
            Invoker getter = Invoker.reflect(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + JieString.capitalize(rawName));
                Method putAllMethod = rawClass.getMethod("putAll" + JieString.capitalize(rawName), Map.class);
                Invoker setter = new Invoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        try {
                            clearMethod.invoke(inst);
                            return putAllMethod.invoke(inst, args);
                        } catch (InvocationTargetException e) {
                            throw new BeanException(e.getCause());
                        } catch (Exception e) {
                            throw new BeanException(e);
                        }
                    }
                };
                return new Impl(name, argsTypes.get(0), getterMethod, null, getter, setter);
            } else {
                return new Impl(name, argsTypes.get(0), getterMethod, null, getter, null);
            }
        }

        // repeated
        if (field.isRepeated()) {
            String name = rawName + "List";
            Method getterMethod = rawClass.getMethod("get" + JieString.capitalize(name));
            List<Type> argsTypes = JieReflect.getActualTypeArguments(getterMethod.getGenericReturnType(), List.class);
            if (JieColl.isEmpty(argsTypes)) {
                throw new BeanException("Cannot get actual argument type for " + getterMethod.getGenericReturnType() + ".");
            }
            Invoker getter = Invoker.reflect(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + JieString.capitalize(rawName));
                Method addAllMethod = rawClass.getMethod("addAll" + JieString.capitalize(rawName), Iterable.class);
                Invoker setter = new Invoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        try {
                            clearMethod.invoke(inst);
                            return addAllMethod.invoke(inst, args);
                        } catch (InvocationTargetException e) {
                            throw new BeanException(e.getCause());
                        } catch (Exception e) {
                            throw new BeanException(e);
                        }
                    }
                };
                return new Impl(name, argsTypes.get(0), getterMethod, null, getter, setter);
            } else {
                return new Impl(name, argsTypes.get(0), getterMethod, null, getter, null);
            }
        }

        // Simple object
        Method getterMethod = rawClass.getMethod("get" + JieString.capitalize(rawName));
        Type type = getterMethod.getGenericReturnType();
        Invoker getter = Invoker.reflect(getterMethod);
        if (isBuilder) {
            Method setterMethod = rawClass.getMethod("set" + JieString.capitalize(rawName), JieReflect.getRawType(type));
            Invoker setter = Invoker.reflect(setterMethod);
            return new Impl(rawName, type, getterMethod, setterMethod, getter, setter);
        } else {
            return new Impl(rawName, type, getterMethod, null, getter, null);
        }
    }

    private static final class Impl implements PropertyIntro {

        private final String name;
        private final Type type;
        private final @Nullable Method getterMethod;
        private final @Nullable Method setterMethod;
        private final Invoker getter;
        private final @Nullable Invoker setter;

        private Impl(
            String name,
            Type type,
            @Nullable Method getterMethod,
            @Nullable Method setterMethod,
            Invoker getter,
            @Nullable Invoker setter
        ) {
            this.name = name;
            this.type = type;
            this.getterMethod = getterMethod;
            this.setterMethod = setterMethod;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public @Nullable Object getValue(Object bean) {
            return getter.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) {
            if (setter == null) {
                throw new BeanException("Not writeable.");
            }
            setter.invoke(bean, value);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public @Nullable Method getGetter() {
            return getterMethod;
        }

        @Override
        public @Nullable Method getSetter() {
            return setterMethod;
        }

        @Override
        public @Nullable Field getField() {
            return null;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public List<Annotation> getAnnotations() {
            return Collections.emptyList();
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWriteable() {
            return setter != null;
        }
    }
}
