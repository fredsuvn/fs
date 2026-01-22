package tests.utils.jdbc;

import internal.test.J17Only;
import lombok.Data;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.utils.jdbc.JdbcException;
import space.sunqian.fs.utils.jdbc.JdbcKit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@J17Only
public class JdbcTest {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:" + JdbcTest.class.getName();//"jdbc:h2:./SqlTest.h2db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private static Connection h2Connection;

    @BeforeAll
    public static void setUp() throws SQLException, ClassNotFoundException {
        Class.forName(DB_DRIVER);
        h2Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        // create table `test_table`
        // `id` (auto increment), `name`, `age`
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "create table if not exists test_table (id int primary key auto_increment, name varchar(255), age int);"
        );
        preparedStatement.execute();
        // insert data: `test`, 18
        preparedStatement = h2Connection.prepareStatement(
            "insert into test_table (name, age) values (?, ?);"
        );
        preparedStatement.setString(1, "test");
        preparedStatement.setInt(2, 18);
        preparedStatement.execute();
        // insert data: `test2`, 20
        preparedStatement = h2Connection.prepareStatement(
            "insert into test_table (name, age) values (?, ?);"
        );
        preparedStatement.setString(1, "test2");
        preparedStatement.setInt(2, 20);
        preparedStatement.execute();
    }

    @AfterAll
    public static void destroy() throws SQLException, ClassNotFoundException {
        // drop table `test_table`
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "drop table if exists test_table;"
        );
        preparedStatement.execute();
    }

    @Test
    public void testQuery() throws Exception {
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "select * from test_table;"
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        List<TestTable> testTables = JdbcKit.toObject(
            resultSet,
            TestTable.class,
            String::toLowerCase,
            ObjectConverter.defaultConverter()
        );
        assertEquals(2, testTables.size());
        // test, 18
        TestTable testTable = testTables.get(0);
        assertEquals(1, testTable.getId());
        assertEquals("test", testTable.getName());
        assertEquals(18, testTable.getAge());
        // test2, 20
        testTable = testTables.get(1);
        assertEquals(2, testTable.getId());
        assertEquals("test2", testTable.getName());
        assertEquals(20, testTable.getAge());
    }

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

    @Data
    public static class TestTable {
        private long id;
        private String name;
        private int age;
    }
}