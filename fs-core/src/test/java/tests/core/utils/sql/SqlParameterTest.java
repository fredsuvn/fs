package tests.core.utils.sql;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.utils.sql.SqlParameter;

import java.sql.JDBCType;
import java.sql.ParameterMetaData;
import java.sql.SQLType;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SqlParameterTest {

    @Test
    public void testSqlParameter() throws Exception {

        // p1: 1L, BIGINT
        SqlParameter p1 = SqlParameter.of(1L, Types.BIGINT);
        assertEquals(1L, p1.value());
        assertEquals(Types.BIGINT, p1.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.BIGINT), p1.sqlType());
        assertNull(p1.mode());

        // p2: "Alice", VARCHAR
        SqlParameter p2 = SqlParameter.of("Alice", Types.VARCHAR);
        assertEquals("Alice", p2.value());
        assertEquals(Types.VARCHAR, p2.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.VARCHAR), p2.sqlType());
        assertNull(p2.mode());

        // p3: "Bob", VARCHAR
        SqlParameter p3 = SqlParameter.of("Bob", JDBCType.VARCHAR);
        assertEquals("Bob", p3.value());
        assertEquals(Types.VARCHAR, p3.sqlTypeCode());
        assertSame(JDBCType.VARCHAR, p3.sqlType());
        assertNull(p3.mode());

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
        SqlParameter p1 = SqlParameter.of(1L, Types.BIGINT, SqlParameter.Mode.IN);
        assertEquals(1L, p1.value());
        assertEquals(Types.BIGINT, p1.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.BIGINT), p1.sqlType());
        assertEquals(SqlParameter.Mode.IN, p1.mode());

        // p2: "Alice", VARCHAR, OUT
        SqlParameter p2 = SqlParameter.of("Alice", Types.VARCHAR, SqlParameter.Mode.OUT);
        assertEquals("Alice", p2.value());
        assertEquals(Types.VARCHAR, p2.sqlTypeCode());
        assertEquals(JDBCType.valueOf(Types.VARCHAR), p2.sqlType());
        assertEquals(SqlParameter.Mode.OUT, p2.mode());

        // p3: "Bob", VARCHAR, IN_OUT
        SqlParameter p3 = SqlParameter.of("Bob", JDBCType.VARCHAR, SqlParameter.Mode.IN_OUT);
        assertEquals("Bob", p3.value());
        assertEquals(Types.VARCHAR, p3.sqlTypeCode());
        assertSame(JDBCType.VARCHAR, p3.sqlType());
        assertEquals(SqlParameter.Mode.IN_OUT, p3.mode());

        // test equals
        assertEquals(p1, p1);
        assertEquals(p1, SqlParameter.of(1L, JDBCType.BIGINT, SqlParameter.Mode.IN));
        assertEquals(p1, SqlParameter.of(1L, Types.BIGINT, SqlParameter.Mode.IN));
        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, SqlParameter.of(1L, Types.VARCHAR, SqlParameter.Mode.IN));
        assertNotEquals(p1, SqlParameter.of(1L, Types.BIGINT, JDBCType.VARCHAR, SqlParameter.Mode.IN));
        assertNotEquals(p1, SqlParameter.of(1L, Types.BIGINT, JDBCType.BIGINT, SqlParameter.Mode.OUT));
        assertNotEquals(p1, "");

        // test hashCode
        assertEquals(p1.hashCode(), SqlParameter.of(1L, JDBCType.BIGINT, SqlParameter.Mode.IN).hashCode());
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), SqlParameter.of(1L, JDBCType.BIGINT, SqlParameter.Mode.OUT).hashCode());

        // test toString
        assertEquals(
            "CallableParameter[1, " + Types.BIGINT + ", " + JDBCType.BIGINT.getName() + ", " + SqlParameter.Mode.IN + "]",
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
        }, SqlParameter.Mode.IN));

        // test mode
        assertEquals(ParameterMetaData.parameterModeIn, SqlParameter.Mode.IN.code());
        assertEquals(ParameterMetaData.parameterModeOut, SqlParameter.Mode.OUT.code());
        assertEquals(ParameterMetaData.parameterModeInOut, SqlParameter.Mode.IN_OUT.code());
        assertEquals(ParameterMetaData.parameterModeUnknown, SqlParameter.Mode.UNKNOWN.code());
    }
}