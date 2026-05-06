package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for object meta info, including map meta info ({@link MapMeta}) and non-map object meta info
 * ({@link ObjectMeta}).
 *
 * @author sunqian
 */
public class MetaKit {

    /**
     * The implementation of {@link ObjectMeta#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(ObjectMeta)}.
     *
     * @param objectMeta the compared {@link ObjectMeta}
     * @param other      the other {@link ObjectMeta}
     * @return whether the compared {@link ObjectMeta} is equal to other {@link ObjectMeta}
     */
    public static boolean equals(@Nonnull ObjectMeta objectMeta, @Nullable Object other) {
        if (objectMeta == other) {
            return true;
        }
        if (!(other instanceof ObjectMeta)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        ObjectMeta otherMeta = (ObjectMeta) other;
        return Objects.equals(objectMeta.type(), otherMeta.type())
            && Objects.equals(objectMeta.introspector(), otherMeta.introspector());
    }

    /**
     * The implementation of {@link PropertyMeta#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(PropertyMeta)}.
     *
     * @param propertyMeta the compared {@link PropertyMeta}
     * @param other        the other {@link PropertyMeta}
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
     * @param mapMeta the compared {@link MapMeta}
     * @param other   the other {@link MapMeta}
     * @return whether the compared {@link MapMeta} is equal to other {@link MapMeta}
     */
    public static boolean equals(@Nonnull MapMeta mapMeta, @Nullable Object other) {
        if (mapMeta == other) {
            return true;
        }
        if (!(other instanceof MapMeta)) {
            return false;
        }
        @SuppressWarnings("PatternVariableCanBeUsed")
        MapMeta otherMeta = (MapMeta) other;
        return Objects.equals(mapMeta.type(), otherMeta.type())
            && Objects.equals(mapMeta.introspector(), otherMeta.introspector());
    }

    /**
     * The implementation of {@link ObjectMeta#hashCode()}, and it works in conjunction with
     * {@link #equals(ObjectMeta, Object)}.
     *
     * @param objectMeta the {@link ObjectMeta} to be hashed
     * @return the hash code of the {@link ObjectMeta}
     */
    public static int hashCode(@Nonnull ObjectMeta objectMeta) {
        int result = 1;
        result = 31 * result + objectMeta.type().hashCode();
        result = 31 * result + objectMeta.introspector().hashCode();
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
     * @param mapMeta the {@link MapMeta} to be hashed
     * @return the hash code of the {@link MapMeta}
     */
    public static int hashCode(@Nonnull MapMeta mapMeta) {
        int result = 1;
        result = 31 * result + mapMeta.type().hashCode();
        result = 31 * result + mapMeta.introspector().hashCode();
        return result;
    }

    /**
     * The implementation of {@link ObjectMeta#toString()}.
     *
     * @param objectMeta the {@link ObjectMeta} to be string
     * @return a string representation of given {@link ObjectMeta}
     */
    public static @Nonnull String toString(@Nonnull ObjectMeta objectMeta) {
        return objectMeta.type().getTypeName() + "{" +
            objectMeta.properties().values().stream()
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
     * @param mapMeta the {@link MapMeta} to be string
     * @return a string representation of given {@link MapMeta}
     */
    public static @Nonnull String toString(@Nonnull MapMeta mapMeta) {
        return mapMeta.type().getTypeName();
    }

    private MetaKit() {
    }
}
