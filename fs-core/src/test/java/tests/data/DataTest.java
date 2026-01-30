package tests.data;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.data.DataException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataTest {

    @Test
    public void testException() throws Exception {
        {
            // DataException
            assertThrows(DataException.class, () -> {
                throw new DataException();
            });
            assertThrows(DataException.class, () -> {
                throw new DataException("");
            });
            assertThrows(DataException.class, () -> {
                throw new DataException("", new RuntimeException());
            });
            assertThrows(DataException.class, () -> {
                throw new DataException(new RuntimeException());
            });
        }
    }
}
