package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for data object.
 *
 * @author sunqian
 */
public class DataObjectKit {

    /**
     * The implementation of {@link DataSchema#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(DataSchema)}.
     *
     * @param dataSchema the compared {@link DataSchema}
     * @param other      the other {@link DataSchema}
     * @return whether the compared {@link DataSchema} is equal to other {@link DataSchema}
     */
    public static boolean equals(@Nonnull DataSchema dataSchema, @Nullable Object other) {
        if (dataSchema == other) {
            return true;
        }
        if (!(other instanceof DataSchema)) {
            return false;
        }
        DataSchema otherSchema = (DataSchema) other;
        return Objects.equals(dataSchema.type(), otherSchema.type())
            && Objects.equals(dataSchema.parser(), otherSchema.parser());
    }

    /**
     * The implementation of {@link DataProperty#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(DataProperty)}.
     *
     * @param dataProperty the compared {@link DataProperty}
     * @param other        the other {@link DataProperty}
     * @return whether the compared {@link DataProperty} is equal to other {@link DataProperty}
     */
    public static boolean equals(@Nonnull DataProperty dataProperty, @Nullable Object other) {
        if (dataProperty == other) {
            return true;
        }
        if (!(other instanceof DataProperty)) {
            return false;
        }
        DataProperty otherProperty = (DataProperty) other;
        return Objects.equals(dataProperty.name(), otherProperty.name())
            && Objects.equals(dataProperty.owner(), otherProperty.owner());
    }

    /**
     * The implementation of {@link DataSchema#hashCode()}, and it works in conjunction with
     * {@link #equals(DataSchema, Object)}.
     *
     * @param dataSchema the {@link DataSchema} to be hashed
     * @return the hash code of the {@link DataSchema}
     */
    public static int hashCode(@Nonnull DataSchema dataSchema) {
        int result = 1;
        result = 31 * result + dataSchema.type().hashCode();
        result = 31 * result + dataSchema.parser().hashCode();
        return result;
    }

    /**
     * The implementation of {@link DataProperty#hashCode()}, and it works in conjunction with
     * {@link #equals(DataProperty, Object)}.
     *
     * @param dataProperty the {@link DataProperty} to be hashed
     * @return the hash code of the {@link DataProperty}
     */
    public static int hashCode(@Nonnull DataProperty dataProperty) {
        int result = 1;
        result = 31 * result + dataProperty.name().hashCode();
        result = 31 * result + dataProperty.owner().hashCode();
        return result;
    }

    /**
     * The implementation of {@link DataSchema#toString()}.
     *
     * @param dataSchema the {@link DataSchema} to be string
     * @return a string representation of given {@link DataSchema}
     */
    public static @Nonnull String toString(@Nonnull DataSchema dataSchema) {
        return "data[" +
            "type=" + dataSchema.type().getTypeName() + ", " +
            "properties=[" +
            dataSchema.properties().values().stream().map(it ->
                it.name() + ": " + it.type().getTypeName()
            ).collect(Collectors.joining("; ")) +
            "]]";
    }

    /**
     * The implementation of {@link DataProperty#toString()}.
     *
     * @param dataProperty the {@link DataProperty} to be string
     * @return a string representation of given {@link DataProperty}
     */
    public static @Nonnull String toString(@Nonnull DataProperty dataProperty) {
        return "property[" +
            "name=" + dataProperty.name() + ", " +
            "type=" + dataProperty.type().getTypeName() + ", " +
            "ownerType=" + dataProperty.owner().type().getTypeName() +
            "]";
    }

    // /**
    //  * Tries to map unresolved type variables of properties of given bean info with extra type variable mapping. If no
    //  * mapping found, given bean info itself will be returned. Otherwise, a new bean info with extra mapping will be
    //  * returned.
    //  *
    //  * @param dataSchema          given bean info
    //  * @param extraTypeVarMapping extra type variable mapping
    //  * @return iven bean info itself or a new bean info with extra mapping
    //  * @throws DataObjectException if any problem occurs when resolving
    //  */
    // public static DataSchema withExtraTypeVariableMapping(
    //     DataSchema dataSchema, @Nullable Map<TypeVariable<?>, Type> extraTypeVarMapping
    // ) throws DataObjectException {
    //     if (MapKit.isNotEmpty(extraTypeVarMapping)) {
    //         Map<DataProperty, Type> mapping = new HashMap<>();
    //         Set<Type> stack = new HashSet<>();
    //         dataSchema.properties().forEach((n, p) -> {
    //             Type pt = p.type();
    //             if (pt instanceof TypeVariable) {
    //                 stack.clear();
    //                 Type newType = MapKit.resolveChain(extraTypeVarMapping, pt, stack);
    //                 if (newType != null) {
    //                     mapping.put(p, newType);
    //                 }
    //             }
    //         });
    //         if (!mapping.isEmpty()) {
    //             return new DataSchemaWrapper(dataSchema, mapping);
    //         }
    //     }
    //     return dataSchema;
    // }

