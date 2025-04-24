package xyz.sunqian.common.data.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.mapping.MappingException;
import xyz.sunqian.common.objects.data.BeanException;
import xyz.sunqian.common.objects.data.DataPropertyBase;
import xyz.sunqian.common.objects.data.DataSchemaParser;
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
 * {@link DataSchemaParser.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * <p>
 * Note this handler depends on {@code protobuf libs} in the runtime.
 *
 * @author fredsuvn
 */
public class ProtobufBeanResolveHandler implements DataSchemaParser.Handler {

    @Override
    public @Nullable boolean doParse(DataSchemaParser.Context context) {
        try {
            Class<?> rawType = JieReflect.getRawType(context.getType());
            if (rawType == null) {
                return true;
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
                return true;
            }
            Method getDescriptorMethod = rawType.getMethod("getDescriptor");
            Descriptors.Descriptor descriptor = (Descriptors.Descriptor) getDescriptorMethod.invoke(null);
            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                DataPropertyBase dataPropertyBase = buildProperty(context, field, rawType, isBuilder);
                context.getPropertyBaseMap().put(dataPropertyBase.getName(), dataPropertyBase);
            }
            return false;
        } catch (MappingException e) {
            throw e;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private DataPropertyBase buildProperty(
        DataSchemaParser.Context builder,
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
            Invocable getter = Invocable.of(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + JieString.capitalize(rawName));
                Method putAllMethod = rawClass.getMethod("putAll" + JieString.capitalize(rawName), Map.class);
                Invocable setter = new Invocable() {
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
            Invocable getter = Invocable.of(getterMethod);
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + JieString.capitalize(rawName));
                Method addAllMethod = rawClass.getMethod("addAll" + JieString.capitalize(rawName), Iterable.class);
                Invocable setter = new Invocable() {
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
        Invocable getter = Invocable.of(getterMethod);
        if (isBuilder) {
            Method setterMethod = rawClass.getMethod("set" + JieString.capitalize(rawName), JieReflect.getRawType(type));
            Invocable setter = Invocable.of(setterMethod);
            return new Impl(rawName, type, getterMethod, setterMethod, getter, setter);
        } else {
            return new Impl(rawName, type, getterMethod, null, getter, null);
        }
    }

    private static final class Impl implements DataPropertyBase {

        private final String name;
        private final Type type;
        private final @Nullable Method getterMethod;
        private final @Nullable Method setterMethod;
        private final Invocable getter;
        private final @Nullable Invocable setter;

        private Impl(
            String name,
            Type type,
            @Nullable Method getterMethod,
            @Nullable Method setterMethod,
            Invocable getter,
            @Nullable Invocable setter
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
