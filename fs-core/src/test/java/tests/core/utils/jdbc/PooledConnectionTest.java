package tests.core.utils.jdbc;

import internal.annotations.J17Also;
import internal.utils.Mocker;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.jdbc.SimpleJdbcPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Also
public class PooledConnectionTest {

    // private static final String DB_DRIVER = "org.h2.Driver";
    // private static final String DB_URL = "jdbc:h2:mem:" + ConnectionWrapperTest.class.getName();
    // private static final String DB_USER = "sa";
    // private static final String DB_PASSWORD = "";

    @Test
    public void testConnectionWrapperFunctionality() throws Exception {
        // Create pool with mocked connection factory
        SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
            .driverClassName("DB_DRIVER")
            .url("DB_URL")
            .username("DB_USER")
            .password("DB_PASSWORD")
            .coreSize(2)
            .maxSize(3)
            .idleTimeout(Duration.ofSeconds(10))
            .connectionFactory(
                (driverClassName, url, username, password) ->
                    Mocker.mock(Connection.class))
            .build();

        // Get connection and verify it's not closed
        Connection conn = pool.getConnection();
        assertFalse(conn.isClosed());

        // Test all connection methods except close and isClosed
        List<Method> methods = Arrays.asList(Connection.class.getMethods()).stream()
            .filter(method -> !method.getName().equals("close") && !method.getName().equals("isClosed"))
            .collect(Collectors.toList());
        List<Object[]> argsList = new ArrayList<>();
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = Mocker.mock(parameters[i].getType());
            }
            method.invoke(conn, args);
            argsList.add(args);
        }

        // Close connection and verify it's closed
        conn.close();
        assertTrue(conn.isClosed());

        // Verify methods throw exceptions after close
        int i = 0;
        for (Method method : methods) {
            Object[] args = argsList.get(i++);
            InvocationTargetException e = assertThrows(InvocationTargetException.class, () -> method.invoke(conn, args));
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            if (exceptionTypes.length > 0 && SQLClientInfoException.class.equals(exceptionTypes[0])) {
                assertInstanceOf(SQLClientInfoException.class, e.getCause());
            } else {
                assertInstanceOf(SQLException.class, e.getCause());
            }
        }
    }
}