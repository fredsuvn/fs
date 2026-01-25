// package tests.utils.jdbc;
//
// import internal.test.J17Only;
// import lombok.Data;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import space.sunqian.fs.utils.jdbc.*;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;
// import java.util.Arrays;
// import java.util.List;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// @J17Only
// public class SqlTest {
//
//     private static final String DB_DRIVER = "org.h2.Driver";
//     private static final String DB_URL = "jdbc:h2:mem:" + SqlTest.class.getName();
//     private static final String DB_USER = "sa";
//     private static final String DB_PASSWORD = "";
//
//     private static Connection h2Connection;
//
//     @BeforeAll
//     public static void setUp() throws SQLException, ClassNotFoundException {
//         Class.forName(DB_DRIVER);
//         h2Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//
//         // create table `test_table`
//         // `id` (auto increment), `name`, `age`
//         PreparedStatement preparedStatement = h2Connection.prepareStatement(
//             "create table if not exists test_table (id int primary key auto_increment, name varchar(255), age int);"
//         );
//         preparedStatement.execute();
//
//         // create table `users`
//         // `id` (auto increment), `name`, `email`, `age`
//         preparedStatement = h2Connection.prepareStatement(
//             "create table if not exists users (id int primary key auto_increment, name varchar(255), email varchar(255), age int);"
//         );
//         preparedStatement.execute();
//     }
//
//     @AfterAll
//     public static void destroy() throws SQLException, ClassNotFoundException {
//         // drop tables
//         PreparedStatement preparedStatement = h2Connection.prepareStatement(
//             "drop table if exists test_table;"
//         );
//         preparedStatement.execute();
//
//         preparedStatement = h2Connection.prepareStatement(
//             "drop table if exists users;"
//         );
//         preparedStatement.execute();
//
//         if (h2Connection != null && !h2Connection.isClosed()) {
//             h2Connection.close();
//         }
//     }
//
//     @Test
//     public void testSqlBuilder() throws Exception {
//         // Test basic append
//         SqlBuilder builder = SqlBuilder.newBuilder()
//             .append("SELECT * FROM users")
//             .append(" WHERE age > ", 18)
//             .append(" ORDER BY name");
//
//         PreparedSql preparedSql = builder.build();
//         assertEquals("SELECT * FROM users WHERE age > ? ORDER BY name", preparedSql.preparedSql());
//         assertEquals(1, preparedSql.parameters().size());
//         assertEquals(18, preparedSql.parameters().get(0));
//
//         // Test append with iterable
//         builder = SqlBuilder.newBuilder()
//             .append("SELECT * FROM users")
//             .append(" WHERE id IN (", Arrays.asList(1, 2, 3))
//             .append(")");
//
//         preparedSql = builder.build();
//         assertEquals("SELECT * FROM users WHERE id IN (?,?,?)", preparedSql.preparedSql());
//         assertEquals(3, preparedSql.parameters().size());
//         assertEquals(1, preparedSql.parameters().get(0));
//         assertEquals(2, preparedSql.parameters().get(1));
//         assertEquals(3, preparedSql.parameters().get(2));
//
//         // Test appendIf
//         boolean searchEnabled = true;
//         builder = SqlBuilder.newBuilder()
//             .append("SELECT * FROM users")
//             .appendIf(searchEnabled, " WHERE name LIKE ?", "%test%");
//
//         preparedSql = builder.build();
//         assertEquals("SELECT * FROM users WHERE name LIKE ?", preparedSql.preparedSql());
//         assertEquals(1, preparedSql.parameters().size());
//         assertEquals("%test%", preparedSql.parameters().get(0));
//
//         // Test appendIf with condition false
//         searchEnabled = false;
//         builder = SqlBuilder.newBuilder()
//             .append("SELECT * FROM users")
//             .appendIf(searchEnabled, " WHERE name LIKE ?", "%test%");
//
//         preparedSql = builder.build();
//         assertEquals("SELECT * FROM users", preparedSql.preparedSql());
//         assertEquals(0, preparedSql.parameters().size());
//     }
//
//     @Test
//     public void testSqlQuery() throws Exception {
//         // Insert test data
//         PreparedStatement preparedStatement = h2Connection.prepareStatement(
//             "insert into test_table (name, age) values (?, ?);"
//         );
//         preparedStatement.setString(1, "test");
//         preparedStatement.setInt(2, 18);
//         preparedStatement.execute();
//
//         preparedStatement = h2Connection.prepareStatement(
//             "insert into test_table (name, age) values (?, ?);"
//         );
//         preparedStatement.setString(1, "test2");
//         preparedStatement.setInt(2, 20);
//         preparedStatement.execute();
//
//         // Test query
//         PreparedSql preparedSql = SqlBuilder.newBuilder()
//             .append("SELECT * FROM test_table")
//             .build()
//             .connection(h2Connection);
//
//         try (SqlQuery<TestTable> query = preparedSql.query(TestTable.class)) {
//             List<TestTable> results = query.resultList();
//             assertEquals(2, results.size());
//
//             TestTable first = query.first();
//             assertNotNull(first);
//             assertEquals("test", first.getName());
//             assertEquals(18, first.getAge());
//         }
//
//         // Test query with parameters
//         preparedSql = SqlBuilder.newBuilder()
//             .append("SELECT * FROM test_table WHERE age > ", 18)
//             .build()
//             .connection(h2Connection);
//
//         try (SqlQuery<TestTable> query = preparedSql.query(TestTable.class)) {
//             List<TestTable> results = query.resultList();
//             assertEquals(1, results.size());
//             assertEquals("test2", results.get(0).getName());
//             assertEquals(20, results.get(0).getAge());
//         }
//
//         // Test query with no results
//         preparedSql = SqlBuilder.newBuilder()
//             .append("SELECT * FROM test_table WHERE age > ", 100)
//             .build()
//             .connection(h2Connection);
//
//         try (SqlQuery<TestTable> query = preparedSql.query(TestTable.class)) {
//             List<TestTable> results = query.resultList();
//             assertEquals(0, results.size());
//
//             TestTable first = query.first();
//             assertNull(first);
//         }
//     }
//
//     @Test
//     public void testSqlUpdate() throws Exception {
//         // Insert test data
//         PreparedStatement preparedStatement = h2Connection.prepareStatement(
//             "insert into test_table (name, age) values (?, ?);"
//         );
//         preparedStatement.setString(1, "test");
//         preparedStatement.setInt(2, 18);
//         preparedStatement.execute();
//
//         // Test update
//         PreparedSql preparedSql = SqlBuilder.newBuilder()
//             .append("UPDATE test_table SET age = ", 20)
//             .append(" WHERE name = ", "test")
//             .build()
//             .connection(h2Connection);
//
//         try (SqlUpdate update = preparedSql.update()) {
//             long affectedRows = update.affectedRows();
//             assertEquals(1, affectedRows);
//         }
//
//         // Verify update
//         preparedSql = SqlBuilder.newBuilder()
//             .append("SELECT * FROM test_table WHERE name = ", "test")
//             .build()
//             .connection(h2Connection);
//
//         try (SqlQuery<TestTable> query = preparedSql.query(TestTable.class)) {
//             TestTable result = query.first();
//             assertNotNull(result);
//             assertEquals(20, result.getAge());
//         }
//
//         // Test update with no matching rows
//         preparedSql = SqlBuilder.newBuilder()
//             .append("UPDATE test_table SET age = ", 30)
//             .append(" WHERE name = ", "nonexistent")
//             .build()
//             .connection(h2Connection);
//
//         try (SqlUpdate update = preparedSql.update()) {
//             long affectedRows = update.affectedRows();
//             assertEquals(0, affectedRows);
//         }
//     }
//
//     @Test
//     public void testSqlInsert() throws Exception {
//         // Test insert
//         PreparedSql preparedSql = SqlBuilder.newBuilder()
//             .append("INSERT INTO users (name, email, age) VALUES (?, ?, ?)", "test", "test@example.com", 25)
//             .build()
//             .connection(h2Connection);
//
//         try (SqlInsert insert = preparedSql.insert()) {
//             long insertedRows = insert.insertedRows();
//             assertEquals(1, insertedRows);
//
//             List<Object> generatedKeys = insert.autoGeneratedKeys();
//             assertEquals(1, generatedKeys.size());
//             assertTrue(generatedKeys.get(0) instanceof Number);
//             long id = ((Number) generatedKeys.get(0)).longValue();
//             assertTrue(id > 0);
//
//             // Verify insert
//             PreparedSql verifySql = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM users WHERE id = ", id)
//                 .build()
//                 .connection(h2Connection);
//
//             try (SqlQuery<User> query = verifySql.query(User.class)) {
//                 User user = query.first();
//                 assertNotNull(user);
//                 assertEquals("test", user.getName());
//                 assertEquals("test@example.com", user.getEmail());
//                 assertEquals(25, user.getAge());
//             }
//         }
//
//         // Test multiple inserts
//         preparedSql = SqlBuilder.newBuilder()
//             .append("INSERT INTO users (name, email, age) VALUES (?, ?, ?)", "test2", "test2@example.com", 30)
//             .build()
//             .connection(h2Connection);
//
//         try (SqlInsert insert = preparedSql.insert()) {
//             long insertedRows = insert.insertedRows();
//             assertEquals(1, insertedRows);
//         }
//
//         // Verify count
//         preparedSql = SqlBuilder.newBuilder()
//             .append("SELECT COUNT(*) FROM users")
//             .build()
//             .connection(h2Connection);
//
//         try (SqlQuery<Long> query = preparedSql.query(Long.class)) {
//             Long count = query.first();
//             assertEquals(2, count);
//         }
//     }
//
//     @Data
//     public static class TestTable {
//         private long id;
//         private String name;
//         private int age;
//     }
//
//     @Data
//     public static class User {
//         private long id;
//         private String name;
//         private String email;
//         private int age;
//     }
// }