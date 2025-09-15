package xyz.sunqian.common.third.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.ProtocolStringList;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.convert.ObjectConverter;
import xyz.sunqian.common.object.data.ObjectBuilderProvider;

import java.lang.reflect.Type;
import java.util.List;

/**
 * {@link ObjectBuilderProvider.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>, can be quickly used through similar
 * codes:
 * <pre>{@code
 * ObjectConverter converter = ObjectConverter
 *     .defaultConverter()
 *     .withFirstHandler(new ProtobufConvertHandler());
 * }</pre>
 * <p>
 * This handler provides support for {@link ProtocolStringList} and {@link ByteString}.
 * <p>
 * To use this class, the protobuf package {@code com.google.protobuf} must in the runtime environment.
 *
 * @author sunqian
 */
public class ProtobufConvertHandler implements ObjectConverter.Handler {

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
        if (src instanceof ByteString) {
            ByteString bs = (ByteString) src;
            return converter.asHandler().convert(bs.asReadOnlyByteBuffer(), srcType, target, converter, options);
        } else if (target.equals(ByteString.class)) {
            Object ret = converter.asHandler().convert(src, srcType, byte[].class, converter, options);
            if (ret instanceof byte[]) {
                return ByteString.copyFrom((byte[]) ret);
            }
        } else if (target.equals(ProtocolStringList.class)) {
            Object ret = converter.asHandler().convert(
                src, srcType, ProtobufSchemaHandler.StringListTypeRef.SINGLETON.type(), converter, options
            );
            if (ret instanceof List) {
                List<String> list = Jie.as(ret);
                return new LazyStringArrayList(list);
            }
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }
}
