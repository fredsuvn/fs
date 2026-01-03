package space.sunqian.fs.object.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities for data object.
 *
 * @author sunqian
 */
public class DataObjectKit {

    /**
     * The implementation of {@link ObjectSchema#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(ObjectSchema)}.
     *
     * @param objectSchema the compared {@link ObjectSchema}
     * @param other        the other {@link ObjectSchema}
     * @return whether the compared {@link ObjectSchema} is equal to other {@link ObjectSchema}
     */
    public static boolean equals(@Nonnull ObjectSchema objectSchema, @Nullable Object other) {
        if (objectSchema == other) {
            return true;
        }
        if (!(other instanceof ObjectSchema)) {
            return false;
        }
        ObjectSchema otherSchema = (ObjectSchema) other;
        return Objects.equals(objectSchema.type(), otherSchema.type())
            && Objects.equals(objectSchema.parser(), otherSchema.parser());
    }

    /**
     * The implementation of {@link ObjectProperty#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(ObjectProperty)}.
     *
     * @param objectProperty the compared {@link ObjectProperty}
     * @param other          the other {@link ObjectProperty}
     * @return whether the compared {@link ObjectProperty} is equal to other {@link ObjectProperty}
     */
    public static boolean equals(@Nonnull ObjectProperty objectProperty, @Nullable Object other) {
        if (objectProperty == other) {
            return true;
        }
        if (!(other instanceof ObjectProperty)) {
            return false;
        }
        ObjectProperty otherProperty = (ObjectProperty) other;
        return Objects.equals(objectProperty.name(), otherProperty.name())
            && Objects.equals(objectProperty.owner(), otherProperty.owner());
    }

    /**
     * The implementation of {@link MapSchema#equals(Object)}, and it works in conjunction with
     * {@link #hashCode(MapSchema)}.
     *
     * @param mapSchema the compared {@link MapSchema}
     * @param other     the other {@link MapSchema}
     * @return whether the compared {@link MapSchema} is equal to other {@link MapSchema}
     */
    public static boolean equals(@Nonnull MapSchema mapSchema, @Nullable Object other) {
        if (mapSchema == other) {
            return true;
        }
        if (!(other instanceof MapSchema)) {
            return false;
        }
        MapSchema otherSchema = (MapSchema) other;
        return Objects.equals(mapSchema.type(), otherSchema.type())
            && Objects.equals(mapSchema.parser(), otherSchema.parser());
    }

    /**
     * The implementation of {@link ObjectSchema#hashCode()}, and it works in conjunction with
     * {@link #equals(ObjectSchema, Object)}.
     *
     * @param objectSchema the {@link ObjectSchema} to be hashed
     * @return the hash code of the {@link ObjectSchema}
     */
    public static int hashCode(@Nonnull ObjectSchema objectSchema) {
        int result = 1;
        result = 31 * result + objectSchema.type().hashCode();
        result = 31 * result + objectSchema.parser().hashCode();
        return result;
    }

    /**
     * The implementation of {@link ObjectProperty#hashCode()}, and it works in conjunction with
     * {@link #equals(ObjectProperty, Object)}.
     *
     * @param objectProperty the {@link ObjectProperty} to be hashed
     * @return the hash code of the {@link ObjectProperty}
     */
    public static int hashCode(@Nonnull ObjectProperty objectProperty) {
        int result = 1;
        result = 31 * result + objectProperty.name().hashCode();
        result = 31 * result + objectProperty.owner().hashCode();
        return result;
    }

    /**
     * The implementation of {@link MapSchema#hashCode()}, and it works in conjunction with
     * {@link #equals(MapSchema, Object)}.
     *
     * @param mapSchema the {@link MapSchema} to be hashed
     * @return the hash code of the {@link MapSchema}
     */
    public static int hashCode(@Nonnull MapSchema mapSchema) {
        int result = 1;
        result = 31 * result + mapSchema.type().hashCode();
        result = 31 * result + mapSchema.parser().hashCode();
        return result;
    }

    /**
     * The implementation of {@link ObjectSchema#toString()}.
     *
     * @param objectSchema the {@link ObjectSchema} to be string
     * @return a string representation of given {@link ObjectSchema}
     */
    public static @Nonnull String toString(@Nonnull ObjectSchema objectSchema) {
        return objectSchema.type().getTypeName() + "{" +
            objectSchema.properties().values().stream()
                .map(ObjectProperty::toString)
                .collect(Collectors.joining(", "))
            + "}";
    }

    /**
     * The implementation of {@link ObjectProperty#toString()}.
     *
     * @param property the {@link ObjectProperty} to be string
     * @return a string representation of given {@link ObjectProperty}
     */
    public static @Nonnull String toString(@Nonnull ObjectProperty property) {
        return property.name() + ": " + property.type().getTypeName();
    }

    /**
     * The implementation of {@link MapSchema#toString()}.
     *
     * @param mapSchema the {@link MapSchema} to be string
     * @return a string representation of given {@link MapSchema}
     */
    public static @Nonnull String toString(@Nonnull MapSchema mapSchema) {
        return mapSchema.type().getTypeName();
    }

    private DataObjectKit() {
    }
}
