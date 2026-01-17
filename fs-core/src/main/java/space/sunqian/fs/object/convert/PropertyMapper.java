package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.schema.DataSchema;

import java.util.Map;

/**
 * Property mapper for copying object property, this interface is typically called in {@code copyProperties} methods in
 * {@link PropertiesMapper}.
 *
 * @author sunqian
 */
public interface PropertyMapper {

    /**
     * Maps the source property with the specified name. this method determines the name and value of the actual
     * destination property that the specified property needs to be copied to. The returned entry's key and value are
     * the name and value of the actual destination property. If this method returns {@code null}, then the specified
     * property will not be copied.
     * <p>
     * This method is applicable to both {@link Map} and non-map object. For non-map objects, the type of property name
     * must be {@link String}.
     *
     * @param propertyName the name of the specified property to be copied
     * @param src          the source object
     * @param srcSchema    the schema of the source object
     * @param dst          the destination object
     * @param dstSchema    the schema of the destination object
     * @param converter    the converter used in the mapping process
     * @param options      the options used in the mapping process
     * @return the mapped name and value, may be {@code null} to ignore copy of this property
     */
    Map.@Nullable Entry<@Nonnull Object, Object> map(
        @Nonnull Object propertyName,
        @Nonnull Object src,
        @Nonnull DataSchema srcSchema,
        @Nonnull Object dst,
        @Nonnull DataSchema dstSchema,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    );
}
