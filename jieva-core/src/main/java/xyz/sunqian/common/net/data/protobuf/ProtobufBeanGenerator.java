package xyz.sunqian.common.net.data.protobuf;

import com.google.protobuf.Message;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.mapping.MappingException;
import xyz.sunqian.common.mapping.handlers.BeanMapperHandler;
import xyz.sunqian.common.reflect.JieType;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * {@link BeanMapperHandler.BeanGenerator} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
 * <p>
 * Note this handler depends on {@code protobuf libs} in the runtime.
 *
 * @author fredsuvn
 */
public class ProtobufBeanGenerator implements BeanMapperHandler.BeanGenerator {

    @Override
    public @Nullable Object generate(Type targetType) {
        if (!(targetType instanceof Class<?>)) {
            return BeanMapperHandler.DEFAULT_GENERATOR.generate(targetType);
        }
        Class<?> rawType = JieType.getRawClass(targetType);
        // Check whether it is a protobuf object
        boolean isProtobuf = false;
        boolean isBuilder = false;
        if (Message.class.isAssignableFrom(rawType)) {
            isProtobuf = true;
        }
        if (Message.Builder.class.isAssignableFrom(rawType)) {
            isProtobuf = true;
            isBuilder = true;
        }
        if (!isProtobuf) {
            return BeanMapperHandler.DEFAULT_GENERATOR.generate(targetType);
        }
        try {
            return getProtobufBuilder(rawType, isBuilder);
        } catch (MappingException e) {
            throw e;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @Override
    public Object build(Object builder) {
        boolean isBuilder = Message.Builder.class.isAssignableFrom(builder.getClass());
        try {
            return build(builder, isBuilder);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @Nullable
    private Object getProtobufBuilder(Class<?> type, boolean isBuilder) throws Exception {
        if (isBuilder) {
            Method build = type.getMethod("build");
            Class<?> srcType = build.getReturnType();
            Method newBuilder = srcType.getMethod("newBuilder");
            return newBuilder.invoke(null);
        } else {
            Method newBuilder = type.getMethod("newBuilder");
            return newBuilder.invoke(null);
        }
    }

    private Object build(Object builder, boolean isBuilder) throws Exception {
        if (isBuilder) {
            return builder;
        }
        Method build = builder.getClass().getMethod("build");
        return build.invoke(builder);
    }
}
