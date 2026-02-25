package space.sunqian.fs.third.protobuf;

import com.google.protobuf.Message;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.exception.UnsupportedEnvException;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.builder.BuilderOperator;
import space.sunqian.fs.object.builder.BuilderOperatorProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * {@link BuilderOperatorProvider.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>, can be quickly used through similar
 * codes:
 * <pre>{@code
 * BuilderOperatorProvider provider = ...;
 * BuilderOperatorProvider protoProvider = provider
 *     .withFirstHandler(ProtobufBuilderHandler.getInstance());
 * }</pre>
 * To use this class, the protobuf package {@code com.google.protobuf} must in the runtime environment. And in this
 * environment, the {@link BuilderOperatorProvider#defaultProvider()} will automatically load this handler.
 *
 * @author sunqian
 */
public class ProtobufBuilderHandler implements BuilderOperatorProvider.Handler {

    private static final @Nonnull ProtobufBuilderHandler INST = new ProtobufBuilderHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull ProtobufBuilderHandler getInstance() {
        return INST;
    }

    /**
     * Constructs a new handler instance. This constructor will check whether the protobuf package is available in the
     * current environment.
     *
     * @throws UnsupportedEnvException if the protobuf package is not available in the current environment.
     */
    public ProtobufBuilderHandler() throws UnsupportedEnvException {
        Fs.uncheck(() -> Class.forName("com.google.protobuf.Message"), UnsupportedEnvException::new);
    }

    @Override
    public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
        // Check whether it is a protobuf object
        if (!(target instanceof Class<?>)) {
            return null;
        }
        Class<?> rawTarget = (Class<?>) target;
        boolean isProtobuf = false;
        boolean isBuilder = false;
        if (Message.class.isAssignableFrom(rawTarget)) {
            isProtobuf = true;
        }
        if (Message.Builder.class.isAssignableFrom(rawTarget)) {
            isProtobuf = true;
            isBuilder = true;
        }
        if (!isProtobuf) {
            return null;
        }
        if (isBuilder) {
            Method buildMethod = rawTarget.getMethod("build");
            Class<?> messageType = buildMethod.getReturnType();
            Method builderMethod = messageType.getMethod("newBuilder");
            return new ImplForBuilder(builderMethod, target);
        } else {
            Method builderMethod = rawTarget.getMethod("newBuilder");
            Class<?> builderType = builderMethod.getReturnType();
            Method buildMethod = builderType.getMethod("build");
            return new ImplForMessage(builderMethod, buildMethod, builderType);
        }
    }

    private static final class ImplForBuilder implements BuilderOperator {

        private final @Nonnull Invocable builder;
        private final @Nonnull Type builderType;

        private ImplForBuilder(@Nonnull Method builderMethod, @Nonnull Type builderType) {
            this.builder = Invocable.of(builderMethod);
            this.builderType = builderType;
        }

        @Override
        public @Nonnull Type builderType() {
            return builderType;
        }

        @Override
        public @Nonnull Type targetType() {
            return builderType;
        }

        @Override
        public @Nonnull Object createBuilder() throws ObjectException {
            return Fs.uncheck(() -> builder.invoke(null), ObjectException::new);
        }

        @Override
        public @Nonnull Object buildTarget(@Nonnull Object builder) throws ObjectException {
            return builder;
        }
    }

    private static final class ImplForMessage implements BuilderOperator {

        private final @Nonnull Invocable builder;
        private final @Nonnull Invocable build;
        private final @Nonnull Type builderType;
        private final @Nonnull Type messageType;

        private ImplForMessage(
            @Nonnull Method builderMethod, @Nonnull Method buildMethod, @Nonnull Type builderType
        ) {
            this.builder = Invocable.of(builderMethod);
            this.build = Invocable.of(buildMethod);
            this.builderType = builderType;
            this.messageType = buildMethod.getReturnType();
        }

        @Override
        public @Nonnull Type builderType() {
            return builderType;
        }

        @Override
        public @Nonnull Type targetType() {
            return messageType;
        }

        @Override
        public @Nonnull Object createBuilder() throws ObjectException {
            return Fs.uncheck(() -> builder.invoke(null), ObjectException::new);
        }

        @Override
        public @Nonnull Object buildTarget(@Nonnull Object builder) throws ObjectException {
            return Fs.uncheck(() -> build.invoke(builder), ObjectException::new);
        }
    }
}
