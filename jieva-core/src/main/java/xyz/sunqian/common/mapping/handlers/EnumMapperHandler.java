package xyz.sunqian.common.mapping.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.bean.PropertyInfo;
import xyz.sunqian.common.mapping.Mapper;
import xyz.sunqian.common.mapping.MappingOptions;

import java.lang.reflect.Type;

/**
 * Enum mapper handler implementation which is used to support mapping from any object to enum types.
 * <p>
 * If source object is null or target type is not an enum type, return {@link Flag#CONTINUE}.
 *
 * @author fredsuvn
 */
public class EnumMapperHandler implements Mapper.Handler {

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Flag.CONTINUE;
        }
        if (!(targetType instanceof Class<?>)) {
            return Flag.CONTINUE;
        }
        Class<?> enumType = (Class<?>) targetType;
        if (!enumType.isEnum()) {
            return Flag.CONTINUE;
        }
        String name = source.toString();
        return Jie.findEnum(enumType, name);
    }

    @Override
    public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
        return map(source, sourceType, targetType, mapper, options);
    }
}
