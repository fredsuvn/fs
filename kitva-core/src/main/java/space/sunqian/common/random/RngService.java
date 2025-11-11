package space.sunqian.common.random;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.KitLoader;

import java.util.Random;

interface RngService {

    @Nonnull
    RngService INST = KitLoader.loadImplByJvm(RngService.class, 17);

    @Nonnull
    Rng random(@Nonnull Random random);

    @Nonnull
    Rng threadLocalRandom();
}
