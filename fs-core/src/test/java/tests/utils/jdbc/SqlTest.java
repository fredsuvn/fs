// package tests.utils.jdbc;
//
// import internal.test.J17Only;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import space.sunqian.fs.utils.jdbc.*;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;
// import java.time.ZonedDateTime;
// import java.util.ArrayList;
// import java.util.List;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
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
//         // `id` (auto increment), `name`, `age`, `birthday`
//         PreparedStatement preparedStatement = h2Connection.prepareStatement(
//             "create table if not exists test_table (id int primary key auto_increment, name varchar(255), age int, birthday timestamp);"
//         );
//         preparedStatement.execute();
//     }
//
//     @AfterAll
//     public static void destroy() throws SQLException, ClassNotFoundException {
//         // Clean up: drop test table
//         PreparedStatement preparedStatement = h2Connection.prepareStatement(
//             "drop table if exists test_table;"
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
//         // Test basic SQL building and querying
//         {
//             // Insert data
//             PreparedSql insertSql = SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .append("", "Alice")
//                 .append("", 25)
//                 .append("", ZonedDateTime.now())
//                 .build()
//                 .connection(h2Connection);
//
//             SqlInsert insertResult = insertSql.insert();
//             assertEquals(1, insertResult.insertedRows());
//
//             // Query data
//             SqlQuery<User> query = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Alice")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class);
//
//             User user = query.first();
//             assertEquals("Alice", user.getName());
//             assertEquals(25, user.getAge());
//         }
//
//         // Test conditional SQL building
//         {
//             // Build SQL with conditions
//             String name = "Bob";
//             Integer age = null;
//
//             SqlBuilder builder = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE 1=1")
//                 .appendIf(name != null, " AND name = ?", name)
//                 .appendIf(age != null, " AND age = ?", age);
//
//             PreparedSql preparedSql = builder.build();
//             preparedSql.connection(h2Connection);
//
//             // Insert test data
//             SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .append("", "Bob")
//                 .append("", 30)
//                 .append("", ZonedDateTime.now())
//                 .build()
//                 .connection(h2Connection)
//                 .insert();
//
//             // Query data
//             SqlQuery<User> query = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Bob")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class);
//
//             User user = query.first();
//             assertEquals("Bob", user.getName());
//             assertEquals(30, user.getAge());
//         }
//
//         // Test collection parameters
//         {
//             // Insert more test data
//             SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .append("", "Charlie")
//                 .append("", 35)
//                 .append("", ZonedDateTime.now())
//                 .build()
//                 .connection(h2Connection)
//                 .insert();
//
//             // Use IN query
//             List<String> names = List.of("Alice", "Bob", "Charlie");
//             SqlQuery<User> query = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name IN ", names)
//                 .append(" ORDER BY id")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class);
//
//             List<User> users = query.resultList();
//             assertEquals(3, users.size());
//             assertEquals("Alice", users.get(0).getName());
//             assertEquals("Bob", users.get(1).getName());
//             assertEquals("Charlie", users.get(2).getName());
//         }
//
//         // Test update operations
//         {
//             // Update data
//             long affectedRows = SqlBuilder.newBuilder()
//                 .append("UPDATE test_table SET age = ? WHERE name = ?", 40, "Alice")
//                 .build()
//                 .connection(h2Connection)
//                 .update()
//                 .affectedRows();
//
//             assertEquals(1, affectedRows);
//
//             // Verify update
//             User user = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Alice")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .first();
//
//             assertEquals(40, user.getAge());
//         }
//     }
//
//     @Test
//     public void testPreparedSql() throws Exception {
//         // Test basic query operations
//         {
//             // Insert test data
//             PreparedSql insertSql = SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .append("", "David")
//                 .append("", 28)
//                 .append("", ZonedDateTime.now())
//                 .build()
//                 .connection(h2Connection);
//
//             SqlInsert insertResult = insertSql.insert();
//             assertEquals(1, insertResult.insertedRows());
//
//             // Get auto-generated keys
//             List<Object> generatedKeys = insertResult.autoGeneratedKeys();
//             assertEquals(1, generatedKeys.size());
//
//             // Query data
//             PreparedSql querySql = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "David")
//                 .build()
//                 .connection(h2Connection);
//
//             SqlQuery<User> query = querySql.query(User.class);
//             User user = query.first();
//             assertEquals("David", user.getName());
//             assertEquals(28, user.getAge());
//         }
//
//         // Test result set list operations
//         {
//             // Insert more test data
//             SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .append("", "Eve")
//                 .append("", 32)
//                 .append("", ZonedDateTime.now())
//                 .build()
//                 .connection(h2Connection)
//                 .insert();
//
//             SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .append("", "Frank")
//                 .append("", 27)
//                 .append("", ZonedDateTime.now())
//                 .build()
//                 .connection(h2Connection)
//                 .insert();
//
//             // Query all data
//             PreparedSql querySql = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name IN (?, ?, ?) ORDER BY id",
//                     List.of("David", "Eve", "Frank"))
//                 .build()
//                 .connection(h2Connection);
//
//             SqlQuery<User> query = querySql.query(User.class);
//             List<User> users = query.resultList();
//             assertEquals(3, users.size());
//             assertEquals("David", users.get(0).getName());
//             assertEquals("Eve", users.get(1).getName());
//             assertEquals("Frank", users.get(2).getName());
//         }
//
//         // Test update operations
//         {
//             // Update data
//             PreparedSql updateSql = SqlBuilder.newBuilder()
//                 .append("UPDATE test_table SET age = ? WHERE name = ?", 33, "Eve")
//                 .build()
//                 .connection(h2Connection);
//
//             SqlUpdate updateResult = updateSql.update();
//             assertEquals(1, updateResult.affectedRows());
//
//             // Verify update
//             PreparedSql querySql = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Eve")
//                 .build()
//                 .connection(h2Connection);
//
//             SqlQuery<User> query = querySql.query(User.class);
//             User user = query.first();
//             assertEquals(33, user.getAge());
//         }
//
//         // Test delete operations
//         {
//             // Delete data
//             PreparedSql deleteSql = SqlBuilder.newBuilder()
//                 .append("DELETE FROM test_table WHERE name = ?", "Frank")
//                 .build()
//                 .connection(h2Connection);
//
//             SqlUpdate deleteResult = deleteSql.update();
//             assertEquals(1, deleteResult.affectedRows());
//
//             // Verify deletion
//             PreparedSql querySql = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Frank")
//                 .build()
//                 .connection(h2Connection);
//
//             SqlQuery<User> query = querySql.query(User.class);
//             User user = query.first();
//             assertEquals(null, user);
//         }
//
//         // Test SQL string and parameter retrieval
//         {
//             PreparedSql preparedSql = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE age > ? AND name LIKE ?",
//                     25, "%a%")
//                 .build();
//
//             String sqlString = preparedSql.preparedSql();
//             assertEquals("SELECT * FROM test_table WHERE age > ? AND name LIKE ?", sqlString);
//
//             List<Object> parameters = preparedSql.parameters();
//             assertEquals(2, parameters.size());
//             assertEquals(25, parameters.get(0));
//             assertEquals("%a%", parameters.get(1));
//
//             // Set connection and execute
//             preparedSql.connection(h2Connection);
//             SqlQuery<User> query = preparedSql.query(User.class);
//             List<User> users = query.resultList();
//
//             // Verify results
//             for (User user : users) {
//                 assertEquals(true, user.getAge() > 25);
//                 assertEquals(true, user.getName().contains("a"));
//             }
//         }
//     }
//
//     @Test
//     public void testBatchSql() throws Exception {
//         // Test batch insert operations
//         {
//             // Prepare batch insert SQL
//             BatchSql batchSql = SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .buildBatch()
//                 .connection(h2Connection);
//
//             // Prepare batch parameters
//             List<List<Object>> batchParams = new ArrayList<>();
//             batchParams.add(List.of("Grace", 29, ZonedDateTime.now()));
//             batchParams.add(List.of("Henry", 31, ZonedDateTime.now()));
//             batchParams.add(List.of("Ivy", 26, ZonedDateTime.now()));
//
//             // Execute batch insert
//             SqlBatchResult batchResult = batchSql.batchParameters(batchParams).execute();
//             int[] affectedRows = batchResult.affectedRows();
//             assertEquals(3, affectedRows.length);
//             assertEquals(1, affectedRows[0]);
//             assertEquals(1, affectedRows[1]);
//             assertEquals(1, affectedRows[2]);
//
//             // Verify insert results
//             List<User> users = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name IN (?, ?, ?) ORDER BY id",
//                     List.of("Grace", "Henry", "Ivy"))
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .resultList();
//
//             assertEquals(3, users.size());
//             assertEquals("Grace", users.get(0).getName());
//             assertEquals(29, users.get(0).getAge());
//             assertEquals("Henry", users.get(1).getName());
//             assertEquals(31, users.get(1).getAge());
//             assertEquals("Ivy", users.get(2).getName());
//             assertEquals(26, users.get(2).getAge());
//         }
//
//         // Test batch operations with individual parameter addition
//         {
//             // Prepare batch update SQL
//             BatchSql batchSql = SqlBuilder.newBuilder()
//                 .append("UPDATE test_table SET age = ? WHERE name = ?")
//                 .buildBatch()
//                 .connection(h2Connection);
//
//             // Add parameters individually
//             batchSql.parameters(List.of(30, "Grace"));
//             batchSql.parameters(List.of(32, "Henry"));
//             batchSql.parameters(List.of(27, "Ivy"));
//
//             // Execute batch update
//             SqlBatchResult batchResult = batchSql.execute();
//             int[] affectedRows = batchResult.affectedRows();
//             assertEquals(3, affectedRows.length);
//             assertEquals(1, affectedRows[0]);
//             assertEquals(1, affectedRows[1]);
//             assertEquals(1, affectedRows[2]);
//
//             // Verify update results
//             List<User> users = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name IN (?, ?, ?) ORDER BY id",
//                     List.of("Grace", "Henry", "Ivy"))
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .resultList();
//
//             assertEquals(30, users.get(0).getAge());
//             assertEquals(32, users.get(1).getAge());
//             assertEquals(27, users.get(2).getAge());
//         }
//
//         // Test batch delete operations
//         {
//             // Prepare batch delete SQL
//             BatchSql batchSql = SqlBuilder.newBuilder()
//                 .append("DELETE FROM test_table WHERE name = ?")
//                 .buildBatch()
//                 .connection(h2Connection);
//
//             // Prepare batch parameters
//             List<List<Object>> batchParams = new ArrayList<>();
//             batchParams.add(List.of("Grace"));
//             batchParams.add(List.of("Henry"));
//
//             // Execute batch delete
//             SqlBatchResult batchResult = batchSql.batchParameters(batchParams).execute();
//             int[] affectedRows = batchResult.affectedRows();
//             assertEquals(2, affectedRows.length);
//             assertEquals(1, affectedRows[0]);
//             assertEquals(1, affectedRows[1]);
//
//             // Verify delete results
//             User grace = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Grace")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .first();
//
//             User henry = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Henry")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .first();
//
//             User ivy = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name = ?", "Ivy")
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .first();
//
//             assertEquals(null, grace);
//             assertEquals(null, henry);
//             assertEquals("Ivy", ivy.getName());
//         }
//
//         // Test SQL string and parameter retrieval in batch operations
//         {
//             // Create batch SQL object
//             BatchSql batchSql = SqlBuilder.newBuilder()
//                 .append("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)")
//                 .buildBatch();
//
//             // Verify SQL string
//             String sqlString = batchSql.preparedSql();
//             assertEquals("INSERT INTO test_table (name, age, birthday) VALUES (?, ?, ?)", sqlString);
//
//             // Initial parameters should be empty
//             List<Object> parameters = batchSql.parameters();
//             assertEquals(0, parameters.size());
//
//             // Add batch parameters
//             List<List<Object>> batchParams = new ArrayList<>();
//             batchParams.add(List.of("Jack", 35, ZonedDateTime.now()));
//             batchParams.add(List.of("Kate", 28, ZonedDateTime.now()));
//
//             batchSql.batchParameters(batchParams);
//
//             // Get batch parameters
//             List<List<Object>> batchedParameters = batchSql.batchParameters();
//             assertEquals(2, batchedParameters.size());
//             assertEquals(3, batchedParameters.get(0).size());
//             assertEquals(3, batchedParameters.get(1).size());
//
//             // Set connection and execute
//             batchSql.connection(h2Connection);
//             SqlBatchResult batchResult = batchSql.execute();
//             int[] affectedRows = batchResult.affectedRows();
//             assertEquals(2, affectedRows.length);
//             assertEquals(1, affectedRows[0]);
//             assertEquals(1, affectedRows[1]);
//
//             // Verify insert results
//             List<User> users = SqlBuilder.newBuilder()
//                 .append("SELECT * FROM test_table WHERE name IN (?, ?) ORDER BY id",
//                     List.of("Jack", "Kate"))
//                 .build()
//                 .connection(h2Connection)
//                 .query(User.class)
//                 .resultList();
//
//             assertEquals(2, users.size());
//             assertEquals("Jack", users.get(0).getName());
//             assertEquals("Kate", users.get(1).getName());
//         }
//     }
//
//     @Data
//     @AllArgsConstructor
//     @NoArgsConstructor
//     @EqualsAndHashCode
//     public static class User {
//         private long id;
//         private String name;
//         private int age;
//         private ZonedDateTime birthday;
//     }
// }