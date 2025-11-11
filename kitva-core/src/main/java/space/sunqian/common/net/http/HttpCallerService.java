package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.KitLoader;

import java.net.Proxy;

interface HttpCallerService {

    @Nonnull
    HttpCallerService INST = KitLoader.loadImplByJvm(HttpCallerService.class, 11);

    @Nonnull
    HttpCaller newCaller(
        int bufSize,
        @Nonnull Proxy proxy
    ) throws HttpNetException;
}
