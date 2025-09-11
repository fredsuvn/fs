// package xyz.sunqian.common.third.protobuf;
//
// import com.google.protobuf.ByteString;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.base.lang.Flag;
// import xyz.sunqian.common.base.Jie;
// import xyz.sunqian.common.base.chars.CharsKit;
// import xyz.sunqian.common.object.convert.ObjectConverter;
// import xyz.sunqian.common.object.convert.ConversionOptions;
// import xyz.sunqian.common.object.data.ObjectProperty;
//
// import java.lang.reflect.Type;
// import java.nio.ByteBuffer;
// import java.nio.charset.Charset;
// import java.util.Objects;
//
// /**
//  * Mapper handler implementation for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
//  * <p>
//  * If source object is {@code null}, return {@link Flag#CONTINUE}. Otherwise, it supports mapping between the types:
//  * <ul>
//  *     <li>{@code byte[]};</li>
//  *     <li>{@link ByteBuffer};</li>
//  *     <li>{@code char[]};</li>
//  *     <li>{@link String};</li>
//  *     <li>{@link ByteString};</li>
//  * </ul>
//  * If source type not in those types, return {@link Flag#CONTINUE}.
//  *
//  * @author fredsuvn
//  */
// public class ProtobufMapperHandler implements ObjectConverter.Handler {
//
//     @Override
//     public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, ObjectConverter objectConverter, ConversionOptions options) {
//         return mapProperty(source, sourceType, targetType, null, objectConverter, options);
//     }
//
//     @Override
//     public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, @Nullable ObjectProperty targetProperty, ObjectConverter objectConverter, ConversionOptions options) {
//         if (source == null) {
//             return Flag.CONTINUE;
//         }
//         if (Objects.equals(targetType, ByteString.class)) {
//             if (source instanceof ByteString) {
//                 return source;
//             }
//             if (source instanceof byte[]) {
//                 return ByteString.copyFrom((byte[]) source);
//             }
//             if (source instanceof ByteBuffer) {
//                 return ByteString.copyFrom(((ByteBuffer) source).slice());
//             }
//             if (source instanceof String) {
//                 return ByteString.copyFrom((String) source, getCharset(targetProperty, options));
//             }
//             if (source instanceof char[]) {
//                 return ByteString.copyFrom(new String((char[]) source), getCharset(targetProperty, options));
//             }
//         }
//         if (source instanceof ByteString) {
//             ByteString src = (ByteString) source;
//             if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
//                 return src.toString(getCharset(targetProperty, options));
//             } else if (Objects.equals(targetType, char[].class)) {
//                 return src.toString(getCharset(targetProperty, options)).toCharArray();
//             } else if (Objects.equals(targetType, byte[].class)) {
//                 return src.toByteArray();
//             } else if (Objects.equals(targetType, ByteBuffer.class)) {
//                 return ByteBuffer.wrap(src.toByteArray());
//             }
//         }
//         return Flag.CONTINUE;
//     }
//
//     private Charset getCharset(@Nullable ObjectProperty targetProperty, ConversionOptions options) {
//         Charset charset = options.getCharset(targetProperty);
//         return Jie.nonnull(charset, CharsKit.defaultCharset());
//     }
// }
