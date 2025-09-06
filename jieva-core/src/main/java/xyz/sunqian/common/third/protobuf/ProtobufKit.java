package xyz.sunqian.common.third.protobuf;

import xyz.sunqian.common.runtime.reflect.ClassKit;

/**
 * Utilities for <a href="https://github.com/protocolbuffers/protobuf">Protocol Buffers</a>. To use this class, the
 * protobuf package {@code com.google.protobuf} must in the runtime environment.
 *
 * @author sunqian
 */
public class ProtobufKit {

    /**
     * Returns whether the current runtime environment has protobuf package.
     *
     * @return whether the current runtime environment has protobuf package
     */
    public static boolean supportsProtobuf() {
        return ClassKit.classExists("com.google.protobuf.Message");
    }
}
