// package tests.utils.jdbc;
//
// import internal.test.J17Only;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import space.sunqian.fs.utils.jdbc.SimpleJdbcPool;
// import space.sunqian.fs.utils.jdbc.SqlRuntimeException;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.time.Duration;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicInteger;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.assertThrows;
//
// @J17Only
// public class PoolTest {
//
//     private static final String DB_DRIVER = "org.h2.Driver";
//     private static final String DB_URL = "jdbc:h2:mem:" + PoolTest.class.getName();
//     private static final String DB_USER = "sa";
//     private static final String DB_PASSWORD = "";
//
//     private SimpleJdbcPool pool;
//
//     @BeforeEach
//     public void setUp() throws SQLException, ClassNotFoundException {
//         // Create a connection pool with H2 in-memory database
//         pool = SimpleJdbcPool.newBuilder()
//             .driverClassName(DB_DRIVER)
//             .url(DB_URL)
//             .username(DB_USER)
//             .password(DB_PASSWORD)
//             .coreSize(2)
//             .maxSize(5)
//             .idleTimeout(Duration.ofSeconds(1))
//             .build();
//
//         // Initialize the database
//         try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
//             PreparedStatement statement = connection.prepareStatement(
//                 "CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(255))"
//             );
//             statement.execute();
//         }
//     }
//
//     @AfterEach
//     public void tearDown() {
//         if (pool != null && !pool.isClosed()) {
//             pool.close();
//         }
//     }
//
//     @Test
//     public void testGetAndReleaseConnection() throws SqlRuntimeException {
//         // Get a connection
//         Connection connection = pool.getConnection();
//         assertNotNull(connection);
//         assertEquals(1, pool.activeSize());
//         assertEquals(1, pool.size());
//
//         // Release the connection
//         boolean released = pool.releaseConnection(connection);
//         assertTrue(released);
//         assertEquals(1, pool.idleSize());
//         assertEquals(1, pool.size());
//     }
//
//     @Test
//     public void testMultipleConnections() throws SqlRuntimeException, InterruptedException {
//         List<Connection> connections = new ArrayList<>();
//
//         // Get multiple connections up to core size
//         for (int i = 0; i < 2; i++) {
//             Connection connection = pool.getConnection();
//             assertNotNull(connection);
//             connections.add(connection);
//         }
//         assertEquals(2, pool.activeSize());
//         assertEquals(2, pool.size());
//
//         // Get one more connection (should create a new one)
//         Connection connection = pool.getConnection();
//         assertNotNull(connection);
//         connections.add(connection);
//         assertEquals(3, pool.activeSize());
//         assertEquals(3, pool.size());
//
//         // Release all connections
//         for (Connection conn : connections) {
//             pool.releaseConnection(conn);
//         }
//         assertEquals(3, pool.idleSize());
//         assertEquals(3, pool.size());
//     }
//
//     @Test
//     public void testConnectionValidation() throws SqlRuntimeException, SQLException {
//         // Get a connection
//         Connection connection = pool.getConnection();
//         assertNotNull(connection);
//
//         // Use the connection
//         PreparedStatement statement = connection.prepareStatement(
//             "INSERT INTO test_table (id, name) VALUES (?, ?)"
//         );
//         statement.setInt(1, 1);
//         statement.setString(2, "test");
//         statement.execute();
//
//         // Release the connection
//         pool.releaseConnection(connection);
//
//         // Get the connection again and use it
//         Connection connection2 = pool.getConnection();
//         PreparedStatement statement2 = connection2.prepareStatement(
//             "SELECT * FROM test_table WHERE id = ?"
//         );
//         statement2.setInt(1, 1);
//         ResultSet resultSet = statement2.executeQuery();
//         assertTrue(resultSet.next());
//         assertEquals(1, resultSet.getInt("id"));
//         assertEquals("test", resultSet.getString("name"));
//         resultSet.close();
//         statement2.close();
//
//         pool.releaseConnection(connection2);
//     }
//
//     @Test
//     public void testClean() throws SqlRuntimeException, InterruptedException {
//         // Get connections and release them
//         List<Connection> connections = new ArrayList<>();
//         for (int i = 0; i < 3; i++) {
//             Connection connection = pool.getConnection();
//             connections.add(connection);
//         }
//
//         for (Connection conn : connections) {
//             pool.releaseConnection(conn);
//         }
//
//         assertEquals(3, pool.idleSize());
//         assertEquals(3, pool.size());
//
//         // Wait for idle timeout
//         Thread.sleep(1500);
//
//         // Clean the pool
//         pool.clean();
//
//         // Should have only coreSize connections after clean
//         assertEquals(2, pool.idleSize());
//         assertEquals(2, pool.size());
//     }
//
//     @Test
//     public void testConcurrentAccess() throws InterruptedException {
//         int threadCount = 10;
//         int operationsPerThread = 5;
//         ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//         CountDownLatch latch = new CountDownLatch(threadCount);
//         AtomicInteger successCount = new AtomicInteger(0);
//
//         for (int i = 0; i < threadCount; i++) {
//             final int threadId = i;
//             executor.submit(() -> {
//                 try {
//                     for (int j = 0; j < operationsPerThread; j++) {
//                         Connection connection = pool.getConnection();
//                         assertNotNull(connection);
//
//                         // Use the connection
//                         PreparedStatement statement = connection.prepareStatement(
//                             "INSERT INTO test_table (id, name) VALUES (?, ?)"
//                         );
//                         statement.setInt(1, threadId * operationsPerThread + j);
//                         statement.setString(2, "test_" + threadId + "_" + j);
//                         statement.execute();
//                         statement.close();
//
//                         // Release the connection
//                         pool.releaseConnection(connection);
//                         successCount.incrementAndGet();
//                     }
//                 } catch (Exception e) {
//                     // Ignore exceptions for this test
//                 } finally {
//                     latch.countDown();
//                 }
//             });
//         }
//
//         assertTrue(latch.await(10, TimeUnit.SECONDS));
//         assertEquals(threadCount * operationsPerThread, successCount.get());
//
//         executor.shutdown();
//     }
//
//     @Test
//     public void testReleaseInvalidConnection() throws SqlRuntimeException, SQLException {
//         // Get a connection from the pool
//         Connection connection = pool.getConnection();
//         assertNotNull(connection);
//
//         // Close the connection directly (simulating an invalid connection)
//         connection.close();
//
//         // Try to release the closed connection
//         boolean released = pool.releaseConnection(connection);
//         assertFalse(released);
//
//         // The pool should still have the core size connections
//         assertEquals(2, pool.idleSize());
//         assertEquals(2, pool.size());
//     }
//
//     @Test
//     public void testClose() throws SqlRuntimeException {
//         // Get connections
//         Connection connection1 = pool.getConnection();
//         Connection connection2 = pool.getConnection();
//         assertNotNull(connection1);
//         assertNotNull(connection2);
//
//         // Close the pool
//         pool.close();
//         assertTrue(pool.isClosed());
//
//         // Try to get a connection after closing
//         assertNull(pool.getConnection());
//
//         // Try to release a connection after closing
//         assertFalse(pool.releaseConnection(connection1));
//     }
//
//     @Test
//     public void testBuilderWithInvalidArguments() {
//         // Test with null URL
//         assertThrows(IllegalArgumentException.class, () -> {
//             SimpleJdbcPool.newBuilder()
//                 .url(null)
//                 .build();
//         });
//
//         // Test with empty URL
//         assertThrows(IllegalArgumentException.class, () -> {
//             SimpleJdbcPool.newBuilder()
//                 .url("")
//                 .build();
//         });
//     }
//
//     @Test
//     public void testBuilderWithCustomValidator() throws SqlRuntimeException {
//         // Create a pool with a custom validator
//         SimpleJdbcPool customPool = SimpleJdbcPool.newBuilder()
//             .driverClassName(DB_DRIVER)
//             .url(DB_URL)
//             .username(DB_USER)
//             .password(DB_PASSWORD)
//             .coreSize(1)
//             .maxSize(2)
//             .validator(connection -> {
//                 try {
//                     return connection != null && !connection.isClosed() && connection.isValid(1);
//                 } catch (SQLException e) {
//                     return false;
//                 }
//             })
//             .build();
//
//         try {
//             // Get a connection
//             Connection connection = customPool.getConnection();
//             assertNotNull(connection);
//
//             // Use the connection
//             PreparedStatement statement = connection.prepareStatement(
//                 "SELECT 1"
//             );
//             ResultSet resultSet = statement.executeQuery();
//             assertTrue(resultSet.next());
//             assertEquals(1, resultSet.getInt(1));
//             resultSet.close();
//             statement.close();
//
//             // Release the connection
//             assertTrue(customPool.releaseConnection(connection));
//         } finally {
//             customPool.close();
//         }
//     }
//
//     @Test
//     public void testConnectionCloseBehavior() throws SqlRuntimeException, SQLException {
//         // Get a connection
//         Connection connection = pool.getConnection();
//         assertNotNull(connection);
//         assertEquals(1, pool.activeSize());
//         assertEquals(0, pool.idleSize());
//
//         // Use the connection
//         PreparedStatement statement = connection.prepareStatement(
//             "INSERT INTO test_table (id, name) VALUES (?, ?)"
//         );
//         statement.setInt(1, 2);
//         statement.setString(2, "test_close");
//         statement.execute();
//         statement.close();
//
//         // Close the connection (should release it back to the pool)
//         connection.close();
//         assertEquals(0, pool.activeSize());
//         assertEquals(1, pool.idleSize());
//
//         // Get another connection (should reuse the same real connection)
//         Connection connection2 = pool.getConnection();
//         assertEquals(1, pool.activeSize());
//         assertEquals(0, pool.idleSize());
//
//         // Use the connection to verify the data was inserted
//         PreparedStatement statement2 = connection2.prepareStatement(
//             "SELECT * FROM test_table WHERE id = ?"
//         );
//         statement2.setInt(1, 2);
//         ResultSet resultSet = statement2.executeQuery();
//         assertTrue(resultSet.next());
//         assertEquals(2, resultSet.getInt("id"));
//         assertEquals("test_close", resultSet.getString("name"));
//         resultSet.close();
//         statement2.close();
//
//         // Close the connection again
//         connection2.close();
//     }
// }