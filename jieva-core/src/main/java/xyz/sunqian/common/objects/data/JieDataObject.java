package xyz.sunqian.common.objects.data;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.JieMap;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Static utilities class for data object.
 *
 * @author sunqian
 */
public class JieDataObject {

    /**
     * The implementation of {@link DataSchema#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(DataSchema)}.
     *
     * @param dataSchema the compared {@link DataSchema}
     * @param other      the other {@link DataSchema}
     * @return whether the compared {@link DataSchema} is equal to other {@link DataSchema}
     */
    public static boolean equals(DataSchema dataSchema, @Nullable Object other) {
        if (dataSchema == other) {
            return true;
        }
        if (!(other instanceof DataSchema)) {
            return false;
        }
        DataSchema otherSchema = (DataSchema) other;
        return Objects.equals(dataSchema.getType(), otherSchema.getType())
            && Objects.equals(dataSchema.getParser(), otherSchema.getParser());
    }

    /**
     * The implementation of {@link DataProperty#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(DataProperty)}.
     *
     * @param dataProperty the compared {@link DataProperty}
     * @param other        the other {@link DataProperty}
     * @return whether the compared {@link DataProperty} is equal to other {@link DataProperty}
     */
    public static boolean equals(DataProperty dataProperty, @Nullable Object other) {
        if (dataProperty == other) {
            return true;
        }
        if (!(other instanceof DataProperty)) {
            return false;
        }
        DataProperty otherProperty = (DataProperty) other;
        return Objects.equals(dataProperty.getName(), otherProperty.getName())
            && Objects.equals(dataProperty.getOwner(), otherProperty.getOwner());
    }

    /**
     * The implementation of {@link DataSchema#hashCode()}, and it works in conjunction with
     * {@link #equals(DataSchema, Object)}.
     *
     * @param dataSchema the {@link DataSchema} to be hashed
     * @return hash code of the {@link DataSchema}
     */
    public static int hashCode(DataSchema dataSchema) {
        int result = 1;
        result = 31 * result + dataSchema.getType().hashCode();
        result = 31 * result + dataSchema.getParser().hashCode();
        return result;
    }

    /**
     * The implementation of {@link DataProperty#hashCode()}, and it works in conjunction with
     * {@link #equals(DataProperty, Object)}.
     *
     * @param dataProperty the {@link DataProperty} to be hashed
     * @return hash code of the {@link DataProperty}
     */
    public static int hashCode(DataProperty dataProperty) {
        int result = 1;
        result = 31 * result + dataProperty.getName().hashCode();
        result = 31 * result + dataProperty.getOwner().hashCode();
        return result;
    }

    /**
     * The implementation of {@link DataSchema#toString()}.
     *
     * @param dataSchema the {@link DataSchema} to be string
     * @return a string representation of given {@link DataSchema}
     */
    public static String toString(DataSchema dataSchema) {
        return "data[" +
            "type=" + dataSchema.getType().getTypeName() + ", " +
            "properties=[" +
            dataSchema.getProperties().values().stream().map(it ->
                it.getName() + ": " + it.getType().getTypeName()
            ).collect(Collectors.joining("; ")) +
            "]]";
    }

    /**
     * The implementation of {@link DataProperty#toString()}.
     *
     * @param dataProperty the {@link DataProperty} to be string
     * @return a string representation of given {@link DataProperty}
     */
    public static String toString(DataProperty dataProperty) {
        return "property[" +
            "name=" + dataProperty.getName() + ", " +
            "type=" + dataProperty.getType().getTypeName() + ", " +
            "ownerType=" + dataProperty.getOwner().getType().getTypeName() +
            "]";
    }

