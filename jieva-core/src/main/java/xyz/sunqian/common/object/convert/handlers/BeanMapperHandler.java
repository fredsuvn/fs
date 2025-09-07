package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.lang.Flag;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.object.convert.BeanMapper;
import xyz.sunqian.common.object.convert.Mapper;
import xyz.sunqian.common.object.convert.MappingOptions;
import xyz.sunqian.common.object.data.DataProperty;
import xyz.sunqian.common.runtime.reflect.ClassKit;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
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
 * Bean mapper handler implementation, to create and map properties for target type from source object. By default, this
 * handler is last handler in {@link Mapper#getHandlers()}.
 * <p>
 * This handler has a bean generator ({@link BeanGenerator}). If source object is {@code null}, return
 * {@link Flag#CONTINUE}. Else the generator tries to create a new object of target type as target bean object, if the
 * generator return {@code null}, this handler return {@link Flag#CONTINUE}, else this handler will get the bean mapper
 * by {@link MappingOptions#getBeanMapper()} and call
 * {@link BeanMapper#copyProperties(Object, Type, Object, Type, MappingOptions)} to map the properties, then return
 * target object wrapped by {@link #wrapResult(Object)} ({@code wrapResult(targetBean)}).
 * <p>
 * The generator should be specified in {@link #BeanMapperHandler(BeanGenerator)}, or use default generator
 * ({@link #DEFAULT_GENERATOR}) in {@link #BeanMapperHandler()}. Default generator supports these target types of
 * {@link Map}:
 * <ul>
 *     <li>{@link Map};</li>
 *     <li>{@link AbstractMap};</li>
 *     <li>{@link LinkedHashMap};</li>
 *     <li>{@link HashMap};</li>
 *     <li>{@link TreeMap};</li>
 *     <li>{@link ConcurrentMap};</li>
 *     <li>{@link ConcurrentHashMap};</li>
 *     <li>{@link Hashtable};</li>
 *     <li>{@link ConcurrentSkipListMap};</li>
 * </ul>
 * If target type is not type of {@link Map}, the generator tries to get raw class and find empty constructor of the raw
 * class to create new instance. If the raw class is not found, return {@link Flag#CONTINUE}, it is equivalent to:
 * <pre>
 *     Class<?> rawType = JieReflect.getRawType(type);
 *     if (rawType == null) {
 *         return Flag.CONTINUE;
 *     }
 *     return JieReflect.newInstance(rawType);
 * </pre>
 * Method {@link BeanGenerator#build(Object)} of default generator will directly return given builder itself.
 *
 * @author fredsuvn
 * @see BeanGenerator
 */
public class BeanMapperHandler implements Mapper.Handler {

    private static final Map<Type, Supplier<Object>> NEW_INSTANCE_MAP = new HashMap<>();

    static {
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

    /**
     * Default bean generator.
     */
    public static final BeanGenerator DEFAULT_GENERATOR = new BeanGenerator() {
        @Override
        public @Nullable Object generate(Type type) {
            Supplier<Object> supplier = NEW_INSTANCE_MAP.get(type);
            if (supplier != null) {
                return supplier.get();
            }
            Class<?> rawType = TypeKit.getRawClass(type);
            if (rawType == null) {
                return null;
            }
            Supplier<Object> rawSupplier = NEW_INSTANCE_MAP.get(rawType);
            if (rawSupplier != null) {
                return rawSupplier.get();
            }
            return ClassKit.newInstance(rawType);
        }

        @Override
        public Object build(Object builder) {
            return builder;
        }
    };

    private final BeanGenerator generator;

    /**
     * Constructs with {@link #DEFAULT_GENERATOR}.
     */
    public BeanMapperHandler() {
        this(DEFAULT_GENERATOR);
    }

    /**
     * Constructs with specified bean generator.
     *
     * @param generator specified bean generator
     */
    public BeanMapperHandler(BeanGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Flag.CONTINUE;
        }
        if (!(sourceType instanceof Class<?>) && !(sourceType instanceof ParameterizedType)) {
            return Flag.CONTINUE;
        }
        Object targetObject = generator.generate(targetType);
        if (targetObject == null) {
            return Flag.CONTINUE;
        }
        BeanMapper beanMapper = Jie.nonnull(options.getBeanMapper(), BeanMapper.defaultMapper());
        beanMapper.copyProperties(source, sourceType, targetObject, targetType, options);
        return wrapResult(generator.build(targetObject));
    }

    @Override
    public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, DataProperty targetProperty, Mapper mapper, MappingOptions options) {
        return map(source, sourceType, targetType, mapper, options);
    }

    /**
     * Bean generator to create new bean object of target type.
     * <p>
     * A bean object may be mutable or immutable. If it is mutable, {@link #generate(Type)} will directly create a new
     * instance of bean, and {@link #build(Object)} should do nothing. If the bean is immutable, {@link #generate(Type)}
     * should create a builder object of bean, and {@link #build(Object)} should complete to build object of bean.
     */
    public interface BeanGenerator {

        /**
         * Generates and returns a new bean object or its builder object of target type, or returns {@code null} if
         * unsupported.
         *
         * @param targetType target type
         * @return a new bean object or its builder object of target type or null if unsupported
         */
        @Nullable
        Object generate(Type targetType);

        /**
         * Builds bean object of given bean builder. If the builder is bean object itself, return itself.
         *
         * @param builder given bean builder
         * @return bean object
         */
        Object build(Object builder);
    }
}
