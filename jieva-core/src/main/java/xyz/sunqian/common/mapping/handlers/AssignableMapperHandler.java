package xyz.sunqian.common.mapping.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.mapping.Mapper;
import xyz.sunqian.common.mapping.MappingOptions;
import xyz.sunqian.common.objects.data.DataProperty;
import xyz.sunqian.common.reflect.JieType;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * Default first {@link Mapper.Handler} of {@link Mapper#getHandlers()}, to check assignable relationship between source
 * and target types.
 * <p>
 * In this handler, if {@code source} is {@code null}, return {@link Val#ofNull()}.
 * <p>
 * Else if value of {@link MappingOptions#getCopyLevel()} equals to {@link MappingOptions#COPY_LEVEL_EQUAL} and
 * {@code targetType} equals to {@code targetType}, return {@code wrapResult(source)}.
 * <p>
 * Else if value of {@link MappingOptions#getCopyLevel()} equals to {@link MappingOptions#COPY_LEVEL_ASSIGNABLE}, and (
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
public class AssignableMapperHandler implements Mapper.Handler {

    public AssignableMapperHandler() {
    }

    @Override
    public Object map(
        @Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Val.ofNull();
        }
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_EQUAL && Objects.equals(source, targetType)) {
            return wrapResult(source);
        }
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_ASSIGNABLE) {
            if (Objects.equals(targetType, Object.class) || JieType.isAssignable(targetType, sourceType)) {
                return wrapResult(source);
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = JieType.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return new Object();
            }
            // ? extends T
            Type upper = JieType.getUpperBound(targetWildcard);
            return mapper.asHandler().map(source, sourceType, upper, mapper, options);
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = targetTypeVariable.getBounds();
            if (uppers.length == 1) {
                return mapper.asHandler().map(source, sourceType, uppers[0], mapper, options);
            }
        }
        return Flag.CONTINUE;
    }

    @Override
    public Object mapProperty(
        @Nullable Object source, Type sourceType, Type targetType, DataProperty targetProperty, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Val.ofNull();
        }
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_EQUAL && Objects.equals(source, targetType)) {
            return wrapResult(source);
        }
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_ASSIGNABLE) {
            if (Objects.equals(targetType, Object.class) || JieType.isAssignable(targetType, sourceType)) {
                return wrapResult(source);
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = JieType.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return new Object();
            }
            // ? extends T
            Type upper = JieType.getUpperBound(targetWildcard);
            return mapper.asHandler().mapProperty(source, sourceType, upper, targetProperty, mapper, options);
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = targetTypeVariable.getBounds();
            if (uppers.length == 1) {
                return mapper.asHandler().mapProperty(source, sourceType, uppers[0], targetProperty, mapper, options);
            }
        }
        return Flag.CONTINUE;
    }
}
