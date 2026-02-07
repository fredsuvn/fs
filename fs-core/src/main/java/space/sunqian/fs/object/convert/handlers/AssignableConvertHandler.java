package space.sunqian.fs.object.convert.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * The default first {@link ObjectConverter.Handler} of {@link ObjectConverter#defaultConverter()}, mainly used to
 * determine whether it is possible to return the source object directly without creating a new object. An instance
 * {@link #INSTANCE} is provided for convenience and less memory usage.
 * <p>
 * Its conversion logic is:
 * <ol>
 *     <li>
 *         If the specified source type equals to the target type, returns the source object itself;
 *     </li>
 *     <li>
 *         If the {@link ConvertOption#STRICT_TYPE_MODE} option is enabled, returns
 *         {@link ObjectConverter.Status#HANDLER_CONTINUE} for target type of {@link WildcardType};
 *         Otherwise, recursively convert their lower/upper bounds type via the {@code converter} parameter;
 *     </li>
 *     <li>
 *         Using {@link TypeKit#isCompatible(Type, Type)} to check if the target type is compatible with the specified
 *         source type, and returns the source object itself if it is compatible;
 *     </li>
 *     <li>
 *         Otherwise, returns {@link ObjectConverter.Status#HANDLER_CONTINUE}.
 *     </li>
 * </ol>
 *
 * @author sunqian
 */
public class AssignableConvertHandler implements ObjectConverter.Handler {

    /**
     * An instance of this handler.
     */
    public static final @Nonnull AssignableConvertHandler INSTANCE = new AssignableConvertHandler();

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
        if (Option.containsKey(ConvertOption.STRICT_TYPE_MODE, options)) {
            // strict mode, wildcard is unsupported
            if (target instanceof WildcardType) {
                return ObjectConverter.Status.HANDLER_CONTINUE;
            }
        } else {
            // non-strict mode, wildcard and type variable will be considered as their bounds type
            if (target instanceof WildcardType) {
                WildcardType wildcard = (WildcardType) target;
                Type superType = TypeKit.getLowerBound(wildcard);
                if (superType != null) {
                    return converter.convert(src, srcType, superType, options);
                }
                return converter.convert(src, srcType, TypeKit.getUpperBound(wildcard), options);
            }
            if (target instanceof TypeVariable<?>) {
                return converter.convert(src, srcType, ((TypeVariable<?>) target).getBounds()[0], options);
            }
        }
        if (TypeKit.isCompatible(target, srcType)) {
            return src;
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }
}
