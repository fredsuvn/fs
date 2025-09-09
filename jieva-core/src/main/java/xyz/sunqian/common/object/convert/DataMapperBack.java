// package xyz.sunqian.common.object.convert;
//
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.base.option.Option;
// import xyz.sunqian.common.object.data.DataProperty;
// import xyz.sunqian.common.object.data.DataSchema;
// import xyz.sunqian.common.runtime.reflect.TypeRef;
//
// import java.lang.reflect.ParameterizedType;
// import java.lang.reflect.Type;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// final class DataMapperBack {
//
//     private final @Nonnull Map<Type, DataSchema> schemaCache = new ConcurrentHashMap<>();
//
//     static void copyProperties(
//         @Nonnull Object src,
//         @Nonnull Type srcType,
//         @Nonnull Object dst,
//         @Nonnull Type dstType,
//         @Nonnull Option<?, ?> @Nonnull ... options
//     ) throws ObjectConversionException {
//         // try {
//         //     if (source instanceof Map) {
//         //         if (dest instanceof Map) {
//         //             mapToMap(source, sourceType, dest, destType, options);
//         //         } else {
//         //             mapToBean(source, sourceType, dest, destType, options);
//         //         }
//         //     } else {
//         //         if (dest instanceof Map) {
//         //             beanToMap(source, sourceType, dest, destType, options);
//         //         } else {
//         //             beanToBean(source, sourceType, dest, destType, options);
//         //         }
//         //     }
//         // } catch (Exception e) {
//         //     throw new ObjectConversionException(e);
//         // }
//         // return dest;
//     }
//
//     void mapToMap(
//         @Nonnull Map<Object, Object> src,
//         @Nonnull MapType srcType,
//         @Nonnull Map<Object, Object> dst,
//         @Nonnull MapType dstType,
//         @Nonnull ObjectConverter converter,
//         @Nullable DataMapper.PropertyMapper propertyMapper,
//         @Nullable DataMapper.ExceptionHandler exceptionHandler,
//         @Nonnull Option<?, ?> @Nonnull ... options
//     ) throws Exception {
//         src.forEach((srcKey, srcValue) -> {
//             try {
//                 Object dstPropertyName;
//                 Object dstPropertyValue;
//                 if (propertyMapper != null) {
//                     Map.Entry<Object, Object> entry = propertyMapper.map(
//                         srcKey, src, null, dst, null, converter, options
//                     );
//                     if (entry == null) {
//                         return;
//                     }
//                     dstPropertyName = entry.getKey();
//                     dstPropertyValue = entry.getValue();
//                 } else {
//                     dstPropertyName = converter.convert(srcKey, srcType.keyType, dstType.keyType, options);
//                     dstPropertyValue = converter.convert(srcValue, srcType.valueType, dstType.valueType, options);
//                 }
//                 dst.put(dstPropertyName, dstPropertyValue);
//             } catch (Exception e) {
//                 if (exceptionHandler != null) {
//                     try {
//                         exceptionHandler.handle(e, srcKey, src, null, dst, null, converter, options);
//                     } catch (Exception ex) {
//                         throw new ObjectConversionException(ex);
//                     }
//                 }
//             }
//         });
//     }
//
//     void mapToObject(
//         @Nonnull Map<Object, Object> src,
//         @Nonnull MapType srcType,
//         @Nonnull Object dst,
//         @Nonnull DataSchema dstSchema,
//         @Nonnull ObjectConverter converter,
//         @Nullable DataMapper.PropertyMapper propertyMapper,
//         @Nullable DataMapper.ExceptionHandler exceptionHandler,
//         @Nonnull Option<?, ?> @Nonnull ... options
//     ) throws Exception {
//         src.forEach((srcKey, srcValue) -> {
//             try {
//                 Object dstPropertyName;
//                 Object dstPropertyValue;
//                 DataProperty dstProperty;
//                 if (propertyMapper != null) {
//                     Map.Entry<Object, Object> entry = propertyMapper.map(
//                         srcKey, src, null, dst, dstSchema, converter, options
//                     );
//                     if (entry == null) {
//                         return;
//                     }
//                     dstPropertyName = entry.getKey();
//                     dstPropertyValue = entry.getValue();
//                     dstProperty = dstSchema.getProperty((String) dstPropertyName);
//                     if (dstProperty == null || !dstProperty.isWritable()) {
//                         return;
//                     }
//                 } else {
//                     dstPropertyName = converter.convert(srcKey, srcType.keyType, String.class, options);
//                     dstProperty = dstSchema.getProperty((String) dstPropertyName);
//                     if (dstProperty == null || !dstProperty.isWritable()) {
//                         return;
//                     }
//                     dstPropertyValue = converter.convert(srcValue, srcType.valueType, dstProperty.type(), options);
//                 }
//                 dstProperty.setValue(dst, dstPropertyValue);
//             } catch (Exception e) {
//                 if (exceptionHandler != null) {
//                     try {
//                         exceptionHandler.handle(e, srcKey, src, null, dst, dstSchema, converter, options);
//                     } catch (Exception ex) {
//                         throw new ObjectConversionException(ex);
//                     }
//                 }
//             }
//         });
//     }
//
//
//
//     void objectToMap(
//         @Nonnull Object src,
//         @Nonnull DataSchema srcSchema,
//         @Nonnull Map<Object, Object> dst,
//         @Nonnull MapType dstType,
//         @Nonnull ObjectConverter converter,
//         @Nullable DataMapper.PropertyMapper propertyMapper,
//         @Nullable DataMapper.ExceptionHandler exceptionHandler,
//         @Nonnull Option<?, ?> @Nonnull ... options
//     ) throws Exception {
//         srcSchema.properties().forEach((srcPropertyName, srcProperty) -> {
//             try {
//                 Object dstPropertyName;
//                 Object dstPropertyValue;
//                 DataProperty dstProperty;
//                 if (propertyMapper != null) {
//                     Map.Entry<Object, Object> entry = propertyMapper.map(
//                         srcPropertyName, src, srcSchema, dst, null, converter, options
//                     );
//                     if (entry == null) {
//                         return;
//                     }
//                     dstPropertyName = entry.getKey();
//                     dstPropertyValue = entry.getValue();
//                     dstProperty = dstSchema.getProperty((String) dstPropertyName);
//                     if (dstProperty == null || !dstProperty.isWritable()) {
//                         return;
//                     }
//                 } else {
//                     dstPropertyName = converter.convert(srcKey, srcType.keyType, String.class, options);
//                     dstProperty = dstSchema.getProperty((String) dstPropertyName);
//                     if (dstProperty == null || !dstProperty.isWritable()) {
//                         return;
//                     }
//                     dstPropertyValue = converter.convert(srcValue, srcType.valueType, dstProperty.type(), options);
//                 }
//                 dstProperty.setValue(dst, dstPropertyValue);
//             } catch (Exception e) {
//                 if (exceptionHandler != null) {
//                     try {
//                         exceptionHandler.handle(e, srcPropertyName, src, null, dst, null, converter, options);
//                     } catch (Exception ex) {
//                         throw new ObjectConversionException(ex);
//                     }
//                 }
//             }
//         });
//     }
//
//     private @Nonnull MapType getMapType(Type mapType) {
//         ParameterizedType pType = (ParameterizedType) mapType;
//         return new MapType(pType.getActualTypeArguments()[0], pType.getActualTypeArguments()[1]);
//     }
//
//     private static final class MapType {
//
//         private final @Nonnull Type keyType;
//         private final @Nonnull Type valueType;
//
//         private MapType(@Nonnull Type keyType, @Nonnull Type valueType) {
//             this.keyType = keyType;
//             this.valueType = valueType;
//         }
//     }
//
//     private static final class RawMapTypeRef extends TypeRef<Map<Object, Object>> {
//         private static final RawMapTypeRef SINGLETON = new RawMapTypeRef();
//     }
// }
