package tests.core.utils.sql;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.sql.SqlRuntimeException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlTest {

    @Test
    public void testExceptions() {
        {
            // SqlRuntimeException
            assertThrows(SqlRuntimeException.class, () -> {
                throw new SqlRuntimeException();
            });
            assertThrows(SqlRuntimeException.class, () -> {
                throw new SqlRuntimeException("");
            });
            assertThrows(SqlRuntimeException.class, () -> {
                throw new SqlRuntimeException("", new RuntimeException());
            });
            assertThrows(SqlRuntimeException.class, () -> {
                throw new SqlRuntimeException(new RuntimeException());
            });
        }
    }
}