package tests.utils.jdbc;

import internal.test.J17Only;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.jdbc.SimpleJdbcPool;

import java.sql.Connection;
import java.time.Duration;

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
        pool.close();
        assertTrue(pool.isClosed());
        assertEquals(3, pool.activeSize());
        assertEquals(0, pool.idleSize());
        assertEquals(0, pool.size());

        // exception
        assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().coreSize(-2));
        assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().maxSize(1));
        assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().idleTimeout(Duration.ofSeconds(-1)));
        assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().build());
        assertThrows(IllegalArgumentException.class, () -> SimpleJdbcPool.newBuilder().url(DB_URL).build());
    }
}