package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.MappingOption;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * The default first {@link ObjectConverter.Handler} of {@link ObjectConverter#defaultConverter()}, mainly used to
 * determine whether it is possible to return the source object directly without creating a new object. Its conversion
 * logic is:
 * <ol>
 *     <li>
 *         If the specified source type equals to the target type, returns the source object itself;
 *     </li>
 *     <li>
 *         If the {@link MappingOption#STRICT_TYPE} option is enabled, returns
 *         {@link ObjectConverter.Status#HANDLER_CONTINUE} for target type of {@link WildcardType};
 *         Otherwise, the source will be returned directly for target type of {@link WildcardType} and
 *         {@link TypeVariable};
 *     </li>
 *     <li>
 *         If the target type is assignable from the specified source type, returns the source object itself;
 *     </li>
 *     <li>
 *         Otherwise, returns {@link ObjectConverter.Status#HANDLER_CONTINUE}.
 *     </li>
 * </ol>
 *
 * @author sunqian
 */
public class AssignableConversionHandler implements ObjectConverter.Handler {

    @Override
    public Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (Objects.equals(target, srcType)) {
            return src;
        }
        if (Option.hasKey(MappingOption.STRICT_TYPE, options)) {
            // strict mode, wildcard is unsupported
            if (target instanceof WildcardType) {
                return ObjectConverter.Status.HANDLER_CONTINUE;
            }
        } else {
            // non-strict mode, wildcard and type variable will be considered as Object.class
            // and the src will be returned directly
            if (target instanceof WildcardType) {
                return src;
            }
            if (target instanceof TypeVariable<?>) {
                return src;
            }
        }
        if (TypeKit.isAssignable(target, srcType)) {
            return src;
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }
}
