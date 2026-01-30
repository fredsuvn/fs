package tests.data.properties;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.data.properties.PropertiesException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PropertiesTest {

    @Test
    public void testException() throws Exception {
        {
            // PropertiesException
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException();
            });
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException("");
            });
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException("", new RuntimeException());
            });
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException(new RuntimeException());
            });
        }
    }
}
