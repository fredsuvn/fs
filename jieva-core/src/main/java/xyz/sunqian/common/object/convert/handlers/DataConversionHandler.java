package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.convert.DataMapper;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.runtime.reflect.ClassKit;
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
import java.util.function.Supplier;

/**
 * The default last {@link ObjectConverter.Handler} of {@link ObjectConverter#defaultConverter()}.
 * <p>
 * This handler uses {@link BuilderFactory} to create a builder for the target type, then copies properties from the
 * source object to the builder, and finally builds the builder to the target object and returns.
 *
 * @author sunqian
 * @see BuilderFactory
 */
public class DataConversionHandler implements ObjectConverter.Handler {

    private static final BuilderFactory DEFAULT_BUILDER_PROVIDER = new DefaultBuilderFactory();

    /**
     * Returns the default builder factory.
     * <p>
     * The default builder factory supports {@link Map} and non-map type with an empty constructor. The supported map
     * types are:
     * <ul>
     *     <li>{@link Map}</li>
     *     <li>{@link AbstractMap}</li>
     *     <li>{@link LinkedHashMap}</li>
     *     <li>{@link HashMap}</li>
     *     <li>{@link TreeMap}</li>
     *     <li>{@link ConcurrentMap}</li>
     *     <li>{@link ConcurrentHashMap}</li>
     *     <li>{@link Hashtable}</li>
     *     <li>{@link ConcurrentSkipListMap}</li>
     * </ul>
     *
     * @return the default builder factory
     */
    public static @Nonnull DataConversionHandler.BuilderFactory defaultBuilderFactory() {
        return DEFAULT_BUILDER_PROVIDER;
    }

    private final BuilderFactory builderFactory;

    /**
     * Constructs with the specified builder factory.
     *
     * @param builderFactory the specified builder factory
     */
    public DataConversionHandler(@Nonnull DataConversionHandler.BuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

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
        DataMapper dataMapper = Jie.nonnull(
            Option.findValue(ConversionOptions.Key.DATA_MAPPER, options),
            DataMapper.defaultMapper()
        );
        Object builder = builderFactory.newBuilder(target);
        if (builder == null) {
            return ObjectConverter.Status.HANDLER_CONTINUE;
        }
        dataMapper.copyProperties(src, srcType, builder, target, converter, options);
        return builderFactory.build(builder);
    }

    /**
     * This interface is used to create object with the target type.
     * <p>
     * Firstly, it calls {@link #newBuilder(Type)} to create a builder object. Then the handler copies the properties to
     * the builder object. And finally calls {@link #build(Object)} to create the target object. Note the builder object
     * and the target object may be the same one.
     */
    public interface BuilderFactory {

        /**
         * Creates and returns a new builder object, or {@code null} if the target type is unsupported.
         *
         * @param target the target type
         * @return a new builder object, or {@code null} if the target type is unsupported
         */
        @Nullable
        Object newBuilder(@Nonnull Type target);

        /**
         * Creates the target object from the given builder.
         *
         * @param builder the given builder
         * @return the target object
         */
        @Nonnull
        Object build(@Nonnull Object builder);
    }

    private static final class DefaultBuilderFactory implements BuilderFactory {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull Supplier<@Nonnull Object>> NEW_INSTANCE_MAP;

        static {
            NEW_INSTANCE_MAP = new HashMap<>();
            NEW_INSTANCE_MAP.put(Map.class, LinkedHashMap::new);
            NEW_INSTANCE_MAP.put(AbstractMap.class, LinkedHashMap::new);
            NEW_INSTANCE_MAP.put(LinkedHashMap.class, LinkedHashMap::new);
            NEW_INSTANCE_MAP.put(HashMap.class, HashMap::new);
            NEW_INSTANCE_MAP.put(TreeMap.class, TreeMap::new);
            NEW_INSTANCE_MAP.put(ConcurrentMap.class, ConcurrentHashMap::new);
            NEW_INSTANCE_MAP.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
            NEW_INSTANCE_MAP.put(Hashtable.class, Hashtable::new);
            NEW_INSTANCE_MAP.put(ConcurrentSkipListMap.class, ConcurrentSkipListMap::new);
        }

        @Override
        public @Nullable Object newBuilder(@Nonnull Type target) {
            Supplier<Object> supplier = NEW_INSTANCE_MAP.get(target);
            if (supplier != null) {
                return supplier.get();
            }
            Class<?> rawType = TypeKit.getRawClass(target);
            if (rawType == null) {
                return null;
            }
            return ClassKit.newInstance(rawType);
        }

        @Override
        public @Nonnull Object build(@Nonnull Object builder) {
            return builder;
        }
    }
}
