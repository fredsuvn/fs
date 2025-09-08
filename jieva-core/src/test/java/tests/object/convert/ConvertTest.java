package tests.object.convert;

import org.testng.annotations.Test;
import xyz.sunqian.common.object.convert.ObjectConversionException;
import xyz.sunqian.test.PrintTest;

import static org.testng.Assert.expectThrows;

public class ConvertTest implements PrintTest {

    @Test
    public void testException() {
        {
            // ObjectConversionException
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException();
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException("");
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException("", new RuntimeException());
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException(new RuntimeException());
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException(Object.class, String.class);
            });
            expectThrows(ObjectConversionException.class, () -> {
                throw new ObjectConversionException(Object.class, String.class, new RuntimeException());
            });
        }
    }
}
