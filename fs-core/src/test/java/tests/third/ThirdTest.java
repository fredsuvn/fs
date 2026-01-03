package tests.third;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.third.ThirdKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThirdTest implements PrintTest {

    @Test
    public void testThirdClassName() throws Exception {
        String className = ThirdKit.thirdClassName("protobuf", "ProtobufSchemaHandler");
        assertEquals(ThirdKit.class.getPackage().getName() + ".protobuf.ProtobufSchemaHandler", className);
    }
}
