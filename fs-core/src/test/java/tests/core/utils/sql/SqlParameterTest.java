package tests.core.utils.sql;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.sql.SqlCallableParameter;
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
        SqlCallableParameter p1 = SqlCallableParameter.of(1L, Types.BIGINT, SqlCallableParameter.Mode.IN);
        assertEquals(1L, p1.value());
        assertEquals(Types.BIGINT, p1.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.BIGINT), p1.sqlType());
        assertEquals(SqlCallableParameter.Mode.IN, p1.mode());

        // p2: "Alice", VARCHAR, OUT
        SqlCallableParameter p2 = SqlCallableParameter.of("Alice", Types.VARCHAR, SqlCallableParameter.Mode.OUT);
        assertEquals("Alice", p2.value());
        assertEquals(Types.VARCHAR, p2.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.VARCHAR), p2.sqlType());
        assertEquals(SqlCallableParameter.Mode.OUT, p2.mode());

        // p3: "Bob", VARCHAR, IN_OUT
        SqlCallableParameter p3 = SqlCallableParameter.of("Bob", JDBCType.VARCHAR, SqlCallableParameter.Mode.IN_OUT);
        assertEquals("Bob", p3.value());
        assertEquals(Types.VARCHAR, p3.sqlTypeCode());
        assertSame(JDBCType.VARCHAR, p3.sqlType());
        assertEquals(SqlCallableParameter.Mode.IN_OUT, p3.mode());

        // test equals
        assertEquals(p1, p1);
        assertEquals(p1, SqlCallableParameter.of(1L, JDBCType.BIGINT, SqlCallableParameter.Mode.IN));
        assertEquals(p1, SqlCallableParameter.of(1L, Types.BIGINT, SqlCallableParameter.Mode.IN));
        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, SqlCallableParameter.of(1L, Types.VARCHAR, SqlCallableParameter.Mode.IN));
        assertNotEquals(p1, SqlCallableParameter.of(1L, Types.BIGINT, JDBCType.VARCHAR, SqlCallableParameter.Mode.IN));
        assertNotEquals(p1, SqlCallableParameter.of(1L, Types.BIGINT, JDBCType.BIGINT, SqlCallableParameter.Mode.OUT));
        assertNotEquals(p1, "");

        // test hashCode
        assertEquals(p1.hashCode(), SqlCallableParameter.of(1L, JDBCType.BIGINT, SqlCallableParameter.Mode.IN).hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), SqlCallableParameter.of(1L, JDBCType.BIGINT, SqlCallableParameter.Mode.OUT).hashCode());

        // test toString
        assertEquals(
            "CallableParameter[1, " + Types.BIGINT + ", " + JDBCType.BIGINT.getName() + ", " + SqlCallableParameter.Mode.IN + "]",
            p1.toString()
        );
        assertNotEquals(p1, SqlCallableParameter.of(1L, Types.BIGINT, new SQLType() {
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
        }, SqlCallableParameter.Mode.IN));
    }
}