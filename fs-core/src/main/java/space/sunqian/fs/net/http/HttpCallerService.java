package space.sunqian.fs.net.http;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.FsLoader;

import java.net.Proxy;

interface HttpCallerService {

    @Nonnull
    HttpCallerService INST = FsLoader.loadImplByJvm(HttpCallerService.class, 11);

    @Nonnull
    HttpCaller newCaller(
        int bufSize,
        @Nonnull Proxy proxy
    ) throws HttpNetException;
}
