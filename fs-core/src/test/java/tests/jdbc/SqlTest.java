package tests.jdbc;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.jdbc.JdbcException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlTest {

    @Test
    public void testException() throws Exception {
        {
            // JdbcException
            assertThrows(JdbcException.class, () -> {
                throw new JdbcException();
            });
            assertThrows(JdbcException.class, () -> {
                throw new JdbcException("");
            });
            assertThrows(JdbcException.class, () -> {
                throw new JdbcException("", new RuntimeException());
            });
            assertThrows(JdbcException.class, () -> {
                throw new JdbcException(new RuntimeException());
            });
        }
    }
}
