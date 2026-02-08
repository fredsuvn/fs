package tests.utils.jdbc;

import internal.test.J17Only;
import lombok.Data;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.reflect.TypeRef;
import space.sunqian.fs.utils.jdbc.JdbcKit;
import space.sunqian.fs.utils.jdbc.SqlRuntimeException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
            "create table if not exists `user` (" +
                "id int primary key auto_increment, " +
                "first_name varchar(255), " +
                "last_name varchar(255), " +
                "age int" +
                ")"
        );
        preparedStatement.execute();
        // insert data: `test`, 18
        preparedStatement = h2Connection.prepareStatement(
            "insert into `user` (first_name, last_name, age) values (?, ?, ?)"
        );
        preparedStatement.setString(1, "first1");
        preparedStatement.setString(2, "last1");
        preparedStatement.setInt(3, 18);
        preparedStatement.execute();
        // insert data: `test2`, 20
        preparedStatement = h2Connection.prepareStatement(
            "insert into `user` (first_name, last_name, age) values (?, ?, ?)"
        );
        preparedStatement.setString(1, "first2");
        preparedStatement.setString(2, "last2");
        preparedStatement.setInt(3, 20);
        preparedStatement.execute();
    }

    @AfterAll
    public static void destroy() throws SQLException, ClassNotFoundException {
        // drop table `user`
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "drop table if exists `user`"
        );
        preparedStatement.execute();
    }

    @Test
    public void testJdbcQuery() throws Exception {
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "select * from `user`"
        );
        {
            // default name mapper for Class<User>
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> users = JdbcKit.toObject(
                resultSet,
                User.class
            );
            checkQueryResult(users);
            resultSet.close();
        }
        {
            // default name mapper for TypeRef<User>
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> users = JdbcKit.toObject(
                resultSet,
                new TypeRef<Map<String, Object>>() {}
            );
            checkQueryMapResult(users);
            resultSet.close();
        }
        {
            // default name mapper for Type
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> users = Fs.as(JdbcKit.toObject(
                resultSet,
                new TypeRef<Map<String, Object>>() {}.type()
            ));
            checkQueryMapResult(users);
            resultSet.close();
        }
        {
            // lower case name mapper
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> users = JdbcKit.toObject(
                resultSet,
                User.class,
                s ->
                    s.toLowerCase()
                        .replace("_", "")
                        .replace("n", "N")
            );
            checkQueryResult(users);
            resultSet.close();
        }
    }

    private void checkQueryResult(List<User> users) {
        assertEquals(2, users.size());
        // test, 18
        User user = users.get(0);
        assertEquals(1, user.getId());
        assertEquals("first1", user.getFirstName());
        assertEquals("last1", user.getLastName());
        assertEquals(18, user.getAge());
        // test2, 20
        user = users.get(1);
        assertEquals(2, user.getId());
        assertEquals("first2", user.getFirstName());
        assertEquals("last2", user.getLastName());
        assertEquals(20, user.getAge());
    }

    private void checkQueryMapResult(List<Map<String, Object>> users) {
        assertEquals(2, users.size());
        // test, 18
        Map<String, Object> user = users.get(0);
        assertEquals(1, user.get("id"));
        assertEquals("first1", user.get("firstName"));
        assertEquals("last1", user.get("lastName"));
        assertEquals(18, user.get("age"));
        // test2, 20
        Map<String, Object> user2 = users.get(1);
        assertEquals(2, user2.get("id"));
        assertEquals("first2", user2.get("firstName"));
        assertEquals("last2", user2.get("lastName"));
        assertEquals(20, user2.get("age"));
    }

    @Test
    public void testException() throws Exception {
        {
            // JdbcException
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

    @Data
    public static class User {
        private long id;
        private String firstName;
        private String lastName;
        private int age;
    }
}