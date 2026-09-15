package tests.core.utils.sql;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.sql.CallableParameter;
import space.sunqian.fs.utils.sql.SqlParameter;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SqlParameterTest {

    @Test
    public void testSqlParameter() throws Exception {

        // p1: 1L, BIGINT
        SqlParameter p1 = SqlParameter.of(1L, Types.BIGINT);
        assertEquals(1L, p1.value());
        assertEquals(Types.BIGINT, p1.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.BIGINT), p1.sqlType());

        // p2: "Alice", VARCHAR
        SqlParameter p2 = SqlParameter.of("Alice", Types.VARCHAR);
        assertEquals("Alice", p2.value());
        assertEquals(Types.VARCHAR, p2.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.VARCHAR), p2.sqlType());

        // p3: "Bob", VARCHAR
        SqlParameter p3 = SqlParameter.of("Bob", JDBCType.VARCHAR);
        assertEquals("Bob", p3.value());
        assertEquals(Types.VARCHAR, p3.sqlTypeCode());
        assertSame(JDBCType.VARCHAR, p3.sqlType());

        // test equals
        assertEquals(p1, p1);
        assertEquals(p1, SqlParameter.of(1L, JDBCType.BIGINT));
        assertEquals(p1, SqlParameter.of(1L, Types.BIGINT));
        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, SqlParameter.of(1L, Types.VARCHAR));
        assertNotEquals(p1, SqlParameter.of(1L, Types.BIGINT, JDBCType.VARCHAR));
        assertNotEquals(p1, "");

        // test hashCode
        assertEquals(p1.hashCode(), SqlParameter.of(1L, JDBCType.BIGINT).hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());

        // test toString
        assertEquals(
            "SqlParameter[1, " + Types.BIGINT + ", " + JDBCType.BIGINT.getName() + "]",
            p1.toString()
        );
        assertNotEquals(p1, SqlParameter.of(1L, Types.BIGINT, new SQLType() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            public String getVendor() {
                return "";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return 0;
            }
        }));
    }

    @Test
    public void testCallableParameter() throws Exception {

        // p1: 1L, BIGINT, IN
        CallableParameter p1 = CallableParameter.of(1L, Types.BIGINT, CallableParameter.Mode.IN);
        assertEquals(1L, p1.value());
        assertEquals(Types.BIGINT, p1.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.BIGINT), p1.sqlType());
        assertEquals(CallableParameter.Mode.IN, p1.mode());

        // p2: "Alice", VARCHAR, OUT
        CallableParameter p2 = CallableParameter.of("Alice", Types.VARCHAR, CallableParameter.Mode.OUT);
        assertEquals("Alice", p2.value());
        assertEquals(Types.VARCHAR, p2.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.VARCHAR), p2.sqlType());
        assertEquals(CallableParameter.Mode.OUT, p2.mode());

        // p3: "Bob", VARCHAR, IN_OUT
        CallableParameter p3 = CallableParameter.of("Bob", JDBCType.VARCHAR, CallableParameter.Mode.IN_OUT);
        assertEquals("Bob", p3.value());
        assertEquals(Types.VARCHAR, p3.sqlTypeCode());
        assertSame(JDBCType.VARCHAR, p3.sqlType());
        assertEquals(CallableParameter.Mode.IN_OUT, p3.mode());

        // test equals
        assertEquals(p1, p1);
        assertEquals(p1, CallableParameter.of(1L, JDBCType.BIGINT, CallableParameter.Mode.IN));
        assertEquals(p1, CallableParameter.of(1L, Types.BIGINT, CallableParameter.Mode.IN));
        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, CallableParameter.of(1L, Types.VARCHAR, CallableParameter.Mode.IN));
        assertNotEquals(p1, CallableParameter.of(1L, Types.BIGINT, JDBCType.VARCHAR, CallableParameter.Mode.IN));
        assertNotEquals(p1, CallableParameter.of(1L, Types.BIGINT, JDBCType.BIGINT, CallableParameter.Mode.OUT));
        assertNotEquals(p1, "");

        // test hashCode
        assertEquals(p1.hashCode(), CallableParameter.of(1L, JDBCType.BIGINT, CallableParameter.Mode.IN).hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), CallableParameter.of(1L, JDBCType.BIGINT, CallableParameter.Mode.OUT).hashCode());

        // test toString
        assertEquals(
            "CallableParameter[1, " + Types.BIGINT + ", " + JDBCType.BIGINT.getName() + ", " + CallableParameter.Mode.IN + "]",
            p1.toString()
        );
        assertNotEquals(p1, CallableParameter.of(1L, Types.BIGINT, new SQLType() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            public String getVendor() {
                return "";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return 0;
            }
        }, CallableParameter.Mode.IN));
    }
}