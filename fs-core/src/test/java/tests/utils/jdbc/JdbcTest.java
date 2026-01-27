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
    private static final String DB_URL = "jdbc:h2:mem:" + JdbcTest.class.getName();
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private static Connection h2Connection;

    @BeforeAll
    public static void setUp() throws SQLException, ClassNotFoundException {
        Class.forName(DB_DRIVER);
        h2Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        // create table `user`
        // `id` (auto increment), `name`, `age`
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "create table if not exists user (id int primary key auto_increment, name varchar(255), age int)"
        );
        preparedStatement.execute();
        // insert data: `test`, 18
        preparedStatement = h2Connection.prepareStatement(
            "insert into user (name, age) values (?, ?)"
        );
        preparedStatement.setString(1, "test");
        preparedStatement.setInt(2, 18);
        preparedStatement.execute();
        // insert data: `test2`, 20
        preparedStatement = h2Connection.prepareStatement(
            "insert into user (name, age) values (?, ?)"
        );
        preparedStatement.setString(1, "test2");
        preparedStatement.setInt(2, 20);
        preparedStatement.execute();
    }

    @AfterAll
    public static void destroy() throws SQLException, ClassNotFoundException {
        // drop table `user`
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "drop table if exists user"
        );
        preparedStatement.execute();
    }

    @Test
    public void testJdbcQuery() throws Exception {
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "select * from user"
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        List<User> users = JdbcKit.toObject(
            resultSet,
            User.class,
            String::toLowerCase,
            ObjectConverter.defaultConverter()
        );
        assertEquals(2, users.size());
        // test, 18
        User user = users.get(0);
        assertEquals(1, user.getId());
        assertEquals("test", user.getName());
        assertEquals(18, user.getAge());
        // test2, 20
        user = users.get(1);
        assertEquals(2, user.getId());
        assertEquals("test2", user.getName());
        assertEquals(20, user.getAge());
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
    public static class User {
        private long id;
        private String name;
        private int age;
    }
}