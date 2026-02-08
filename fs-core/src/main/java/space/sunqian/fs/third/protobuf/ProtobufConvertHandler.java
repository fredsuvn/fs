package space.sunqian.fs.third.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.ProtocolStringList;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.exception.UnsupportedEnvException;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * {@link ObjectConverter.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>, can be quickly used through similar
 * codes:
 * <pre>{@code
 * ObjectConverter converter = ...;
 * ObjectConverter protoConverter = converter
 *     .withFirstHandler(ProtobufConvertHandler.getInstance());
 * }</pre>
 * <p>
 * This handler provides support for {@link ProtocolStringList} and {@link ByteString}.
 * <p>
 * To use this class, the protobuf package {@code com.google.protobuf} must in the runtime environment. And in this
 * environment, the {@link ObjectConverter#defaultConverter()} will automatically load this handler.
 *
 * @author sunqian
 */
public class ProtobufConvertHandler implements ObjectConverter.Handler {

    private static final @Nonnull ProtobufConvertHandler INST = new ProtobufConvertHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull ProtobufConvertHandler getInstance() {
        return INST;
    }

    /**
     * Constructs a new handler instance. This constructor will check whether the protobuf package is available in the
     * current environment.
     *
     * @throws UnsupportedEnvException if the protobuf package is not available in the current environment.
     */
    public ProtobufConvertHandler() throws UnsupportedEnvException {
        Fs.uncheck(() -> Class.forName("com.google.protobuf.Message"), UnsupportedEnvException::new);
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
                List<String> list = Fs.as(ret);
                return new LazyStringArrayList(list);
            }
        }
        return ObjectConverter.Status.HANDLER_CONTINUE;
    }
}