    // private static final class DataSchemaWrapper implements DataSchema {
    //
    //     private final DataSchema origin;
    //     private final Map<String, DataProperty> props;
    //
    //     private DataSchemaWrapper(DataSchema origin, Map<DataProperty, Type> mapping) {
    //         this.origin = origin;
    //         Map<String, DataProperty> newProps = new LinkedHashMap<>();
    //         origin.properties().forEach((n, p) -> {
    //             Type newType = mapping.get(p);
    //             if (newType != null) {
    //                 newProps.put(n, new DataPropertyWrapper(p, newType));
    //                 return;
    //             }
    //             newProps.put(n, new DataPropertyWrapper(p, p.type()));
    //         });
    //         this.props = Collections.unmodifiableMap(newProps);
    //     }
    //
    //     @Override
    //     public DataSchemaParser parser() {
    //         return origin.parser();
    //     }
    //
    //     @Override
    //     public Type type() {
    //         return origin.type();
    //     }
    //
    //     @Override
    //     public Class<?> rawType() {
    //         return origin.rawType();
    //     }
    //
    //     @Override
    //     public @Immutable Map<String, DataProperty> properties() {
    //         return props;
    //     }
    //
    //     @Override
    //     public @Nullable DataProperty getProperty(String name) {
    //         return props.get(name);
    //     }
    //
    //     @Override
    //     public boolean equals(Object o) {
    //         return JieDataObject.equals(this, o);
    //     }
    //
    //     @Override
    //     public int hashCode() {
    //         return JieDataObject.hashCode(this);
    //     }
    //
    //     @Override
    //     public String toString() {
    //         return JieDataObject.toString(this);
    //     }
    //
    //     private final class DataPropertyWrapper implements DataProperty {
    //
    //         private final DataProperty prop;
    //         private final Type type;
    //
    //         private DataPropertyWrapper(DataProperty prop, Type type) {
    //             this.prop = prop;
    //             this.type = type;
    //         }
    //
    //         @Override
    //         public DataSchema owner() {
    //             return DataSchemaWrapper.this;
    //         }
    //
    //         @Override
    //         public String name() {
    //             return prop.name();
    //         }
    //
    //         @Override
    //         public List<Annotation> annotations() {
    //             return prop.annotations();
    //         }
    //
    //         @Override
    //         public <A extends Annotation> @Nullable A getAnnotation(Class<A> type) {
    //             return prop.getAnnotation(type);
    //         }
    //
    //         @Override
    //         public @Nullable Object getValue(Object inst) {
    //             return prop.getValue(inst);
    //         }
    //
    //         @Override
    //         public void setValue(Object inst, @Nullable Object value) {
    //             prop.setValue(inst, value);
    //         }
    //
    //         @Override
    //         public Type type() {
    //             return type;
    //         }
    //
    //         @Override
    //         public @Nullable Class<?> rawType() {
    //             return TypeKit.getRawClass(type);
    //         }
    //
    //         @Override
    //         public @Nullable Method getterMethod() {
    //             return prop.getterMethod();
    //         }
    //
    //         @Override
    //         public @Nullable Method setterMethod() {
    //             return prop.setterMethod();
    //         }
    //
    //         @Override
    //         public @Nullable Field field() {
    //             return prop.field();
    //         }
    //
    //         @Override
    //         public List<Annotation> fieldAnnotations() {
    //             return prop.fieldAnnotations();
    //         }
    //
    //         @Override
    //         public List<Annotation> getterAnnotations() {
    //             return prop.getterAnnotations();
    //         }
    //
    //         @Override
    //         public List<Annotation> setterAnnotations() {
    //             return prop.setterAnnotations();
    //         }
    //
    //         @Override
    //         public boolean isReadable() {
    //             return prop.isReadable();
    //         }
    //
    //         @Override
    //         public boolean isWriteable() {
    //             return prop.isWriteable();
    //         }
    //
    //         @Override
    //         public boolean equals(Object o) {
    //             return JieDataObject.equals(this, o);
    //         }
    //
    //         @Override
    //         public int hashCode() {
    //             return JieDataObject.hashCode(this);
    //         }
    //
    //         @Override
    //         public String toString() {
    //             return JieDataObject.toString(this);
    //         }
    //     }
    // }
}
