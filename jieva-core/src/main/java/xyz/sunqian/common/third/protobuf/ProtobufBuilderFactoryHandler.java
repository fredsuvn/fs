// package xyz.sunqian.common.third.protobuf;
//
// import com.google.protobuf.Message;
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.object.data.ObjectBuilderProvider;
// import xyz.sunqian.common.runtime.invoke.Invocable;
//
// import java.lang.reflect.Method;
//
// /**
//  * {@link ObjectBuilderProvider.Handler} implementation for
//  * <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>.
//  * To use this class, the protobuf package {@code com.google.protobuf} must in the runtime environment.
//  *
//  * @author sunqian
//  */
// public class ProtobufBuilderFactoryHandler implements ObjectBuilderProvider.Handler {
//
//     @Override
//     public @Nullable Invocable newConstructor(@Nonnull Class<?> target) throws Exception {
//         // Check whether it is a protobuf object
//         boolean isProtobuf = false;
//         boolean isBuilder = false;
//         if (Message.class.isAssignableFrom(target)) {
//             isProtobuf = true;
//         }
//         if (Message.Builder.class.isAssignableFrom(target)) {
//             isProtobuf = true;
//             isBuilder = true;
//         }
//         if (!isProtobuf) {
//             return null;
//         }
//         return getProtobufBuilder(target, isBuilder);
//     }
//
//     private @Nonnull Invocable getProtobufBuilder(@Nonnull Class<?> type, boolean isBuilder) throws Exception {
//         if (isBuilder) {
//             Method build = type.getMethod("build");
//             Class<?> srcType = build.getReturnType();
//             Method newBuilder = srcType.getMethod("newBuilder");
//             return Invocable.of(newBuilder);
//         } else {
//             Method newBuilder = type.getMethod("newBuilder");
//             return Invocable.of(newBuilder);
//         }
//     }
//
//     @Override
//     public @Nullable Object build(@Nonnull Class<?> target, @Nonnull Object builder) throws Exception {
//         // Check whether it is a protobuf object
//         Class<?> builderType = builder.getClass();
//         boolean isProtobuf = false;
//         boolean isBuilder = false;
//         if (Message.class.isAssignableFrom(builderType)) {
//             isProtobuf = true;
//         }
//         if (Message.Builder.class.isAssignableFrom(builderType)) {
//             isProtobuf = true;
//             isBuilder = true;
//         }
//         if (!isProtobuf) {
//             return null;
//         }
//         return build(builder, isBuilder);
//     }
//
//     private @Nonnull Object build(@Nonnull Object builder, boolean isBuilder) throws Exception {
//         if (isBuilder) {
//             return builder;
//         }
//         Method build = builder.getClass().getMethod("build");
//         return build.invoke(builder);
//     }
// }
