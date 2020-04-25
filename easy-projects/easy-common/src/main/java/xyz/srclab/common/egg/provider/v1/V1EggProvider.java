package xyz.srclab.common.egg.provider.v1;

import xyz.srclab.common.egg.Egg;
import xyz.srclab.common.egg.EggProvider;

public class V1EggProvider implements EggProvider {

    @Override
    public Egg getEgg() {
        return V1Egg.INSTANCE;
    }
}
