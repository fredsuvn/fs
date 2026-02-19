package space.sunqian.fs.net.http;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.lang.FsLoader;

import java.net.Proxy;

interface HttpService {

    @Nonnull
    HttpService INST = FsLoader.loadImplByJvm(HttpService.class, 11);

    @Nonnull
    HttpCaller newCaller(
        int bufSize,
        @Nonnull Proxy proxy
    ) throws HttpNetException;
}
