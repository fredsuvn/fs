package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * The default first {@link ObjectConverter.Handler} of {@link ObjectConverter#defaultConverter()}, mainly used to
 * determine whether it is possible to return the source object directly without creating a new object. Its conversion
 * logic is:
 * <ol>
 *     <li>
 *         if the specified source type equals to the target type, returns the source object itself;
 *     </li>
 *     <li>
 *         if the target type is assignable from the specified source type, returns the source object itself;
 *     </li>
 *     <li>
 *         if the target type is a wildcard type with a lower bound ({@code ? super}), returns
 *         {@link AssignableConversionHandler#SUPER};
 *     </li>
 *     <li>
 *         otherwise, returns {@link ObjectConverter.Status#HANDLER_CONTINUE}.
 *     </li>
 * </ol>
 *
 * @author fredsuvn
 */
public class AssignableConversionHandler implements ObjectConverter.Handler {

    /**
     * An instance of {@link Object}.
     */
    public static final @Nonnull Object SUPER = new Object();

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
        if (TypeKit.isAssignable(target, srcType)) {
            return src;
        }
        if (target instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) target;
            Type lower = TypeKit.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return SUPER;
            }
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }
}
