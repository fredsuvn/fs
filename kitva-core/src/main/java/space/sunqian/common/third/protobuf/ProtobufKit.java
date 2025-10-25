package space.sunqian.common.third.protobuf;

import space.sunqian.common.runtime.reflect.ClassKit;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>. To use this class, the
 * protobuf package {@code com.google.protobuf} must in the runtime environment.
 *
 * @author sunqian
 */
public class ProtobufKit {

    /**
     * Returns whether the {@code Protocol Buffers} is available on the current runtime environment.
     *
     * @return whether the {@code Protocol Buffers} is available on the current runtime environment
     */
    public static boolean isAvailable() {
        return ClassKit.classExists("com.google.protobuf.Message");
    }
}
