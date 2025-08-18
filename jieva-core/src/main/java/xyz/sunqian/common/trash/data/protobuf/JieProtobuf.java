package xyz.sunqian.common.trash.data.protobuf;

import xyz.sunqian.common.mapping.Mapper;
import xyz.sunqian.common.mapping.handlers.AssignableMapperHandler;
import xyz.sunqian.common.mapping.handlers.BeanMapperHandler;
import xyz.sunqian.common.objects.data.DataSchemaParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>. This class depends on
 * protobuf libs in the runtime.
 */
public class JieProtobuf {

    private static final DataSchemaParser BEAN_RESOLVER;
    private static final Mapper MAPPER;

    static {
        BEAN_RESOLVER = DataSchemaParser.defaultParser().addFirstHandler(new ProtobufBeanResolveHandler());
        List<Mapper.Handler> defaultHandlers = Mapper.defaultMapper().getHandlers();
        List<Mapper.Handler> handlers = new ArrayList<>(defaultHandlers.size() + 1);
        handlers.addAll(defaultHandlers);
        for (int i = 0; i < handlers.size(); i++) {
            Mapper.Handler handler = handlers.get(i);
            if (handler instanceof BeanMapperHandler) {
                handlers.set(i, new BeanMapperHandler(new ProtobufBeanGenerator()));
            }
        }
        handlers.add(1, new ProtobufMapperHandler());
        MAPPER = Mapper.newMapper(handlers);
    }

    /**
     * Returns default {@link DataSchemaParser} which supports
     * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>. It copies handler list of
     * {@link DataSchemaParser#defaultParser()} and inserts a {@link ProtobufBeanResolveHandler} at first element.
     *
     * @return default {@link DataSchemaParser} which supports
     * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>
     */
    public static DataSchemaParser defaultBeanResolver() {
        return BEAN_RESOLVER;
    }

    /**
     * Returns default {@link Mapper} which supports
     * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>. It copies handler list of
     * {@link Mapper#defaultMapper()} and inserts a {@link ProtobufMapperHandler} after {@link AssignableMapperHandler}.
     * And it also replaces default {@link BeanMapperHandler.BeanGenerator} of {@link BeanMapperHandler} to
     * {@link ProtobufBeanGenerator}.
     *
     * @return default {@link Mapper} which supports
     * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>
     */
    public static Mapper defaultMapper() {
        return MAPPER;
    }
}
