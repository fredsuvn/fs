package tests.egg;

import org.junit.jupiter.api.Test;
import space.sunqian.common.codec.Base64Kit;
import space.sunqian.egg.FsEgg;
import space.sunqian.egg.FsEggImpl;
import space.sunqian.egg.FsEggLoader;

public class EggTest {

    @Test
    public void testEgg() throws Exception {
        String eggClassName = FsEggImpl.class.getName();
        String eggCode = Base64Kit.encoder().encodeToString(eggClassName.getBytes());
        FsEgg egg = FsEggLoader.lay(eggCode);
        egg.hatch();
    }
}
