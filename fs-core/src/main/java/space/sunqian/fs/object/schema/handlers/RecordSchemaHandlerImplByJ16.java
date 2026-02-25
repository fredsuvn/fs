package space.sunqian.fs.object.schema.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.schema.ObjectPropertyBase;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;

enum RecordSchemaHandlerImplByJ16 implements ObjectSchemaParser.Handler {

    INST;

    @Override
    public boolean parse(ObjectSchemaParser.@Nonnull Context context) throws Exception {
        var type = context.parsedType();
        var rawClass = TypeKit.getRawClass(type);
        if (rawClass == null) {
            return true;
        }
        var components = rawClass.getRecordComponents();
        if (components == null) {
            return true;
        }
        for (RecordComponent component : components) {
            var componentName = component.getName();
            var componentType = component.getGenericType();
            var getterMethod = component.getAccessor();
            Invocable getter = (inst, args) -> getterMethod.invoke(inst);
            context.propertyBaseMap().put(
                componentName,
                new RecordBase(componentName, componentType, getterMethod, getter)
            );
        }
        return false;
    }

    private static final class RecordBase implements ObjectPropertyBase {

        private final @Nonnull String name;
        private final @Nonnull Type type;
        private final @Nonnull Method getterMethod;
        private final @Nonnull Invocable getter;

        private RecordBase(
            @Nonnull String name,
            @Nonnull Type type,
            @Nonnull Method getterMethod,
            @Nonnull Invocable getter
        ) {
            this.name = name;
            this.type = type;
            this.getterMethod = getterMethod;
            this.getter = getter;
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
        public @Nonnull Method getterMethod() {
            return getterMethod;
        }

        @Override
        public @Nullable Method setterMethod() {
            return null;
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
            return null;
        }
    }
}
