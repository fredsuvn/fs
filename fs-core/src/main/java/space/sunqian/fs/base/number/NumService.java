package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.FsLoader;

public interface NumService {

    @Nonnull
    NumService INST = FsLoader.loadImplByJvm(NumService.class, 9);

    @Nonnull
    Number toNumber(@Nonnull CharSequence cs) throws NumException;

    @Nonnull
    Number toNumber(@Nonnull CharSequence cs, int start, int end) throws NumException;
}
