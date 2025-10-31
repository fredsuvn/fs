package tests.java17;

import org.junit.jupiter.api.Test;
import space.sunqian.common.testjvm.XxxImplByJdk17;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class J17Test {

    @Test
    public void testJ17() {
        XxxImplByJdk17.showVersion();
    }
}
