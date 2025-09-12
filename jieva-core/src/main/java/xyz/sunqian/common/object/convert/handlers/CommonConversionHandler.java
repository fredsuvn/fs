package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.lang.EnumKit;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.convert.DataBuilderFactory;
import xyz.sunqian.common.object.convert.DataMapper;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.IntFunction;

public class CommonConversionHandler implements ObjectConverter.Handler {

    @Override
    public Object convert(
        @Nullable Object src,
        @Nonnull Type srcType,
        @Nonnull Type target,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws Exception {
        if (src == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        if (target instanceof Class<?>) {
            Class<?> classType = (Class<?>) target;
            if (classType.isEnum()) {
                // enum:
                String name = src.toString();
                return EnumKit.findEnum(Jie.as(classType), name);
            }
        }
        Class<?> rawTarget = TypeKit.getRawClass(target);
        if (rawTarget == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }

        // Map or data object:
        IntFunction<Object> instantiator = Instantiator.get(rawTarget);
        DataMapper dataMapper = Jie.nonnull(
            Option.findValue(ConversionOptions.Key.DATA_MAPPER, options),
            DataMapper.defaultMapper()
        );
        if (instantiator != null) {
            Object targetObject = instantiator.apply(0);
            dataMapper.copyProperties(src, srcType, targetObject, target, converter, options);
            return targetObject;
        } else {
            DataBuilderFactory builderFactory = Jie.nonnull(
                Option.findValue(ConversionOptions.Key.BUILDER_FACTORY, options),
                DataBuilderFactory.defaultFactory()
            );
            Object targetBuilder = builderFactory.newBuilder(rawTarget);
            if (targetBuilder == null) {
                return ObjectConverter.Status.HANDLER_CONTINUE;
            }
            dataMapper.copyProperties(src, srcType, targetBuilder, target, converter, options);
            return builderFactory.build(targetBuilder);
        }
    }

    private static final class Instantiator {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Object>> CLASS_MAP;

        static {
            CLASS_MAP = new HashMap<>();
            CLASS_MAP.put(Map.class, HashMap::new);
            CLASS_MAP.put(AbstractMap.class, HashMap::new);
            CLASS_MAP.put(LinkedHashMap.class, LinkedHashMap::new);
            CLASS_MAP.put(HashMap.class, HashMap::new);
            CLASS_MAP.put(TreeMap.class, size -> new TreeMap<>());
            CLASS_MAP.put(ConcurrentMap.class, ConcurrentHashMap::new);
            CLASS_MAP.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
            CLASS_MAP.put(Hashtable.class, Hashtable::new);
            CLASS_MAP.put(ConcurrentSkipListMap.class, size -> new ConcurrentSkipListMap<>());
        }

        public static @Nullable IntFunction<@Nonnull Object> get(@Nonnull Class<?> target) {
            return CLASS_MAP.get(target);
        }
    }
}
