// package space.sunqian.fs.object.meta;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.fs.reflect.TypeKit;
//
// import java.lang.reflect.ParameterizedType;
// import java.lang.reflect.Type;
// import java.util.Map;
//
// /**
//  * Represents the {@link Type} for {@link Map}.
//  *
//  * @author sunqian
//  */
// public interface MapType extends Type {
//
//     /**
//      * Returns a new {@link MapType} instance for the specified {@code mapType}, {@code keyType}, and {@code valueType}.
//      * The key type and value type of the {@code mapType} will be discarded. The raw type of the {@code mapType} will be
//      * used as the {@code rawType} of the returned type. For examples:
//      * <table>
//      *     <tr>
//      *         <th>Map Type</th>
//      *         <th>Key Type</th>
//      *         <th>Value Type</th>
//      *         <th>Returned Type</th>
//      *     </tr>
//      *     <tr>
//      *         <td>{@code Map}</td>
//      *         <td>{@code String}</td>
//      *         <td>{@code Integer}</td>
//      *         <td>{@code Map<String, Integer>}</td>
//      *     </tr>
//      *     <tr>
//      *         <td>{@code HashMap<Long, Integer>}</td>
//      *         <td>{@code Long}</td>
//      *         <td>{@code List<String>}</td>
//      *         <td>{@code HashMap<Long, List<String>>}</td>
//      *     </tr>
//      * </table>
//      *
//      * @param mapType   the map type
//      * @param keyType   the key type
//      * @param valueType the value type
//      * @return a new {@link MapType} instance
//      * @throws IllegalArgumentException if the given map type is not a {@link Class} or a {@link ParameterizedType}
//      */
//     static @Nonnull MapType of(
//         @Nonnull Type mapType,
//         @Nonnull Type keyType,
//         @Nonnull Type valueType
//     ) throws IllegalArgumentException {
//         Class<?> rawMapType = TypeKit.getRawClass(mapType);
//         if (rawMapType == null) {
//             throw new IllegalArgumentException("Given map type must be a Class or a ParameterizedType.");
//         }
//         return new MapType() {
//             @Override
//             public @Nonnull Type mapType() {
//                 return mapType;
//             }
//
//             @Override
//             public @Nonnull Class<?> rawType() {
//                 return rawMapType;
//             }
//
//             @Override
//             public @Nonnull Type keyType() {
//                 return keyType;
//             }
//
//             @Override
//             public @Nonnull Type valueType() {
//                 return valueType;
//             }
//
//             @Override
//             public int hashCode() {
//                 return 31 * mapType().hashCode() + 31 * keyType().hashCode() + 31 * valueType().hashCode();
//             }
//
//             @Override
//             public boolean equals(Object obj) {
//                 if (this == obj) {
//                     return true;
//                 }
//                 if (!(obj instanceof MapType)) {
//                     return false;
//                 }
//                 @SuppressWarnings("PatternVariableCanBeUsed")
//                 MapType o = (MapType) obj;
//                 return mapType().equals(o.mapType()) &&
//                     keyType().equals(o.keyType()) &&
//                     valueType().equals(o.valueType());
//             }
//
//             @Override
//             public String toString() {
//                 return getTypeName();
//             }
//         };
//     }
//
//     /**
//      * Returns the type of the map itself.
//      *
//      * @return the type of the map itself
//      */
//     @Nonnull
//     Type mapType();
//
//     /**
//      * Returns the raw type of the map itself.
//      *
//      * @return the raw type of the map itself
//      */
//     @Nonnull
//     Class<?> rawType();
//
//     /**
//      * Returns the type of the key in the map.
//      *
//      * @return the type of the key in the map
//      */
//     @Nonnull
//     Type keyType();
//
//     /**
//      * Returns the type of the value in the map.
//      *
//      * @return the type of the value in the map
//      */
//     @Nonnull
//     Type valueType();
//
//     @Override
//     default String getTypeName() {
//         return rawType().getTypeName() + "<" + keyType().getTypeName() + ", " + valueType().getTypeName() + ">";
//     }
// }
