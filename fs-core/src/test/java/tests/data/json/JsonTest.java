package tests.data.json;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.data.json.JsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonTest {

    @Test
    public void testException() throws Exception {
        {
            // JsonException
            assertThrows(JsonException.class, () -> {
                throw new JsonException();
            });
            assertThrows(JsonException.class, () -> {
                throw new JsonException("");
            });
            assertThrows(JsonException.class, () -> {
                throw new JsonException("", new RuntimeException());
            });
            assertThrows(JsonException.class, () -> {
                throw new JsonException(new RuntimeException());
            });
        }
    }
}
