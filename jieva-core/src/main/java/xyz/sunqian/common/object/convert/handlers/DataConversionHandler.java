package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.convert.DataMapper;
import xyz.sunqian.common.object.convert.ObjectConversionException;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.runtime.invoke.Invocable;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Constructor;
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
 * <p>
 * Using {@link #defaultBuilderFactory()} can get the default builder factory.
 *
 * @author sunqian
 * @see BuilderFactory
 */
public class DataConversionHandler implements ObjectConverter.Handler {

    private static final BuilderFactory DEFAULT_BUILDER_FACTORY = new DefaultBuilderFactory();

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
     * <p>
     * Note the default builder factory has a {@link ConcurrentHashMap} to cache the instantiator (created from
     * {@link Invocable#of(Constructor)}) of the target type.
     *
     * @return the default builder factory
     */
    public static @Nonnull BuilderFactory defaultBuilderFactory() {
        return DEFAULT_BUILDER_FACTORY;
    }

    private final @Nonnull BuilderFactory builderFactory;

    /**
     * Constructs with the specified builder factory.
     *
     * @param builderFactory the specified builder factory
     */
    public DataConversionHandler(@Nonnull BuilderFactory builderFactory) {
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
         * @throws Exception for any exception
         */
        @Nullable
        Object newBuilder(@Nonnull Type target) throws Exception;

        /**
         * Creates the target object from the given builder.
         *
         * @param builder the given builder
         * @return the target object
         * @throws Exception for any exception
         */
        @Nonnull
        Object build(@Nonnull Object builder) throws Exception;
    }

    private static final class DefaultBuilderFactory implements BuilderFactory {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull Supplier<@Nonnull Object>> NEW_INSTANCE_MAP;

        static {
            NEW_INSTANCE_MAP = new HashMap<>();
            NEW_INSTANCE_MAP.put(Map.class, HashMap::new);
            NEW_INSTANCE_MAP.put(AbstractMap.class, HashMap::new);
            NEW_INSTANCE_MAP.put(LinkedHashMap.class, LinkedHashMap::new);
            NEW_INSTANCE_MAP.put(HashMap.class, HashMap::new);
            NEW_INSTANCE_MAP.put(TreeMap.class, TreeMap::new);
            NEW_INSTANCE_MAP.put(ConcurrentMap.class, ConcurrentHashMap::new);
            NEW_INSTANCE_MAP.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
            NEW_INSTANCE_MAP.put(Hashtable.class, Hashtable::new);
            NEW_INSTANCE_MAP.put(ConcurrentSkipListMap.class, ConcurrentSkipListMap::new);
        }

        private final @Nonnull ConcurrentHashMap<@Nonnull Class<?>, @Nonnull Invocable> instantiatorCache =
            new ConcurrentHashMap<>();

        @Override
        public @Nullable Object newBuilder(@Nonnull Type target) {
            Class<?> rawType = TypeKit.getRawClass(target);
            if (rawType == null) {
                return null;
            }
            Supplier<Object> supplier = NEW_INSTANCE_MAP.get(rawType);
            if (supplier != null) {
                return supplier.get();
            }
            Invocable instantiator = instantiatorCache.computeIfAbsent(rawType, t -> {
                try {
                    return Invocable.of(t.getConstructor());
                } catch (NoSuchMethodException e) {
                    throw new ObjectConversionException(e);
                }
            });
            return instantiator.invoke(null);
        }

        @Override
        public @Nonnull Object build(@Nonnull Object builder) {
            return builder;
        }
    }
}
