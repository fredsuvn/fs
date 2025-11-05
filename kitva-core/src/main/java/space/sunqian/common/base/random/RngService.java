package space.sunqian.common.base.random;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.KitVa;

import java.util.Random;

interface RngService {

    @Nonnull
    RngService INST = KitVa.loadImplByJvm(RngService.class, 17);

    @Nonnull
    Rng random(@Nonnull Random random);

    @Nonnull
    Rng threadLocalRandom();
}