    /**
     * Tries to map unresolved type variables of properties of given bean info with extra type variable mapping. If no
     * mapping found, given bean info itself will be returned. Otherwise, a new bean info with extra mapping will be
     * returned.
     *
     * @param dataSchema          given bean info
     * @param extraTypeVarMapping extra type variable mapping
     * @return iven bean info itself or a new bean info with extra mapping
     * @throws DataObjectException if any problem occurs when resolving
     */
    public static DataSchema withExtraTypeVariableMapping(
        DataSchema dataSchema, @Nullable Map<TypeVariable<?>, Type> extraTypeVarMapping
    ) throws DataObjectException {
        if (JieMap.isNotEmpty(extraTypeVarMapping)) {
            Map<DataProperty, Type> mapping = new HashMap<>();
            Set<Type> stack = new HashSet<>();
            dataSchema.getProperties().forEach((n, p) -> {
                Type pt = p.getType();
                if (pt instanceof TypeVariable) {
                    stack.clear();
                    Type newType = JieMap.resolveChain(extraTypeVarMapping, pt, stack);
                    if (newType != null) {
                        mapping.put(p, newType);
                    }
                }
            });
            if (!mapping.isEmpty()) {
                return new DataSchemaWrapper(dataSchema, mapping);
            }
        }
        return dataSchema;
    }

    private static final class DataSchemaWrapper implements DataSchema {

        private final DataSchema origin;
        private final Map<String, DataProperty> props;

        private DataSchemaWrapper(DataSchema origin, Map<DataProperty, Type> mapping) {
            this.origin = origin;
            Map<String, DataProperty> newProps = new LinkedHashMap<>();
            origin.getProperties().forEach((n, p) -> {
                Type newType = mapping.get(p);
                if (newType != null) {
                    newProps.put(n, new DataPropertyWrapper(p, newType));
                    return;
                }
                newProps.put(n, new DataPropertyWrapper(p, p.getType()));
            });
            this.props = Collections.unmodifiableMap(newProps);
        }

        @Override
        public DataSchemaParser getParser() {
            return origin.getParser();
        }

        @Override
        public Type getType() {
            return origin.getType();
        }

        @Override
        public Class<?> getRawType() {
            return origin.getRawType();
        }

        @Override
        public @Immutable Map<String, DataProperty> getProperties() {
            return props;
        }

        @Override
        public @Nullable DataProperty getProperty(String name) {
            return props.get(name);
        }

        @Override
        public boolean equals(Object o) {
            return JieDataObject.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieDataObject.hashCode(this);
        }

        @Override
        public String toString() {
            return JieDataObject.toString(this);
        }

        private final class DataPropertyWrapper implements DataProperty {

            private final DataProperty prop;
            private final Type type;

            private DataPropertyWrapper(DataProperty prop, Type type) {
                this.prop = prop;
                this.type = type;
            }

            @Override
            public DataSchema getOwner() {
                return DataSchemaWrapper.this;
            }

            @Override
            public String getName() {
                return prop.getName();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return prop.getAnnotations();
            }

            @Override
            public <A extends Annotation> @Nullable A getAnnotation(Class<A> type) {
                return prop.getAnnotation(type);
            }

            @Override
            public @Nullable Object getValue(Object inst) {
                return prop.getValue(inst);
            }

            @Override
            public void setValue(Object inst, @Nullable Object value) {
                prop.setValue(inst, value);
            }

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public @Nullable Class<?> getRawType() {
                return JieReflect.getRawClass(type);
            }

            @Override
            public @Nullable Method getGetter() {
                return prop.getGetter();
            }

            @Override
            public @Nullable Method getSetter() {
                return prop.getSetter();
            }

            @Override
            public @Nullable Field getField() {
                return prop.getField();
            }

            @Override
            public List<Annotation> getFieldAnnotations() {
                return prop.getFieldAnnotations();
            }

            @Override
            public List<Annotation> getGetterAnnotations() {
                return prop.getGetterAnnotations();
            }

            @Override
            public List<Annotation> getSetterAnnotations() {
                return prop.getSetterAnnotations();
            }

            @Override
            public boolean isReadable() {
                return prop.isReadable();
            }

            @Override
            public boolean isWriteable() {
                return prop.isWriteable();
            }

            @Override
            public boolean equals(Object o) {
                return JieDataObject.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieDataObject.hashCode(this);
            }

            @Override
            public String toString() {
                return JieDataObject.toString(this);
            }
        }
    }
}
