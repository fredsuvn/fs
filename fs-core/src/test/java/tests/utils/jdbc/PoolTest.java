package tests.utils.jdbc;

import internal.test.J17Only;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.IntVar;
import space.sunqian.fs.utils.jdbc.SimpleJdbcPool;
import space.sunqian.fs.utils.jdbc.SqlRuntimeException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Only
public class PoolTest {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:" + PoolTest.class.getName();
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    @Test
    public void testConnectionPool() throws Exception {
        {
            // common
            SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
                .driverClassName(DB_DRIVER)
                .url(DB_URL)
                .username(DB_USER)
                .password(DB_PASSWORD)
                .coreSize(2)
                .maxSize(3)
                .idleTimeout(Duration.ofSeconds(10))
                .build();
            Connection conn1 = pool.getConnection();
            assertNotNull(conn1);
            Connection conn2 = pool.getConnection();
            assertNotNull(conn2);
            Connection conn3 = pool.getConnection();
            assertNotNull(conn3);
            Connection conn4 = pool.getConnection();
            assertNull(conn4);
            conn1.close();
            Connection conn5 = pool.getConnection();
            assertNotNull(conn5);
            assertNotSame(conn1, conn5);
            pool.clean();
            assertEquals(3, pool.activeSize());
            assertEquals(0, pool.idleSize());
            assertEquals(3, pool.size());
            assertFalse(pool.isClosed());
            conn2.close();
            pool.close();
            assertTrue(pool.isClosed());
            assertEquals(2, pool.activeSize());
            assertEquals(0, pool.idleSize());
            assertEquals(0, pool.size());
        }
        {
            // connection factory and closer
            IntVar connCount = IntVar.of(0);
            IntVar closeCount = IntVar.of(0);
            Supplier<Connection> supplier = () -> Fs.uncheck(() -> {
                connCount.incrementAndGet();
                Class.forName(DB_DRIVER);
                return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }, SqlRuntimeException::new);
            Consumer<Connection> closer = conn -> Fs.uncheck(() -> {
                closeCount.incrementAndGet();
                conn.close();
            }, SqlRuntimeException::new);
            SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
                .driverClassName(DB_DRIVER)
                .url(DB_URL)
                .username(DB_USER)
                .password(DB_PASSWORD)
                .coreSize(2)
                .maxSize(3)
                .idleTimeout(Duration.ofSeconds(10))
                .connectionFactory(
                    (driverClassName, url, username, password) -> supplier.get())
                .closer(closer)
                .validator(conn -> Fs.uncheck(() -> conn.isValid(0), SqlRuntimeException::new))
                .build();
            assertEquals(2, connCount.get());
            assertEquals(0, closeCount.get());
            Connection conn1 = pool.getConnection();
            assertNotNull(conn1);
            assertEquals(2, connCount.get());
            assertEquals(0, closeCount.get());
            Connection conn2 = pool.getConnection();
            assertNotNull(conn2);
            assertEquals(2, connCount.get());
            assertEquals(0, closeCount.get());
            Connection conn3 = pool.getConnection();
            assertNotNull(conn3);
            assertEquals(3, connCount.get());
            assertEquals(0, closeCount.get());
            Connection conn4 = pool.getConnection();
            assertNull(conn4);
            assertEquals(3, connCount.get());
            assertEquals(0, closeCount.get());
            conn1.close();
            Connection conn5 = pool.getConnection();
            assertNotNull(conn5);
            assertNotSame(conn1, conn5);
            assertEquals(3, connCount.get());
            assertEquals(0, closeCount.get());
            conn2.close();
            conn3.close();
            conn5.close();
            assertEquals(0, closeCount.get());
            pool.close();
            assertEquals(3, connCount.get());
            assertEquals(3, closeCount.get());
            assertEquals(0, pool.activeSize());
        }
        {
            // username and password
            SimpleJdbcPool.newBuilder()
                .driverClassName(DB_DRIVER)
                .url(DB_URL)
                .username(DB_USER)
                .build()
                .close();
            SimpleJdbcPool.newBuilder()
                .driverClassName(DB_DRIVER)
                .url(DB_URL)
                .build()
                .close();
        }
        {
            // exception
            assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().coreSize(-2));
            assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().maxSize(1));
            assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().idleTimeout(Duration.ofSeconds(-1)));
            assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().build());
            assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().url(DB_URL).build());
        }
    }
}