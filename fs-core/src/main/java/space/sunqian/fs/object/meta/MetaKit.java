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
            && Objects.equals(objectSchema.parser(), otherSchema.parser());
    }

    /**
     * The implementation of {@link PropertyMetaMeta#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(PropertyMetaMeta)}.
     *
     * @param propertyMeta the compared {@link PropertyMetaMeta}
     * @param other          the other {@link PropertyMetaMeta}
     * @return whether the compared {@link PropertyMetaMeta} is equal to other {@link PropertyMetaMeta}
     */
    public static boolean equals(@Nonnull PropertyMetaMeta propertyMeta, @Nullable Object other) {
        if (propertyMeta == other) {
            return true;
        }
        if (!(other instanceof PropertyMetaMeta)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        PropertyMetaMeta otherProperty = (PropertyMetaMeta) other;
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
        result = 31 * result + objectSchema.parser().hashCode();
        return result;
    }

    /**
     * The implementation of {@link PropertyMetaMeta#hashCode()}, and it works in conjunction with
     * {@link #equals(PropertyMetaMeta, Object)}.
     *
     * @param propertyMeta the {@link PropertyMetaMeta} to be hashed
     * @return the hash code of the {@link PropertyMetaMeta}
     */
    public static int hashCode(@Nonnull PropertyMetaMeta propertyMeta) {
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
                .map(PropertyMetaMeta::toString)
                .collect(Collectors.joining(", "))
            + "}";
    }

    /**
     * The implementation of {@link PropertyMetaMeta#toString()}.
     *
     * @param property the {@link PropertyMetaMeta} to be string
     * @return a string representation of given {@link PropertyMetaMeta}
     */
    public static @Nonnull String toString(@Nonnull PropertyMetaMeta property) {
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
