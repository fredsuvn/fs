package tests.data.json;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.data.json.JsonDataException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonTest {

    @Test
    public void testException() throws Exception {
        {
            // JsonDataException
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException();
            });
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException("");
            });
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException("", new RuntimeException());
            });
            assertThrows(JsonDataException.class, () -> {
                throw new JsonDataException(new RuntimeException());
            });
        }
    }
}
