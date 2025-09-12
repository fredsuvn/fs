// package xyz.sunqian.common.object.convert.handlers;
//
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.base.option.Option;
// import xyz.sunqian.common.object.convert.ObjectConverter;
// import xyz.sunqian.common.runtime.reflect.TypeKit;
//
// import java.lang.reflect.GenericArrayType;
// import java.lang.reflect.Type;
// import java.util.AbstractList;
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.LinkedHashSet;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.TreeSet;
// import java.util.concurrent.ConcurrentSkipListSet;
// import java.util.concurrent.CopyOnWriteArrayList;
// import java.util.function.IntFunction;
//
// /**
//  * The implementation of {@link ObjectConverter.Handler} used to generate collection and array for target types.
//  * <p>
//  * If the target type is an array type, this handler will create a new array with the component type parsed from the
//  * target array type. If the target type is a collection type, this handler will attempt to generate the target
//  * collection. If the target type is unsupported, this handler return {@link ObjectConverter.Status#HANDLER_CONTINUE}.
//  * <p>
//  * This handler uses a {@link BuilderFactory} to retrieve a builder for creating new collection instance, using
//  * {@link #defaultBuilderFactory()} can get the default builder factory.
//  *
//  * @author sunqian
//  * @see BuilderFactory
//  */
// public class CollectionConversionHandler implements ObjectConverter.Handler {
//
//     private static final BuilderFactory DEFAULT_BUILDER_FACTORY = new DefaultBuilderFactory();
//
//     /**
//      * Returns the default builder factory.
//      * <p>
//      * The supported collection types are:
//      * <ul>
//      *     <li>{@link Iterable}</li>
//      *     <li>{@link Collection}</li>
//      *     <li>{@link List}</li>
//      *     <li>{@link AbstractList}</li>
//      *     <li>{@link ArrayList}</li>
//      *     <li>{@link LinkedList}</li>
//      *     <li>{@link CopyOnWriteArrayList}</li>
//      *     <li>{@link Set}</li>
//      *     <li>{@link LinkedHashSet}</li>
//      *     <li>{@link HashSet}</li>
//      *     <li>{@link TreeSet}</li>
//      *     <li>{@link ConcurrentSkipListSet}</li>
//      * </ul>
//      *
//      * @return the default builder factory
//      */
//     public static @Nonnull BuilderFactory defaultBuilderFactory() {
//         return DEFAULT_BUILDER_FACTORY;
//     }
//
//     private final @Nonnull BuilderFactory builderFactory;
//
//     /**
//      * Constructs with the specified builder factory.
//      *
//      * @param builderFactory the specified builder factory
//      */
//     public CollectionConversionHandler(@Nonnull BuilderFactory builderFactory) {
//         this.builderFactory = builderFactory;
//     }
//
//     @Override
//     public Object convert(
//         @Nullable Object src,
//         @Nonnull Type srcType,
//         @Nonnull Type target,
//         @Nonnull ObjectConverter converter,
//         @Nonnull Option<?, ?> @Nonnull ... options
//     ) throws Exception {
//         Type targetComponentType;
//         IntFunction<Object> targetBuilder;
//         if (target instanceof Class<?>) {
//             if (!((Class<?>) target).isArray()) {
//                 return ObjectConverter.Status.HANDLER_CONTINUE;
//             }
//             Class<?> arrType = (Class<?>) target;
//             targetComponentType = arrType.getComponentType();
//         } else if (target instanceof GenericArrayType) {
//             GenericArrayType arrType = (GenericArrayType) target;
//             targetComponentType = arrType.getGenericComponentType();
//         } else {
//             targetBuilder = builderFactory.newBuilder(target);
//             if (targetBuilder == null) {
//                 return ObjectConverter.Status.HANDLER_CONTINUE;
//             }
//         }
//         return ObjectConverter.Status.HANDLER_CONTINUE;
//     }
//
//     private static final class TargetCollectionInfo {
//
//         private final @Nonnull Type collectionType;
//         private final @Nonnull Type componentType;
//
//         private TargetCollectionInfo(@Nonnull Type collectionType, @Nonnull Type componentType) {
//             this.collectionType = collectionType;
//             this.componentType = componentType;
//         }
//     }
//
//     private static final class SourceCollectionInfo {
//
//         private final @Nonnull Type componentType;
//         private final int size;
//         private final @Nonnull Iterable<?> iterable;
//
//         private SourceCollectionInfo(@Nonnull Type componentType, int size, @Nonnull Iterable<?> iterable) {
//             this.componentType = componentType;
//             this.size = size;
//             this.iterable = iterable;
//         }
//     }
//
//
//     // private final CollectionFactory generator;
//     //
//     // /**
//     //  * Constructs with {@link #DEFAULT_GENERATOR}.
//     //  */
//     // public CollectionConversionHandler() {
//     //     this(DEFAULT_GENERATOR);
//     // }
//     //
//     // /**
//     //  * Constructs with specified collection generator.
//     //  *
//     //  * @param generator specified collection generator
//     //  */
//     // public CollectionConversionHandler(CollectionFactory generator) {
//     //     this.generator = generator;
//     // }
//     //
//     // @Override
//     // public Object map(@Nullable Object source, Type sourceType, Type targetType, ObjectConverter objectConverter, ConversionOptions options) {
//     //     return mapProperty(source, sourceType, targetType, null, objectConverter, options);
//     // }
//     //
//     // @Override
//     // public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, @Nullable ObjectProperty targetProperty, ObjectConverter objectConverter, ConversionOptions options) {
//     //     if (source == null) {
//     //         return Flag.CONTINUE;
//     //     }
//     //     Type targetComponentType = getComponentType(targetType);
//     //     if (targetComponentType == null) {
//     //         return Flag.CONTINUE;
//     //     }
//     //     Type sourceComponentType = getComponentType(sourceType);
//     //     if (sourceComponentType == null) {
//     //         return Flag.CONTINUE;
//     //     }
//     //     Object targetCollection = generator.create(targetType, getSourceSize(source));
//     //     if (targetCollection == null) {
//     //         return Flag.CONTINUE;
//     //     }
//     //     int i = 0;
//     //     if (source instanceof Iterable<?>) {
//     //         for (Object sourceComponent : (Iterable<?>) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof Object[]) {
//     //         for (Object sourceComponent : (Object[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof boolean[]) {
//     //         for (Object sourceComponent : (boolean[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof byte[]) {
//     //         for (Object sourceComponent : (byte[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof short[]) {
//     //         for (Object sourceComponent : (short[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof char[]) {
//     //         for (Object sourceComponent : (char[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof int[]) {
//     //         for (Object sourceComponent : (int[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof long[]) {
//     //         for (Object sourceComponent : (long[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof float[]) {
//     //         for (Object sourceComponent : (float[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     if (source instanceof double[]) {
//     //         for (Object sourceComponent : (double[]) source) {
//     //             addComponent(targetCollection, i++, sourceComponent, sourceComponentType, targetComponentType, targetProperty, objectConverter, options);
//     //         }
//     //         return wrapResult(targetCollection);
//     //     }
//     //     return Flag.CONTINUE;
//     // }
//     //
//     // @Nullable
//     // private Type getComponentType(Type type) {
//     //     if (type instanceof Class<?>) {
//     //         if (((Class<?>) type).isArray()) {
//     //             return ((Class<?>) type).getComponentType();
//     //         }
//     //         if (Iterable.class.isAssignableFrom((Class<?>) type)) {
//     //             return Object.class;
//     //         }
//     //         return null;
//     //     }
//     //     if (type instanceof ParameterizedType) {
//     //         List<Type> sourceComponent = TypeKit.resolveActualTypeArguments(type, Iterable.class);
//     //         if (CollectKit.isEmpty(sourceComponent)) {
//     //             return null;
//     //         }
//     //         return sourceComponent.get(0);
//     //     }
//     //     if (type instanceof GenericArrayType) {
//     //         return ((GenericArrayType) type).getGenericComponentType();
//     //     }
//     //     return null;
//     // }
//     //
//     // private int getSourceSize(Object source) {
//     //     if (source instanceof Collection) {
//     //         return ((Collection<?>) source).size();
//     //     }
//     //     if (source.getClass().isArray()) {
//     //         return Array.getLength(source);
//     //     }
//     //     return -1;
//     // }
//     //
//     // private void addComponent(
//     //     Object targetCollection,
//     //     int index,
//     //     @Nullable Object sourceComponent,
//     //     Type sourceComponentType,
//     //     Type targetComponentType,
//     //     @Nullable ObjectProperty targetProperty,
//     //     ObjectConverter objectConverter,
//     //     ConversionOptions options
//     // ) {
//     //     Object targetComponent;
//     //     Object targetResult = targetProperty == null ?
//     //         objectConverter.map(sourceComponent, sourceComponentType, targetComponentType, options)
//     //         :
//     //         objectConverter.mapProperty(sourceComponent, sourceComponentType, targetComponentType, targetProperty, options);
//     //     if (targetResult == null) {
//     //         if (options.isIgnoreError()) {
//     //             targetComponent = null;
//     //         } else {
//     //             throw new ObjectConversionException(sourceComponentType, targetComponentType);
//     //         }
//     //     } else {
//     //         targetComponent = ObjectConverter.resolveResult(targetResult);
//     //     }
//     //     if (targetCollection instanceof Collection<?>) {
//     //         Collection<Object> target = Jie.as(targetCollection);
//     //         target.add(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof Object[]) {
//     //         ((Object[]) targetCollection)[index] = targetComponent;
//     //         return;
//     //     }
//     //     if (targetCollection instanceof boolean[]) {
//     //         ((boolean[]) targetCollection)[index] = targetComponent == null ? false : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof byte[]) {
//     //         ((byte[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof short[]) {
//     //         ((short[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof char[]) {
//     //         ((char[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof int[]) {
//     //         ((int[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof long[]) {
//     //         ((long[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof float[]) {
//     //         ((float[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     //     if (targetCollection instanceof double[]) {
//     //         ((double[]) targetCollection)[index] = targetComponent == null ? 0 : Jie.as(targetComponent);
//     //         return;
//     //     }
//     // }
//
//     /**
//      * This interface is used to get collection builder with the target type.
//      */
//     public interface BuilderFactory {
//
//         /**
//          * Creates and returns a function to generate target collection object, or {@code null} if the target type is
//          * unsupported.
//          * <p>
//          * The {@code int} parameter of the returned function is the expected size of the collection, if the size is
//          * unknown, sets it to {@code -1}.
//          *
//          * @param target the target type
//          * @return a function to generate target collection object, or {@code null} if the target type is unsupported
//          */
//         @Nullable
//         IntFunction<Object> newBuilder(@Nonnull Type target);
//     }
//
//     private static final class DefaultBuilderFactory implements BuilderFactory {
//
//         private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Object>> NEW_INSTANCE_MAP;
//
//         static {
//             NEW_INSTANCE_MAP = new HashMap<>();
//             NEW_INSTANCE_MAP.put(Iterable.class, (s) -> s >= 0 ? new ArrayList<>(s) : new ArrayList<>());
//             NEW_INSTANCE_MAP.put(Collection.class, (s) -> s >= 0 ? new ArrayList<>(s) : new ArrayList<>());
//             NEW_INSTANCE_MAP.put(List.class, (s) -> s >= 0 ? new ArrayList<>(s) : new ArrayList<>());
//             NEW_INSTANCE_MAP.put(AbstractList.class, (s) -> s >= 0 ? new ArrayList<>(s) : new ArrayList<>());
//             NEW_INSTANCE_MAP.put(ArrayList.class, (s) -> s >= 0 ? new ArrayList<>(s) : new ArrayList<>());
//             NEW_INSTANCE_MAP.put(LinkedList.class, (s) -> new LinkedList<>());
//             NEW_INSTANCE_MAP.put(CopyOnWriteArrayList.class, (s) -> new CopyOnWriteArrayList<>());
//             NEW_INSTANCE_MAP.put(Set.class, (s) -> s >= 0 ? new LinkedHashSet<>(s) : new LinkedHashSet<>());
//             NEW_INSTANCE_MAP.put(LinkedHashSet.class, (s) -> s >= 0 ? new LinkedHashSet<>(s) : new LinkedHashSet<>());
//             NEW_INSTANCE_MAP.put(HashSet.class, (s) -> s >= 0 ? new HashSet<>(s) : new HashSet<>());
//             NEW_INSTANCE_MAP.put(TreeSet.class, (s) -> new TreeSet<>());
//             NEW_INSTANCE_MAP.put(ConcurrentSkipListSet.class, (s) -> new ConcurrentSkipListSet<>());
//         }
//
//         @Override
//         public @Nullable IntFunction<Object> newBuilder(@Nonnull Type target) {
//             Class<?> rawType = TypeKit.getRawClass(target);
//             if (rawType == null) {
//                 return null;
//             }
//             return NEW_INSTANCE_MAP.get(rawType);
//         }
//     }
// }
