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
 * determine whether it is possible to return the source object directly without creating a new object. Using
 * {@link #getInstance()} can get a same one instance of this handler.
 * <p>
 * Its conversion logic is:
 * <ol>
 *     <li>
 *         If the specified source type is a {@link WildcardType} or {@link TypeVariable}, and it represents {@code ?}
 *         or {@code ? extends Object} or raw {@code T} or {@code T extends Object}, this handler will use
 *         {@link Object#getClass()} as the actual source type (or {@code Object.class} if the source object is
 *         {@code null}) to convert, the codes are simplified as:
 *         {@code return converter.convert(src, src == null ? Object.class : src.getClass(), targetType, options)};
 *     </li>
 *     <li>
 *         If the conversion enables {@link ConvertOption#NEW_INSTANCE_MODE}, returns
 *         {@link ObjectConverter.Status#HANDLER_CONTINUE} for any source type;
 *     </li>
 *     <li>
 *         If the specified source type equals to the target type, returns the source object itself;
 *     </li>
 *     <li>
 *         If the conversion enables {@link ConvertOption#STRICT_TARGET_TYPE_MODE}, returns
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

    private static final @Nonnull AssignableConvertHandler INST = new AssignableConvertHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull AssignableConvertHandler getInstance() {
        return INST;
    }

    @Override
    public Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type targetType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (srcType instanceof WildcardType) {
            if (isUndefined((WildcardType) srcType)) {
                Type actualSrcType = src == null ? Object.class : src.getClass();
                return converter.convert(src, actualSrcType, targetType, options);
            }
        }
        if (srcType instanceof TypeVariable) {
            if (isUndefined((TypeVariable<?>) srcType)) {
                Type actualSrcType = src == null ? Object.class : src.getClass();
                return converter.convert(src, actualSrcType, targetType, options);
            }
        }
        if (ConvertOption.isNewInstanceMode(options)) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        if (Objects.equals(targetType, srcType)) {
            return src;
        }
        if (ConvertOption.isStrictTargetTypeMode(options)) {
            // strict mode, wildcard is unsupported
            if (targetType instanceof WildcardType) {
                return ObjectConverter.Status.HANDLER_CONTINUE;
            }
        } else {
            // non-strict mode, wildcard and type variable will be considered as their bounds type
            if (targetType instanceof WildcardType) {
                WildcardType wildcard = (WildcardType) targetType;
                Type superType = TypeKit.getLowerBound(wildcard);
                if (superType != null) {
                    return converter.convert(src, srcType, superType, options);
                }
                return converter.convert(src, srcType, TypeKit.getUpperBound(wildcard), options);
            }
            if (targetType instanceof TypeVariable<?>) {
                return converter.convert(src, srcType, ((TypeVariable<?>) targetType).getBounds()[0], options);
            }
        }
        if (TypeKit.isCompatible(targetType, srcType)) {
            return src;
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }

    private boolean isUndefined(WildcardType type) {
        Type lowerBound = TypeKit.getLowerBound(type);
        if (lowerBound == null) {
            Type upperBound = TypeKit.getUpperBound(type);
            return Object.class.equals(upperBound);
        }
        return false;
    }

    private boolean isUndefined(TypeVariable<?> type) {
        Type upperBound = TypeKit.getFirstBound(type);
        return Object.class.equals(upperBound);
    }
}
