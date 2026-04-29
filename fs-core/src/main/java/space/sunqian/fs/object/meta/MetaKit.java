package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for object schema, including map schema and non-map object schema.
 *
 * @author sunqian
 */
public class MetaKit {

    /**
     * The implementation of {@link ObjectMeta#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(ObjectMeta)}.
     *
     * @param objectSchema the compared {@link ObjectMeta}
     * @param other        the other {@link ObjectMeta}
     * @return whether the compared {@link ObjectMeta} is equal to other {@link ObjectMeta}
     */
    public static boolean equals(@Nonnull ObjectMeta objectSchema, @Nullable Object other) {
        if (objectSchema == other) {
            return true;
        }
        if (!(other instanceof ObjectMeta)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        ObjectMeta otherSchema = (ObjectMeta) other;
        return Objects.equals(objectSchema.type(), otherSchema.type())
            && Objects.equals(objectSchema.manager(), otherSchema.manager());
    }

    /**
     * The implementation of {@link PropertyMeta#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(PropertyMeta)}.
     *
     * @param propertyMeta the compared {@link PropertyMeta}
     * @param other          the other {@link PropertyMeta}
     * @return whether the compared {@link PropertyMeta} is equal to other {@link PropertyMeta}
     */
    public static boolean equals(@Nonnull PropertyMeta propertyMeta, @Nullable Object other) {
        if (propertyMeta == other) {
            return true;
        }
        if (!(other instanceof PropertyMeta)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        PropertyMeta otherProperty = (PropertyMeta) other;
        return Objects.equals(propertyMeta.name(), otherProperty.name())
            && Objects.equals(propertyMeta.owner(), otherProperty.owner());
    }

    /**
     * The implementation of {@link MapMeta#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(MapMeta)}.
     *
     * @param mapSchema the compared {@link MapMeta}
     * @param other     the other {@link MapMeta}
     * @return whether the compared {@link MapMeta} is equal to other {@link MapMeta}
     */
    public static boolean equals(@Nonnull MapMeta mapSchema, @Nullable Object other) {
        if (mapSchema == other) {
            return true;
        }
        if (!(other instanceof MapMeta)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        MapMeta otherSchema = (MapMeta) other;
        return Objects.equals(mapSchema.type(), otherSchema.type())
            && Objects.equals(mapSchema.manager(), otherSchema.manager());
    }

    /**
     * The implementation of {@link ObjectMeta#hashCode()}, and it works in conjunction with
     * {@link #equals(ObjectMeta, Object)}.
     *
     * @param objectSchema the {@link ObjectMeta} to be hashed
     * @return the hash code of the {@link ObjectMeta}
     */
    public static int hashCode(@Nonnull ObjectMeta objectSchema) {
        int result = 1;
        result = 31 * result + objectSchema.type().hashCode();
        result = 31 * result + objectSchema.manager().hashCode();
        return result;
    }

    /**
     * The implementation of {@link PropertyMeta#hashCode()}, and it works in conjunction with
     * {@link #equals(PropertyMeta, Object)}.
     *
     * @param propertyMeta the {@link PropertyMeta} to be hashed
     * @return the hash code of the {@link PropertyMeta}
     */
    public static int hashCode(@Nonnull PropertyMeta propertyMeta) {
        int result = 1;
        result = 31 * result + propertyMeta.name().hashCode();
        result = 31 * result + propertyMeta.owner().hashCode();
        return result;
    }

    /**
     * The implementation of {@link MapMeta#hashCode()}, and it works in conjunction with
     * {@link #equals(MapMeta, Object)}.
     *
     * @param mapSchema the {@link MapMeta} to be hashed
     * @return the hash code of the {@link MapMeta}
     */
    public static int hashCode(@Nonnull MapMeta mapSchema) {
        int result = 1;
        result = 31 * result + mapSchema.type().hashCode();
        result = 31 * result + mapSchema.manager().hashCode();
        return result;
    }

    /**
     * The implementation of {@link ObjectMeta#toString()}.
     *
     * @param objectSchema the {@link ObjectMeta} to be string
     * @return a string representation of given {@link ObjectMeta}
     */
    public static @Nonnull String toString(@Nonnull ObjectMeta objectSchema) {
        return objectSchema.type().getTypeName() + "{" +
            objectSchema.properties().values().stream()
                .map(PropertyMeta::toString)
                .collect(Collectors.joining(", "))
            + "}";
    }

    /**
     * The implementation of {@link PropertyMeta#toString()}.
     *
     * @param property the {@link PropertyMeta} to be string
     * @return a string representation of given {@link PropertyMeta}
     */
    public static @Nonnull String toString(@Nonnull PropertyMeta property) {
        return property.name() + ": " + property.type().getTypeName();
    }

    /**
     * The implementation of {@link MapMeta#toString()}.
     *
     * @param mapSchema the {@link MapMeta} to be string
     * @return a string representation of given {@link MapMeta}
     */
    public static @Nonnull String toString(@Nonnull MapMeta mapSchema) {
        return mapSchema.type().getTypeName();
    }

    private MetaKit() {
    }
}
