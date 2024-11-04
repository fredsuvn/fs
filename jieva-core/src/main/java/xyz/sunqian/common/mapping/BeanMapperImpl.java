package xyz.sunqian.common.mapping;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.bean.BeanInfo;
import xyz.sunqian.common.bean.BeanProvider;
import xyz.sunqian.common.bean.PropertyInfo;
import xyz.sunqian.common.ref.Val;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;

final class BeanMapperImpl implements BeanMapper {

    static BeanMapperImpl DEFAULT_MAPPER = new BeanMapperImpl();

    @Override
    public <T> T copyProperties(
        Object source, Type sourceType, T dest, Type destType, MappingOptions options
    ) throws MappingException {
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
            throw new MappingException(e);
        }
        return dest;
    }

    private void mapToMap(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        List<Type> sourceTypeArgs = getMapTypeArgs(sourceType);
        Type sourceKeyType = sourceTypeArgs.get(0);
        Type sourceValueType = sourceTypeArgs.get(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        List<Type> destParamType = getMapTypeArgs(destType);
        Type destKeyType = destParamType.get(0);
        Type destValueType = destParamType.get(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Collection<?> ignored = Jie.nonNull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonNull(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.nonNull(options.getMapper(), Mapper.defaultMapper());
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
                destMap, mapper, options
            );
        });
    }

    private void mapToBean(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        List<Type> sourceTypeArgs = getMapTypeArgs(sourceType);
        Type sourceKeyType = sourceTypeArgs.get(0);
        Type sourceValueType = sourceTypeArgs.get(1);
        Map<Object, Object> sourceMap = Jie.as(source);
        BeanProvider beanProvider = Jie.nonNull(options.getBeanProvider(), BeanProvider.defaultProvider());
        BeanInfo destInfo = beanProvider.getBeanInfo(destType);
        Map<String, PropertyInfo> destProperties = destInfo.getProperties();
        Collection<?> ignored = Jie.nonNull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonNull(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.nonNull(options.getMapper(), Mapper.defaultMapper());
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
                dest, destProperties, mapper, options
            );
        });
    }

    private void beanToMap(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        BeanProvider beanProvider = Jie.nonNull(options.getBeanProvider(), BeanProvider.defaultProvider());
        BeanInfo sourceInfo = beanProvider.getBeanInfo(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceInfo.getProperties();
        List<Type> destTypeArgs = getMapTypeArgs(destType);
        Type destKeyType = destTypeArgs.get(0);
        Type destValueType = destTypeArgs.get(1);
        Map<Object, Object> destMap = Jie.as(dest);
        Collection<?> ignored = Jie.nonNull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonNull(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.nonNull(options.getMapper(), Mapper.defaultMapper());
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
                sourceValue, property.getType(), destValueType,
                destMap, mapper, options
            );
        });
    }

    private void beanToBean(Object source, Type sourceType, Object dest, Type destType, MappingOptions options) {
        BeanProvider beanProvider = Jie.nonNull(options.getBeanProvider(), BeanProvider.defaultProvider());
        BeanInfo sourceInfo = beanProvider.getBeanInfo(sourceType);
        Map<String, PropertyInfo> sourceProperties = sourceInfo.getProperties();
        BeanInfo destInfo = beanProvider.getBeanInfo(destType);
        Map<String, PropertyInfo> destProperties = destInfo.getProperties();
        Collection<?> ignored = Jie.nonNull(options.getIgnored(), Collections.emptyList());
        boolean ignoreNull = options.isIgnoreNull();
        BiFunction<Object, Type, @Nullable Object> nameMapper = Jie.nonNull(options.getNameMapper(), (o1, o2) -> o1);
        Mapper mapper = Jie.nonNull(options.getMapper(), Mapper.defaultMapper());
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
                mappedKey, String.class, sourceValue, property.getType(),
                dest, destProperties, mapper, options
            );
        });
    }

    private List<Type> getMapTypeArgs(Type mapType) {
        return JieReflect.getActualTypeArguments(mapType, Map.class);
    }

    private void putToMap(
        Object mappedKey, Type sourceKeyType, Type destKeyType,
        Object sourceValue, Type sourceValueType, Type destValueType,
        Map<Object, Object> destMap, Mapper mapper, MappingOptions options
    ) {
        if (mappedKey instanceof Collection) {
            for (Object mk : ((Collection<?>) mappedKey)) {
                putToMap0(mk, sourceKeyType, destKeyType, sourceValue, sourceValueType, destValueType, destMap, mapper, options);
            }
        } else {
            putToMap0(mappedKey, sourceKeyType, destKeyType, sourceValue, sourceValueType, destValueType, destMap, mapper, options);
        }
    }

    private void putToMap0(
        Object mappedKey, Type sourceKeyType, Type destKeyType,
        Object sourceValue, Type sourceValueType, Type destValueType,
        Map<Object, Object> destMap, Mapper mapper, MappingOptions options
    ) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey = map(mapper, mappedKey, sourceKeyType, destKeyType, options);
        if (destKey == F.RETURN || destKey == null) {
            return;
        }
        if (!destMap.containsKey(destKey) && !options.isPutNew()) {
            return;
        }
        Object destValue = map(mapper, sourceValue, sourceValueType, destValueType, options);
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
        Object dest, Map<String, PropertyInfo> destProperties, Mapper mapper, MappingOptions options
    ) {
        if (mappedKey instanceof Collection) {
            for (Object mk : ((Collection<?>) mappedKey)) {
                putToBean0(mk, sourceKeyType, sourceValue, sourceValueType, dest, destProperties, mapper, options);
            }
        } else {
            putToBean0(mappedKey, sourceKeyType, sourceValue, sourceValueType, dest, destProperties, mapper, options);
        }
    }

    private void putToBean0(
        Object mappedKey, Type sourceKeyType, Object sourceValue, Type sourceValueType,
        Object dest, Map<String, PropertyInfo> destProperties, Mapper mapper, MappingOptions options
    ) {
        boolean ignoreError = options.isIgnoreError();
        Object destKey = map(mapper, mappedKey, sourceKeyType, String.class, options);
        if (destKey == F.RETURN || destKey == null) {
            return;
        }
        String destName = String.valueOf(destKey);
        PropertyInfo destProperty = destProperties.get(destName);
        if (destProperty == null || !destProperty.isWriteable()) {
            return;
        }
        Object destValue = mapProperty(mapper, sourceValue, sourceValueType, destProperty, options);
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
        Mapper mapper, @Nullable Object sourceValue, Type sourceType, Type destType, MappingOptions options) {
        return map0(mapper, sourceValue, sourceType, destType, null, options);
    }

    @Nullable
    private Object mapProperty(
        Mapper mapper, @Nullable Object sourceValue, Type sourceType, PropertyInfo destProperty, MappingOptions options) {
        return map0(mapper, sourceValue, sourceType, destProperty.getType(), destProperty, options);
    }

    @Nullable
    private Object map0(
        Mapper mapper,
        @Nullable Object sourceValue,
        Type sourceType,
        Type destType,
        @Nullable PropertyInfo destProperty,
        MappingOptions options
    ) {
        Object destValue;
        try {
            destValue = destProperty == null ?
                mapper.map(sourceValue, sourceType, destType, options)
                :
                mapper.mapProperty(sourceValue, sourceType, destType, destProperty, options);
        } catch (Exception e) {
            if (options.isIgnoreError()) {
                return F.RETURN;
            }
            throw new MappingException(sourceType, destType, e);
        }
        if (destValue == null) {
            if (options.isIgnoreError()) {
                return F.RETURN;
            }
            throw new MappingException(sourceValue, sourceType, destType);
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
