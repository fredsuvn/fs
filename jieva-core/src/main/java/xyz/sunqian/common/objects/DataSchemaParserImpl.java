package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.objects.handlers.JavaBeanDataSchemaHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class DataSchemaParserImpl implements DataSchemaParser, DataSchemaParser.Handler {

    static DataSchemaParserImpl SINGLETON =
        new DataSchemaParserImpl(Collections.singletonList(new JavaBeanDataSchemaHandler()));

    private final List<DataSchemaParser.Handler> handlers;

    DataSchemaParserImpl(@Immutable List<? extends DataSchemaParser.Handler> handlers) {
        this.handlers = Jie.as(handlers);
    }

    @Override
    public DataSchema parse(Type type) throws DataObjectException {
        try {
            ContextImpl builder = new ContextImpl(type);
            for (Handler handler : handlers) {
                if (!handler.doParse(builder)) {
                    break;
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw new DataObjectException(type, e);
        }
    }

    @Override
    @Immutable
    public List<DataSchemaParser.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public DataSchemaParser.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable boolean doParse(DataSchemaParser.Context builder) {
        for (DataSchemaParser.Handler handler : getHandlers()) {
            if (!handler.doParse(builder)) {
                return false;
            }
        }
        return true;
    }

    private final class ContextImpl implements DataSchemaParser.Context {

        private final Type type;
        private final Map<String, DataPropertyBase> properties = new LinkedHashMap<>();

        ContextImpl(Type type) {
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, DataPropertyBase> getPropertyBaseMap() {
            return properties;
        }

        private DataSchema build() {
            return new DataSchemaImpl(type, properties);
        }
    }

    private final class DataSchemaImpl implements DataSchema {

        private final Type type;
        private final Map<String, DataProperty> properties;

        private DataSchemaImpl(
            Type type,
            Map<String, DataPropertyBase> properties
        ) {
            this.type = type;
            this.properties = JieColl.toMap(properties, name -> name, DataPropertyImpl::new);
        }

        @Override
        public DataSchemaParser getParser() {
            return DataSchemaParserImpl.this;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, DataProperty> getProperties() {
            return properties;
        }

        @Override
        public boolean equals(Object o) {
            return JieData.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieData.hashCode(this);
        }

        @Override
        public String toString() {
            return JieData.toString(this);
        }

        private final class DataPropertyImpl implements DataProperty {

            private final DataPropertyBase base;

            private DataPropertyImpl(DataPropertyBase propBase) {
                this.base = propBase;
            }

            @Override
            public DataSchema getOwner() {
                return DataSchemaImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            @Nullable
            public Object getValue(Object inst) {
                return base.getValue(inst);
            }

            @Override
            public void setValue(Object inst, @Nullable Object value) {
                base.setValue(inst, value);
            }

            @Override
            public Type getType() {
                return base.getType();
            }

            @Override
            public Class<?> getRawType() {
                return base.getRawType();
            }

            @Override
            public @Nullable Method getGetter() {
                return base.getGetter();
            }

            @Override
            public @Nullable Method getSetter() {
                return base.getSetter();
            }

            @Override
            public @Nullable Field getField() {
                return base.getField();
            }

            @Override
            public List<Annotation> getGetterAnnotations() {
                return base.getGetterAnnotations();
            }

            @Override
            public List<Annotation> getSetterAnnotations() {
                return base.getSetterAnnotations();
            }

            @Override
            public List<Annotation> getFieldAnnotations() {
                return base.getFieldAnnotations();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return base.getAnnotations();
            }

            @Override
            public <A extends Annotation> @Nullable A getAnnotation(Class<A> type) {
                return base.getAnnotation(type);
            }

            @Override
            public boolean isReadable() {
                return base.isReadable();
            }

            @Override
            public boolean isWriteable() {
                return base.isWriteable();
            }

            @Override
            public boolean equals(Object o) {
                return JieData.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieData.hashCode(this);
            }

            @Override
            public String toString() {
                return JieData.toString(this);
            }
        }
    }
}
