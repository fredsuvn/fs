package tests.jdbc;

import internal.test.J17Only;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.jdbc.JdbcException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@J17Only
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
