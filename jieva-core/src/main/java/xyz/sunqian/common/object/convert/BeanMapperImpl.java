package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.object.data.ObjectSchema;
import xyz.sunqian.common.object.data.ObjectSchemaParser;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

final class BeanMapperImpl implements BeanMapper {

    static BeanMapperImpl DEFAULT_MAPPER = new BeanMapperImpl();

    @Override
    public <T> T copyProperties(
        Object source, Type sourceType, T dest, Type destType, ConversionOptions options
    ) throws ObjectConversionException {
        try {
            if (source instanceof Map) {
                if (dest instanceof Map) {
                    mapToMap(source, sourceType, dest, destType, options);
                } else {
                    mapToBean(source, sourceType, dest, destType, options);
                }
            } else {
                if (dest instanceof Map) {
                    beanToMap(source, sourceType, dest, destType, options);
                } else {
                    beanToBean(source, sourceType, dest, destType, options);
                }
            }
        } catch (Exception e) {
            throw new ObjectConversionException(e);
        }
        return dest;
    }

    private void mapToMap(Object source, Type sourceType, Object dest, Type destType, ConversionOptions options) {
        List<Type> sourceTypeArgs = getMapTypeArgs(sourceType);
        Type sourceKeyType = sourceTypeArgs.get(0);
        Type sourceValueType = sourceTypeArgs.get(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        List<Type> destParamType = getMapTypeArgs(destType);
        Type destKeyType = destParamType.get(0);
        Type destValueType = destParamType.get(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Collection<?> ignored = Jie.nonnull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonnull(options.getNameMapper(), (o1, o2) -> o1);
        ObjectConverter objectConverter = Jie.nonnull(options.getObjectConverter(), ObjectConverter.defaultConverter());
        boolean ignoreError = options.isIgnoreError();
        sourceMap.forEach((key, value) -> {
            if (ignored.contains(key)) {
                return;
            }
            if (value == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper.apply(key, sourceKeyType);
            if (mappedKey == null) {
                return;
            }
            putToMap(
                mappedKey, sourceKeyType, destKeyType,
                value, sourceValueType, destValueType,
                destMap, objectConverter, options
            );
        });
    }

    private void mapToBean(Object source, Type sourceType, Object dest, Type destType, ConversionOptions options) {
        List<Type> sourceTypeArgs = getMapTypeArgs(sourceType);
        Type sourceKeyType = sourceTypeArgs.get(0);
        Type sourceValueType = sourceTypeArgs.get(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        ObjectSchemaParser beanProvider = Jie.nonnull(options.getObjectSchemaParser(), ObjectSchemaParser.defaultParser());
        ObjectSchema destInfo = ObjectSchema.parse(destType);
        Map<String, ObjectProperty> destProperties = destInfo.properties();
        Collection<?> ignored = Jie.nonnull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonnull(options.getNameMapper(), (o1, o2) -> o1);
        ObjectConverter objectConverter = Jie.nonnull(options.getObjectConverter(), ObjectConverter.defaultConverter());
        boolean ignoreError = options.isIgnoreError();
        sourceMap.forEach((key, value) -> {
            if (ignored.contains(key)) {
                return;
            }
            if (value == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper.apply(key, sourceKeyType);
            if (mappedKey == null) {
                return;
            }
            putToBean(
                mappedKey, sourceKeyType, value, sourceValueType,
                dest, destProperties, objectConverter, options
            );
        });
    }

    private void beanToMap(Object source, Type sourceType, Object dest, Type destType, ConversionOptions options) {
        ObjectSchemaParser beanProvider = Jie.nonnull(options.getObjectSchemaParser(), ObjectSchemaParser.defaultParser());
        ObjectSchema sourceInfo = ObjectSchema.parse(sourceType);
        Map<String, ObjectProperty> sourceProperties = sourceInfo.properties();
        List<Type> destTypeArgs = getMapTypeArgs(destType);
        Type destKeyType = destTypeArgs.get(0);
        Type destValueType = destTypeArgs.get(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Collection<?> ignored = Jie.nonnull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonnull(options.getNameMapper(), (o1, o2) -> o1);
        ObjectConverter objectConverter = Jie.nonnull(options.getObjectConverter(), ObjectConverter.defaultConverter());
        boolean ignoreError = options.isIgnoreError();
        boolean ignoreClass = options.isIgnoreClass();
        sourceProperties.forEach((name, property) -> {
            if (ignored.contains(name) || !property.isReadable()) {
                return;
            }
            if (ignoreClass && Objects.equals(name, "class")) {
                return;
            }
            Object sourceValue = property.getValue(source);
            if (sourceValue == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper.apply(name, String.class);
            if (mappedKey == null) {
                return;
            }
            putToMap(
                mappedKey, String.class, destKeyType,
                sourceValue, property.type(), destValueType,
                destMap, objectConverter, options
            );
        });
    }

    private void beanToBean(Object source, Type sourceType, Object dest, Type destType, ConversionOptions options) {
        ObjectSchemaParser beanProvider = Jie.nonnull(options.getObjectSchemaParser(), ObjectSchemaParser.defaultParser());
        ObjectSchema sourceInfo = ObjectSchema.parse(sourceType);
        Map<String, ObjectProperty> sourceProperties = sourceInfo.properties();
        ObjectSchema destInfo = ObjectSchema.parse(destType);
        Map<String, ObjectProperty> destProperties = destInfo.properties();
        Collection<?> ignored = Jie.nonnull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonnull(options.getNameMapper(), (o1, o2) -> o1);
        ObjectConverter objectConverter = Jie.nonnull(options.getObjectConverter(), ObjectConverter.defaultConverter());
        boolean ignoreError = options.isIgnoreError();
        boolean ignoreClass = options.isIgnoreClass();
        sourceProperties.forEach((name, property) -> {
            if (ignored.contains(name) || !property.isReadable()) {
                return;
            }
            if (ignoreClass && Objects.equals(name, "class")) {
                return;
            }
            Object sourceValue = property.getValue(source);
            if (sourceValue == null && ignoreNull) {
                return;
            }
            Object mappedKey = nameMapper.apply(name, String.class);
            if (mappedKey == null) {
                return;
            }
            putToBean(
                mappedKey, String.class, sourceValue, property.type(),
                dest, destProperties, objectConverter, options
            );
        });
    }

    private List<Type> getMapTypeArgs(Type mapType) {
        return TypeKit.resolveActualTypeArguments(mapType, Map.class);
    }

    private void putToMap(
        Object mappedKey, Type sourceKeyType, Type destKeyType,
        Object sourceValue, Type sourceValueType, Type destValueType,
        Map<Object, Object> destMap, ObjectConverter objectConverter, ConversionOptions options
    ) {
        if (mappedKey instanceof Collection) {
            for (Object mk : ((Collection<?>) mappedKey)) {
                putToMap0(mk, sourceKeyType, destKeyType, sourceValue, sourceValueType, destValueType, destMap, objectConverter, options);
            }
        } else {
            putToMap0(mappedKey, sourceKeyType, destKeyType, sourceValue, sourceValueType, destValueType, destMap, objectConverter, options);
        }
    }

    private void putToMap0(
        Object mappedKey, Type sourceKeyType, Type destKeyType,
        Object sourceValue, Type sourceValueType, Type destValueType,
        Map<Object, Object> destMap, ObjectConverter objectConverter, ConversionOptions options
    ) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey = map(objectConverter, mappedKey, sourceKeyType, destKeyType, options);
        if (destKey == F.RETURN || destKey == null) {
            return;
        }
        if (!destMap.containsKey(destKey) && !options.isPutNew()) {
            return;
        }
        Object destValue = map(objectConverter, sourceValue, sourceValueType, destValueType, options);
        if (destValue == F.RETURN) {
            return;
        }
        if (destValue == null && options.isIgnoreNull()) {
            return;
        }
        destMap.put(destKey, destValue);
    }

    private void putToBean(
        Object mappedKey, Type sourceKeyType, Object sourceValue, Type sourceValueType,
        Object dest, Map<String, ObjectProperty> destProperties, ObjectConverter objectConverter, ConversionOptions options
    ) {
        if (mappedKey instanceof Collection) {
            for (Object mk : ((Collection<?>) mappedKey)) {
                putToBean0(mk, sourceKeyType, sourceValue, sourceValueType, dest, destProperties, objectConverter, options);
            }
        } else {
            putToBean0(mappedKey, sourceKeyType, sourceValue, sourceValueType, dest, destProperties, objectConverter, options);
        }
    }

    private void putToBean0(
        Object mappedKey, Type sourceKeyType, Object sourceValue, Type sourceValueType,
        Object dest, Map<String, ObjectProperty> destProperties, ObjectConverter objectConverter, ConversionOptions options
    ) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey = map(objectConverter, mappedKey, sourceKeyType, String.class, options);
        if (destKey == F.RETURN || destKey == null) {
            return;
        }
        String destName = String.valueOf(destKey);
        ObjectProperty destProperty = destProperties.get(destName);
        if (destProperty == null || !destProperty.isWritable()) {
            return;
        }
        Object destValue = mapProperty(objectConverter, sourceValue, sourceValueType, destProperty, options);
        if (destValue == F.RETURN) {
            return;
        }
        if (destValue == null && options.isIgnoreNull()) {
            return;
        }
        destProperty.setValue(dest, destValue);
    }

    @Nullable
    private Object map(
        ObjectConverter objectConverter, @Nullable Object sourceValue, Type sourceType, Type destType, ConversionOptions options) {
        return map0(objectConverter, sourceValue, sourceType, destType, null, options);
    }

    @Nullable
    private Object mapProperty(
        ObjectConverter objectConverter, @Nullable Object sourceValue, Type sourceType, ObjectProperty destProperty, ConversionOptions options) {
        return map0(objectConverter, sourceValue, sourceType, destProperty.type(), destProperty, options);
    }

    @Nullable
    private Object map0(
        ObjectConverter objectConverter,
        @Nullable Object sourceValue,
        Type sourceType,
        Type destType,
        @Nullable ObjectProperty destProperty,
        ConversionOptions options
    ) {
        Object destValue;
        try {
            destValue = destProperty == null ?
                objectConverter.map(sourceValue, sourceType, destType, options)
                :
                objectConverter.mapProperty(sourceValue, sourceType, destType, destProperty, options);
        } catch (Exception e) {
            if (options.isIgnoreError()) {
                return F.RETURN;
            }
            throw new ObjectConversionException(sourceType, destType, e);
        }
        if (destValue == null) {
            if (options.isIgnoreError()) {
                return F.RETURN;
            }
            throw new ObjectConversionException(sourceType, destType);
        }
        if (destValue instanceof Val) {
            destValue = ((Val<?>) destValue).get();
        }
        return destValue;
    }

    private enum F {
        RETURN
    }
}
