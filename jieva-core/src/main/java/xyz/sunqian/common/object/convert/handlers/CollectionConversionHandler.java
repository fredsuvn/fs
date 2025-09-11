package xyz.sunqian.common.object.convert.handlers;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.lang.Flag;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.object.convert.ConversionOptions;
import xyz.sunqian.common.object.convert.ObjectConversionException;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.data.ObjectProperty;
import xyz.sunqian.common.runtime.reflect.TypeKit;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;

/**
 * Collection mapper handler implementation, to create and map elements for target collection type from source
 * collection type.
 * <p>
 * This handler has a collection generator ({@link CollectionFactory}). If source object is {@code null}, or source type
 * or target type is not subtype of {@link Iterable}, array, {@link GenericArrayType}, return {@link Flag#CONTINUE}.
 * Else the generator tries to create a new collection of target type as target collection, if the generator return
 * {@code null}, this handler return {@link Flag#CONTINUE}, else this handler will map all component of source object by
 * {@link ObjectConverter#map(Object, Type, Type, ConversionOptions)} or
 * {@link ObjectConverter#mapProperty(Object, Type, Type, ObjectProperty, ConversionOptions)}, then return target
 * collection wrapped by {@link #wrapResult(Object)} ({@code wrapResult(targetCollection)}).
 * <p>
 * The generator should be specified in {@link #CollectionConversionHandler(CollectionFactory)}, or use default
 * generator ({@link #DEFAULT_GENERATOR}) in {@link #CollectionConversionHandler()}. Default generator supports these
 * target collection types:
 * <ul>
 *     <li>any array;</li>
 *     <li>{@link Iterable};</li>
 *     <li>{@link Collection};</li>
 *     <li>{@link List};</li>
 *     <li>{@link AbstractList};</li>
 *     <li>{@link ArrayList};</li>
 *     <li>{@link LinkedList};</li>
 *     <li>{@link CopyOnWriteArrayList};</li>
 *     <li>{@link Set};</li>
 *     <li>{@link LinkedHashSet};</li>
 *     <li>{@link HashSet};</li>
 *     <li>{@link TreeSet};</li>
 *     <li>{@link ConcurrentSkipListSet};</li>
 * </ul>
 *
 * @author fredsuvn
 */
public class CollectionConversionHandler implements ObjectConverter.Handler {
    @Override
    public Object convert(@Nullable Object src, @Nonnull Type srcType, @Nonnull Type target, @Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) throws Exception {
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }


