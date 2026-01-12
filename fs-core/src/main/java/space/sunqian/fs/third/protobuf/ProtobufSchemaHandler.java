package space.sunqian.fs.third.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolStringList;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.exception.UnsupportedEnvException;
import space.sunqian.fs.base.string.StringKit;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.data.ObjectProperty;
import space.sunqian.fs.object.data.ObjectPropertyBase;
import space.sunqian.fs.object.data.ObjectSchemaParser;
import space.sunqian.fs.reflect.TypeKit;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ObjectSchemaParser.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>, can be quickly used through similar
 * codes:
 * <pre>{@code
 * ObjectSchemaParser parser = ObjectSchemaParser
 *     .defaultParser()
 *     .withFirstHandler(new ProtobufSchemaHandler());
 * }</pre>
 * To use this class, the protobuf package {@code com.google.protobuf} must in the runtime environment.
 * <p>
 * Note:
 * <ul>
 *     <li>
 *         When {@link ProtocolStringList} is used as a property type, it will be mapped to {@code List<String>}, but
 *         the type of the instance returned by {@link ObjectProperty#getValue(Object)} is still
 *         {@link ProtocolStringList};
 *     </li>
 *     <li>
 *         For a Builder, the properties of {@code repeated} and {@code map} types do not have setter methods, but the
 *         setter (which is am {@link Invocable}) is not null.
 *     </li>
 * </ul>
 *
 * @author sunqian
 */
public class ProtobufSchemaHandler implements ObjectSchemaParser.Handler {

    static final class StringListTypeRef extends TypeRef<List<String>> {
        static final @Nonnull StringListTypeRef SINGLETON = new StringListTypeRef();
    }

    /**
     * Constructs a new handler instance. This constructor will check whether the protobuf package is available in the
     * current environment.
     *
     * @throws UnsupportedEnvException if the protobuf package is not available in the current environment.
     */
    public ProtobufSchemaHandler() throws UnsupportedEnvException {
        Fs.uncheck(() -> Class.forName("com.google.protobuf.Message"), UnsupportedEnvException::new);
    }

    @Override
    public boolean parse(@Nonnull ObjectSchemaParser.Context context) throws Exception {
        Class<?> rawType = TypeKit.getRawClass(context.dataType());
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
            ObjectPropertyBase objectPropertyBase = buildProperty(field, rawType, isBuilder);
            context.propertyBaseMap().put(objectPropertyBase.name(), objectPropertyBase);
        }
        return false;
    }

    private @Nonnull ObjectPropertyBase buildProperty(
        @Nonnull Descriptors.FieldDescriptor field,
        @Nonnull Class<?> rawClass,
        boolean isBuilder
    ) throws Exception {

        String rawName = field.getName();

        // map
        if (field.isMapField()) {
            String name = rawName + "Map";
            Method getterMethod = rawClass.getMethod("get" + StringKit.capitalize(name));
            Invocable getter = Invocable.of(getterMethod);
            Invocable setter = null;
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + StringKit.capitalize(rawName));
                Method putAllMethod = rawClass.getMethod("putAll" + StringKit.capitalize(rawName), Map.class);
                setter = new MapSetter(clearMethod, putAllMethod);
            }
            return new PropertyBaseImpl(
                rawName,
                getterMethod.getGenericReturnType(),
                getterMethod,
                null,
                getter,
                setter
            );
        }

        // repeated
        if (field.isRepeated()) {
            String name = rawName + "List";
            Method getterMethod = rawClass.getMethod("get" + StringKit.capitalize(name));
            Type type = getterMethod.getGenericReturnType();
            if (Objects.equals(type, ProtocolStringList.class)) {
                type = StringListTypeRef.SINGLETON.type();
            }
            Invocable getter = Invocable.of(getterMethod);
            Invocable setter = null;
            if (isBuilder) {
                Method clearMethod = rawClass.getMethod("clear" + StringKit.capitalize(rawName));
                Method addAllMethod = rawClass.getMethod("addAll" + StringKit.capitalize(rawName), Iterable.class);
                setter = new ListSetter(clearMethod, addAllMethod);
            }
            return new PropertyBaseImpl(
                rawName,
                type,
                getterMethod,
                null,
                getter,
                setter
            );
        }

        // Simple object
        Method getterMethod = rawClass.getMethod("get" + StringKit.capitalize(rawName));
        Type type = getterMethod.getGenericReturnType();
        Invocable getter = Invocable.of(getterMethod);
        Method setterMethod = null;
        Invocable setter = null;
        if (isBuilder) {
            setterMethod = rawClass.getMethod("set" + StringKit.capitalize(rawName), TypeKit.getRawClass(type));
            setter = Invocable.of(setterMethod);
        }
        return new PropertyBaseImpl(
            rawName,
            type,
            getterMethod,
            setterMethod,
            getter,
            setter
        );
    }

    private static final class MapSetter implements Invocable {

        private final @Nonnull MethodHandle clearHandle;
        private final @Nonnull MethodHandle putAllHandle;

        private MapSetter(@Nonnull Method clearMethod, @Nonnull Method putAllMethod) throws Exception {
            // setter = (inst, args) -> {
            //     clearMethod.invoke(inst);
            //     return putAllMethod.invoke(inst, args);
            // };
            this.clearHandle = MethodHandles.lookup().unreflect(clearMethod);
            this.putAllHandle = MethodHandles.lookup().unreflect(putAllMethod);
        }

        @Override
        public @Nullable Object invokeDirectly(
            @Nullable Object inst, @Nullable Object @Nonnull ... args
        ) throws Throwable {
            clearHandle.invoke(inst);
            putAllHandle.invoke(inst, args[0]);
            return null;
        }
    }

    private static final class ListSetter implements Invocable {

        private final @Nonnull MethodHandle clearHandle;
        private final @Nonnull MethodHandle addAllMethod;

        private ListSetter(@Nonnull Method clearMethod, @Nonnull Method addAllMethod) throws Exception {
            // setter = (inst, args) -> {
            //     clearMethod.invoke(inst);
            //     return addAllMethod.invoke(inst, args);
            // };
            this.clearHandle = MethodHandles.lookup().unreflect(clearMethod);
            this.addAllMethod = MethodHandles.lookup().unreflect(addAllMethod);
        }

        @Override
        public @Nullable Object invokeDirectly(
            @Nullable Object inst, @Nullable Object @Nonnull ... args
        ) throws Throwable {
            clearHandle.invoke(inst);
            addAllMethod.invoke(inst, args[0]);
            return null;
        }
    }

    private static final class PropertyBaseImpl implements ObjectPropertyBase {

        private final @Nonnull String name;
        private final @Nonnull Type type;
        private final @Nullable Method getterMethod;
        private final @Nullable Method setterMethod;
        private final @Nonnull Invocable getter;
        private final @Nullable Invocable setter;

        private PropertyBaseImpl(
            @Nonnull String name,
            @Nonnull Type type,
            @Nullable Method getterMethod,
            @Nullable Method setterMethod,
            @Nonnull Invocable getter,
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
            return null;
        }

        @Override
        public @Nonnull Invocable getter() {
            return getter;
        }

        @Override
        public @Nullable Invocable setter() {
            return setter;
        }
    }
}
