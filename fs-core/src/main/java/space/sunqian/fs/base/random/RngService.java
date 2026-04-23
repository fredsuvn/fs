package space.sunqian.fs.base.random;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.FsLoader;

import java.util.Random;

interface RngService {

    @Nonnull
    RngService INST = FsLoader.loadImplByJvm(RngService.class, 17);

    @Nonnull
    Rng random(@Nonnull Random random);

    @Nonnull
    Rng threadLocalRandom();
}
