package tests.jdbc;

import internal.test.J17Only;
import lombok.Data;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.jdbc.JdbcException;
import space.sunqian.fs.jdbc.JdbcKit;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
