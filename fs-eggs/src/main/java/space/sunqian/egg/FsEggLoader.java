package space.sunqian.egg;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.codec.Base64Kit;
import space.sunqian.fs.reflect.ClassKit;

public class FsEggLoader {

    public static @Nonnull FsEgg lay(@Nonnull String eggCode) {
        String className = Base64Kit.decoder().decodeToString(eggCode);
        FsEgg egg = ClassKit.newInstance(className);
        if (egg == null) {
            throw new EggHatchingException("Wrong egg code: " + eggCode);
        }
        return egg;
    }
}
