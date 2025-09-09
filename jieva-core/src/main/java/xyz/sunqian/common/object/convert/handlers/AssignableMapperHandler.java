package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.lang.Flag;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * Default first {@link ObjectConverter.Handler} of {@link ObjectConverter#getHandlers()}, to check assignable relationship between source
 * and target types.
 * <p>
 * In this handler, if {@code source} is {@code null}, return {@link Val#ofNull()}.
 * <p>
 * Else if value of {@link ConversionOptions#getCopyLevel()} equals to {@link ConversionOptions#COPY_LEVEL_EQUAL} and
 * {@code targetType} equals to {@code targetType}, return {@code wrapResult(source)}.
 * <p>
 * Else if value of {@link ConversionOptions#getCopyLevel()} equals to {@link ConversionOptions#COPY_LEVEL_ASSIGNABLE}, and (
 * {@code targetType} equals to {@code Object.class} or {@code targetType} is assignable from {@code sourceType}),
 * return {@code wrapResult(source)}.
 * <p>
 * Else if {@code targetType} is {@link WildcardType} and has a lower bound (represents {@code ? super T}), return
 * {@code new Object()}.
 * <p>
 * Else if {@code targetType} is {@link WildcardType} and has an upper bound (represents {@code ? extends T}), let the
 * upper bound be {@code upperBound}, return
 * {@code mapper.asHandler().map(source, sourceType, upperBound, mapper, options)} or
 * {@code mapper.asHandler().mapProperty(source, sourceType, upper, targetProperty, mapper, options)}.
 * <p>
 * Else if {@code targetType} is {@link TypeVariable} and has only one bound (represents {@code T extends X}, excludes
 * {@code T extends X & Y}), let the bound be {@code bound}, return
 * {@code mapper.asHandler().map(source, sourceType, bound, mapper, options)} or
 * {@code mapper.asHandler().mapProperty(source, sourceType, bound, targetProperty, mapper, options)}.
 * <p>
 * Otherwise, return {@link Flag#CONTINUE}.
 *
 * @author fredsuvn
 */
public class AssignableMapperHandler implements ObjectConverter.Handler {

    public AssignableMapperHandler() {
    }

    @Override
    public Object map(
        @Nullable Object source, Type sourceType, Type targetType, ObjectConverter objectConverter, ConversionOptions options) {
        if (source == null) {
            return Val.ofNull();
        }
        if (options.getCopyLevel() == ConversionOptions.COPY_LEVEL_EQUAL && Objects.equals(source, targetType)) {
            return wrapResult(source);
        }
        if (options.getCopyLevel() == ConversionOptions.COPY_LEVEL_ASSIGNABLE) {
            if (Objects.equals(targetType, Object.class) || TypeKit.isAssignable(targetType, sourceType)) {
                return wrapResult(source);
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = TypeKit.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return new Object();
            }
            // ? extends T
            Type upper = TypeKit.getUpperBound(targetWildcard);
            return objectConverter.asHandler().map(source, sourceType, upper, objectConverter, options);
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = targetTypeVariable.getBounds();
            if (uppers.length == 1) {
                return objectConverter.asHandler().map(source, sourceType, uppers[0], objectConverter, options);
            }
        }
        return Flag.CONTINUE;
    }

    @Override
    public Object mapProperty(
        @Nullable Object source, Type sourceType, Type targetType, ObjectProperty targetProperty, ObjectConverter objectConverter, ConversionOptions options) {
        if (source == null) {
            return Val.ofNull();
        }
        if (options.getCopyLevel() == ConversionOptions.COPY_LEVEL_EQUAL && Objects.equals(source, targetType)) {
            return wrapResult(source);
        }
        if (options.getCopyLevel() == ConversionOptions.COPY_LEVEL_ASSIGNABLE) {
            if (Objects.equals(targetType, Object.class) || TypeKit.isAssignable(targetType, sourceType)) {
                return wrapResult(source);
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = TypeKit.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return new Object();
            }
            // ? extends T
            Type upper = TypeKit.getUpperBound(targetWildcard);
            return objectConverter.asHandler().mapProperty(source, sourceType, upper, targetProperty, objectConverter, options);
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = targetTypeVariable.getBounds();
            if (uppers.length == 1) {
                return objectConverter.asHandler().mapProperty(source, sourceType, uppers[0], targetProperty, objectConverter, options);
            }
        }
        return Flag.CONTINUE;
    }
}