    // private final CollectionFactory generator;
    //
    // /**
    //  * Constructs with {@link #DEFAULT_GENERATOR}.
    //  */
    // public CollectionConversionHandler() {
    //     this(DEFAULT_GENERATOR);
    // }
    //
    // /**
    //  * Constructs with specified collection generator.
    //  *
    //  * @param generator specified collection generator
    //  */
    // public CollectionConversionHandler(CollectionFactory generator) {
    //     this.generator = generator;
    // }
    //
    // @Override
    // public Object map(@Nullable Object source, Type sourceType, Type targetType, ObjectConverter objectConverter, ConversionOptions options) {
    //     return mapProperty(source, sourceType, targetType, null, objectConverter, options);
    // }
    //
    // @Override
    // public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, @Nullable ObjectProperty targetProperty, ObjectConverter objectConverter, ConversionOptions options) {
    //     if (source == null) {
    //         return Flag.CONTINUE;
    //     }
    //     Type targetComponentType = getComponentType(targetType);
    //     if (targetComponentType == null) {
    //         return Flag.CONTINUE;
    //     }
    //     Type sourceComponentType = getComponentType(sourceType);
    //     if (sourceComponentType == null) {
    //         return Flag.CONTINUE;
    //     }
    //     Object targetCollection = generator.create(targetType, getSourceSize(source));
    //     if (targetCollection == null) {
    //         return Flag.CONTINUE;
    //     }
    //     int i = 0;
    //     if (source instanceof Iterable<?>) {
    //         for (Object sourceComponent : (Iterable<?>) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof Object[]) {
    //         for (Object sourceComponent : (Object[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof boolean[]) {
    //         for (Object sourceComponent : (boolean[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof byte[]) {
    //         for (Object sourceComponent : (byte[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof short[]) {
    //         for (Object sourceComponent : (short[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof char[]) {
    //         for (Object sourceComponent : (char[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof int[]) {
    //         for (Object sourceComponent : (int[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof long[]) {
    //         for (Object sourceComponent : (long[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof float[]) {
    //         for (Object sourceComponent : (float[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     if (source instanceof double[]) {
    //         for (Object sourceComponent : (double[]) source) {
    //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
    //         }
    //         return wrapResult(targetCollection);
    //     }
    //     return Flag.CONTINUE;
    // }
    //
    // @Nullable
    // private Type getComponentType(Type type) {
    //     if (type instanceof Class<?>) {
    //         if (((Class<?>) type).isArray()) {
    //             return ((Class<?>) type).getComponentType();
    //         }
    //         if (Iterable.class.isAssignableFrom((Class<?>) type)) {
    //             return Object.class;
    //         }
    //         return null;
    //     }
    //     if (type instanceof ParameterizedType) {
    //         List<Type> sourceComponent = TypeKit.resolveActualTypeArguments(type, Iterable.class);
    //         if (CollectKit.isEmpty(sourceComponent)) {
    //             return null;
    //         }
    //         return sourceComponent.get(0);
    //     }
    //     if (type instanceof GenericArrayType) {
    //         return ((GenericArrayType) type).getGenericComponentType();
    //     }
    //     return null;
    // }
    //
    // private int getSourceSize(Object source) {
    //     if (source instanceof Collection) {
    //         return ((Collection<?>) source).size();
    //     }
    //     if (source.getClass().isArray()) {
    //         return Array.getLength(source);
    //     }
    //     return -1;
    // }
    //
    // private void addComponent(
    //     Object targetCollection,
    //     int index,
    //     @Nullable Object sourceComponent,
    //     Type sourceComponentType,
    //     Type targetComponentType,
    //     @Nullable ObjectProperty targetProperty,
    //     ObjectConverter objectConverter,
    //     ConversionOptions options
    // ) {
    //     Object targetComponent;
    //     Object targetResult = targetProperty == null ?
    //         objectConverter.map(sourceComponent, sourceComponentType, targetComponentType, options)
    //         :
    //         objectConverter.mapProperty(sourceComponent, sourceComponentType, targetComponentType, targetProperty, options);
    //     if (targetResult == null) {
    //         if (options.isIgnoreError()) {
    //             targetComponent = null;
    //         } else {
    //             throw new ObjectConversionException(sourceComponentType, targetComponentType);
    //         }
    //     } else {
    //         targetComponent = ObjectConverter.resolveResult(targetResult);
    //     }
    //     if (targetCollection instanceof Collection<?>) {
    //         Collection<Object> target = Jie.as(targetCollection);
    //         target.add(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof Object[]) {
    //         ((Object[]) targetCollection)[index] = targetComponent;
    //         return;
    //     }
    //     if (targetCollection instanceof boolean[]) {
    //         ((boolean[]) targetCollection)[index] = targetComponent == null ? false : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof byte[]) {
    //         ((byte[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof short[]) {
    //         ((short[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof char[]) {
    //         ((char[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof int[]) {
    //         ((int[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof long[]) {
    //         ((long[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof float[]) {
    //         ((float[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    //     if (targetCollection instanceof double[]) {
    //         ((double[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
    //         return;
    //     }
    // }
    //
    // /**
    //  * This interface is used to create collection object with the target type.
    //  */
    // public interface CollectionFactory {
    //
    //     /**
    //      * Creates and returns a new collection or array with the target type and initial size, or returns {@code null}
    //      * if the target type is unsupported.
    //      *
    //      * @param target      the target type
    //      * @param initialSize the initial size
    //      * @return a new collection or array object
    //      */
    //     @Nullable
    //     Object create(@Nonnull Type target, int initialSize);
    // }
    //
    // /**
    //  * Default collection generator.
    //  */
    // public static final CollectionFactory DEFAULT_GENERATOR = new DefaultCollectionFactory();
    //
    // private static final class DefaultCollectionFactory implements CollectionFactory {
    //
    //     private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Object>> NEW_INSTANCE_MAP;
    //
    //     static {
    //         NEW_INSTANCE_MAP = new HashMap<>();
    //         NEW_INSTANCE_MAP.put(Iterable.class, (s) -> s > 0 ? new ArrayList<>(s) : new ArrayList<>());
    //         NEW_INSTANCE_MAP.put(Collection.class, (s) -> s > 0 ? new ArrayList<>(s) : new ArrayList<>());
    //         NEW_INSTANCE_MAP.put(List.class, (s) -> s > 0 ? new ArrayList<>(s) : new ArrayList<>());
    //         NEW_INSTANCE_MAP.put(AbstractList.class, (s) -> s > 0 ? new ArrayList<>(s) : new ArrayList<>());
    //         NEW_INSTANCE_MAP.put(ArrayList.class, (s) -> s > 0 ? new ArrayList<>(s) : new ArrayList<>());
    //         NEW_INSTANCE_MAP.put(LinkedList.class, (s) -> new LinkedList<>());
    //         NEW_INSTANCE_MAP.put(CopyOnWriteArrayList.class, (s) -> new CopyOnWriteArrayList<>());
    //         NEW_INSTANCE_MAP.put(Set.class, (s) -> s > 0 ? new LinkedHashSet<>(s) : new LinkedHashSet<>());
    //         NEW_INSTANCE_MAP.put(LinkedHashSet.class, (s) -> s > 0 ? new LinkedHashSet<>(s) : new LinkedHashSet<>());
    //         NEW_INSTANCE_MAP.put(HashSet.class, (s) -> s > 0 ? new HashSet<>(s) : new HashSet<>());
    //         NEW_INSTANCE_MAP.put(TreeSet.class, (s) -> new TreeSet<>());
    //         NEW_INSTANCE_MAP.put(ConcurrentSkipListSet.class, (s) -> new ConcurrentSkipListSet<>());
    //     }
    //
    //     @Nullable
    //     public Object create(@Nonnull Type target, int initialSize) {
    //         IntFunction<Object> func = NEW_INSTANCE_MAP.get(target);
    //         if (func != null) {
    //             return func.apply(initialSize);
    //         }
    //         if (target instanceof Class<?>) {
    //             if (((Class<?>) target).isArray()) {
    //                 return Array.newInstance(((Class<?>) type).getComponentType(), size);
    //             }
    //         }
    //         if (type instanceof GenericArrayType) {
    //             Type componentType = ((GenericArrayType) type).getGenericComponentType();
    //             Class<?> componentClass = TypeKit.getRawClass(componentType);
    //             if (componentClass == null && componentType instanceof TypeVariable<?>) {
    //                 componentClass = Object.class;
    //             }
    //             return Array.newInstance(componentClass, size);
    //         }
    //         Class<?> rawType = TypeKit.getRawClass(type);
    //         if (rawType == null) {
    //             return null;
    //         }
    //         IntFunction<Object> rawFunc = NEW_INSTANCE_MAP.get(rawType);
    //         if (rawFunc != null) {
    //             return rawFunc.apply(size);
    //         }
    //         return null;
    //     }
    // }

    //private static final class
}
