package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a static utilities class provides utilities for data object such as {@link DataSchema} and
 * {@link DataProperty}.
 *
 * @author sunqian
 */
public class JieData {

    /**
     * Utility method for implementing {@link DataSchema#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(DataSchema)}.
     *
     * @param dataSchema the {@link DataSchema}
     * @param other      the specified other {@link DataSchema}
     * @return whether the {@link DataSchema} is equal to specified other {@link DataSchema}
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
     * Utility method for implementing {@link DataProperty#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(DataProperty)}.
     *
     * @param dataProperty the {@link DataProperty}
     * @param other        the specified other {@link DataProperty}
     * @return whether the {@link DataProperty} is equal to specified other {@link DataProperty}
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
     * Utility method for implementing {@link DataSchema#hashCode()}, and it works in conjunction with
     * {@link #equals(DataSchema, Object)}.
     *
     * @param dataSchema the {@link DataSchema}
     * @return hash code of given {@link DataSchema}
     */
    public static int hashCode(DataSchema dataSchema) {
        int result = 1;
        result = 31 * result + dataSchema.getType().hashCode();
        result = 31 * result + dataSchema.getParser().hashCode();
        return result;
    }

    /**
     * Utility method for implementing {@link DataProperty#hashCode()}, and it works in conjunction with
     * {@link #equals(DataProperty, Object)}.
     *
     * @param dataProperty the {@link DataProperty}
     * @return hash code of given {@link DataProperty}
     */
    public static int hashCode(DataProperty dataProperty) {
        int result = 1;
        result = 31 * result + dataProperty.getName().hashCode();
        result = 31 * result + dataProperty.getOwner().hashCode();
        return result;
    }

    /**
     * Utility method for implementing {@link DataSchema#toString()}.
     *
     * @param dataSchema the {@link DataSchema}
     * @return a string representation of given {@link DataSchema}
     */
    public static String toString(DataSchema dataSchema) {
        return "data[type=" + dataSchema.getType().getTypeName() + ", properties=["
            + dataSchema.getProperties().values().stream().map(
            it -> it.getName() + "(" + it.getType().getTypeName() + ")"
        ).collect(Collectors.joining(", "))
            + "]]";
    }

    /**
     * Utility method for implementing {@link DataProperty#toString()}.
     *
     * @param dataProperty the {@link DataProperty}
     * @return a string representation of given {@link DataProperty}
     */
    public static String toString(DataProperty dataProperty) {
        return "property[name=" + dataProperty.getName()
            + ", type=" + dataProperty.getType().getTypeName()
            + ", ownerType=" + dataProperty.getOwner().getType().getTypeName()
            + "]";
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
        if (JieColl.isNotEmpty(extraTypeVarMapping)) {
            Map<DataProperty, Type> mapping = new HashMap<>();
            Set<Type> stack = new HashSet<>();
            dataSchema.getProperties().forEach((n, p) -> {
                Type pt = p.getType();
                if (pt instanceof TypeVariable) {
                    stack.clear();
                    Type newType = JieColl.getRecursive(extraTypeVarMapping, pt, stack);
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
                return JieReflect.getRawType(type);
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
