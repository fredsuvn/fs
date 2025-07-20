package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;

import java.nio.ByteBuffer;

/**
 * Handler to process received data.
 *
 * @author sunqian
 */
public interface NetHandler {

    void handle(@Nonnull NetEndpoint endpoint, @Nonnull ByteBuffer data) throws NetException;
}
