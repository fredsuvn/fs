package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.FsLoader;

public interface NumberService {

    @Nonnull
    NumberService INST = FsLoader.loadImplByJvm(NumberService.class, 9);

    @Nonnull
    Number toNumber(@Nonnull CharSequence cs) throws NumberException;

    @Nonnull
    Number toNumber(@Nonnull CharSequence cs, int start, int end) throws NumberException;
}
