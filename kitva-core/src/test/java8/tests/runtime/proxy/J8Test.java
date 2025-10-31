package tests.runtime.proxy;

import org.junit.jupiter.api.Test;
import space.sunqian.common.testjvm.XxxImplByJdk17;

public class J8Test {

    @Test
    public void testJ8() {
        XxxImplByJdk17.showVersion();
    }
}
