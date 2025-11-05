package space.sunqian.common.net.http;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.KitVa;

interface HttpClientEngineService {

    @Nonnull
    HttpClientEngineService INST = KitVa.loadImplByJvm(HttpClientEngineService.class, 11);

    @Nonnull
    HttpClientEngine newEngine(int bufSize) throws IllegalArgumentException;
}
