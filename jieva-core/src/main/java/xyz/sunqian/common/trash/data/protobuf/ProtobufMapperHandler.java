package xyz.sunqian.common.trash.data.protobuf;

import com.google.protobuf.ByteString;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.mapping.Mapper;
import xyz.sunqian.common.mapping.MappingOptions;
import xyz.sunqian.common.objects.data.DataProperty;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Mapper handler implementation for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * <p>
 * If source object is {@code null}, return {@link Flag#CONTINUE}. Otherwise, it supports mapping between the types:
 * <ul>
 *     <li>{@code byte[]};</li>
 *     <li>{@link ByteBuffer};</li>
 *     <li>{@code char[]};</li>
 *     <li>{@link String};</li>
 *     <li>{@link ByteString};</li>
 * </ul>
 * If source type not in those types, return {@link Flag#CONTINUE}.
 *
 * @author fredsuvn
 */
public class ProtobufMapperHandler implements Mapper.Handler {

    @Override
    public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        return mapProperty(source, sourceType, targetType, null, mapper, options);
    }

    @Override
    public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, @Nullable DataProperty targetProperty, Mapper mapper, MappingOptions options) {
        if (source == null) {
            return Flag.CONTINUE;
        }
        if (Objects.equals(targetType, ByteString.class)) {
            if (source instanceof ByteString) {
                return source;
            }
            if (source instanceof byte[]) {
                return ByteString.copyFrom((byte[]) source);
            }
            if (source instanceof ByteBuffer) {
                return ByteString.copyFrom(((ByteBuffer) source).slice());
            }
            if (source instanceof String) {
                return ByteString.copyFrom((String) source, getCharset(targetProperty, options));
            }
            if (source instanceof char[]) {
                return ByteString.copyFrom(new String((char[]) source), getCharset(targetProperty, options));
            }
        }
        if (source instanceof ByteString) {
            ByteString src = (ByteString) source;
            if (Objects.equals(targetType, String.class) || Objects.equals(targetType, CharSequence.class)) {
                return src.toString(getCharset(targetProperty, options));
            } else if (Objects.equals(targetType, char[].class)) {
                return src.toString(getCharset(targetProperty, options)).toCharArray();
            } else if (Objects.equals(targetType, byte[].class)) {
                return src.toByteArray();
            } else if (Objects.equals(targetType, ByteBuffer.class)) {
                return ByteBuffer.wrap(src.toByteArray());
            }
        }
        return Flag.CONTINUE;
    }

    private Charset getCharset(@Nullable DataProperty targetProperty, MappingOptions options) {
        Charset charset = options.getCharset(targetProperty);
        return Jie.nonnull(charset, CharsKit.defaultCharset());
    }
}
