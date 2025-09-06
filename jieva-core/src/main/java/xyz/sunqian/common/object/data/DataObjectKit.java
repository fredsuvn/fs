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
        return dataSchema.type().getTypeName() + "{" +
            dataSchema.properties().values().stream()
                .map(DataProperty::toString)
                .collect(Collectors.joining(", "))
            + "}";
    }

    /**
     * The implementation of {@link DataProperty#toString()}.
     *
     * @param property the {@link DataProperty} to be string
     * @return a string representation of given {@link DataProperty}
     */
    public static @Nonnull String toString(@Nonnull DataProperty property) {
        return property.name() + ": " + property.type().getTypeName();
    }
}
