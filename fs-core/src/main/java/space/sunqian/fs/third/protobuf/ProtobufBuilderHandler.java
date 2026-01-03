package space.sunqian.fs.third.protobuf;

import com.google.protobuf.Message;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.exception.UnsupportedEnvException;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.data.DataObjectException;
import space.sunqian.fs.object.data.ObjectBuilder;
import space.sunqian.fs.object.data.ObjectBuilderProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * {@link ObjectBuilderProvider.Handler} implementation for
 * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>, can be quickly used through similar
 * codes:
 * <pre>{@code
 * ObjectBuilderProvider provider = ObjectBuilderProvider
 *     .defaultProvider()
 *     .withFirstHandler(new ProtobufBuilderHandler());
 * }</pre>
 * To use this class, the protobuf package {@code com.google.protobuf} must in the runtime environment.
 *
 * @author sunqian
 */
public class ProtobufBuilderHandler implements ObjectBuilderProvider.Handler {

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
    public @Nullable ObjectBuilder newBuilder(@Nonnull Type target) throws Exception {
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
            return new BuilderToBuilder(builderMethod, rawTarget);
        } else {
            Method builderMethod = rawTarget.getMethod("newBuilder");
            Class<?> builderType = builderMethod.getReturnType();
            Method buildMethod = builderType.getMethod("build");
            return new BuilderToObject(builderMethod, buildMethod, builderType);
        }
    }

    private static final class BuilderToBuilder implements ObjectBuilder {

        private final @Nonnull Invocable builder;
        private final @Nonnull Type builderType;

        private BuilderToBuilder(@Nonnull Method builderMethod, @Nonnull Type builderType) {
            this.builder = Invocable.of(builderMethod);
            this.builderType = builderType;
        }

        @Override
        public @Nonnull Object newBuilder() throws DataObjectException {
            return Fs.uncheck(() -> builder.invoke(null), DataObjectException::new);
        }

        @Override
        public @Nonnull Type builderType() {
            return builderType;
        }

        @Override
        public @Nonnull Object build(@Nonnull Object builder) throws DataObjectException {
            return builder;
        }
    }

    private static final class BuilderToObject implements ObjectBuilder {

        private final @Nonnull Invocable builder;
        private final @Nonnull Invocable build;
        private final @Nonnull Type builderType;

        private BuilderToObject(
            @Nonnull Method builderMethod, @Nonnull Method buildMethod, @Nonnull Type builderType
        ) {
            this.builder = Invocable.of(builderMethod);
            this.build = Invocable.of(buildMethod);
            this.builderType = builderType;
        }

        @Override
        public @Nonnull Object newBuilder() throws DataObjectException {
            return Fs.uncheck(() -> builder.invoke(null), DataObjectException::new);
        }

        @Override
        public @Nonnull Type builderType() {
            return builderType;
        }

        @Override
        public @Nonnull Object build(@Nonnull Object builder) throws DataObjectException {
            return Fs.uncheck(() -> build.invoke(builder), DataObjectException::new);
        }
    }
}
