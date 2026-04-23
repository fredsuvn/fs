package tests.core.utils.jdbc;

import internal.annotations.J17Only;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeRef;
import space.sunqian.fs.utils.jdbc.JdbcKit;
import space.sunqian.fs.utils.jdbc.PreparedBatchSql;
import space.sunqian.fs.utils.jdbc.PreparedSql;
import space.sunqian.fs.utils.jdbc.SqlBatch;
import space.sunqian.fs.utils.jdbc.SqlBuilder;
import space.sunqian.fs.utils.jdbc.SqlInsert;
import space.sunqian.fs.utils.jdbc.SqlQuery;
import space.sunqian.fs.utils.jdbc.SqlRuntimeException;
import space.sunqian.fs.utils.jdbc.SqlUpdate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@J17Only
public class SqlTest {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:" + SqlTest.class.getName();
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private static Connection h2Connection;

    @BeforeEach
    public void setUpEach() throws SQLException, ClassNotFoundException {
        Class.forName(DB_DRIVER);
        h2Connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        // create table `user`
        // `id` (auto increment), `name`, `age`, `birthday`
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "create table if not exists `user` (" +
                "id int primary key auto_increment, name varchar(255), age int, birthday timestamp" +
                ")"
        );
        preparedStatement.execute();
    }

    @AfterEach
    public void destroyEach() throws SQLException, ClassNotFoundException {
        // Clean up: drop test table
        PreparedStatement preparedStatement = h2Connection.prepareStatement(
            "drop table if exists `user`"
        );
        preparedStatement.execute();

        if (h2Connection != null && !h2Connection.isClosed()) {
            h2Connection.close();
        }
    }

    @Test
    public void testSqlBuilderWithBasicOperations() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );
        SqlBuilder builder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .appendIf(true, ",", now)
            .appendIf(false, ",", now)
            .appendIf(true, ")")
            .appendIf(false, ")");
        PreparedSql preparedSql = builder.build();
        assertEquals(
            "INSERT INTO `user` (name, age, birthday) VALUES (?,?,?)",
            preparedSql.preparedSql()
        );
        assertEquals(
            ListKit.list("Alice", 25, now),
            preparedSql.parameters()
        );
    }

    @Test
    public void testSqlBuilderWithBatchOperations() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );
        SqlBuilder builder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");

        PreparedBatchSql batchSql = builder.buildBatch();
        assertEquals(
            "INSERT INTO `user` (name, age, birthday) VALUES (?,?,?)",
            batchSql.preparedSql()
        );
        assertEquals(
            Collections.emptyList(),
            batchSql.batchParameters()
        );

        batchSql.parameters(ListKit.list("Alice", 25, now));
        batchSql.batchParameters(ListKit.list(
            ListKit.list("Bob", 30, now),
            ListKit.list("Charlie", 35, now),
            ListKit.list("David", 40, now)
        ));
        assertEquals(
            ListKit.list(
                ListKit.list("Alice", 25, now),
                ListKit.list("Bob", 30, now),
                ListKit.list("Charlie", 35, now),
                ListKit.list("David", 40, now)
            ),
            batchSql.batchParameters()
        );
    }

    @Test
    public void testSqlBuilderWithListParameters() throws Exception {
        List<String> ids = ListKit.list("1", "2", "3");
        assertEquals(
            "select * from `user` where id in (?,?,?)",
            SqlBuilder.newBuilder()
                .append("select * from `user` where id in ")
                .append("(", ids).append(")")
                .build()
                .preparedSql()
        );

        Iterable<String> idsIt = ids::iterator;
        assertEquals(
            "select * from `user` where id in (?,?,?)",
            SqlBuilder.newBuilder()
                .append("select * from `user` where id in ")
                .append("(", idsIt).append(")")
                .build()
                .preparedSql()
        );

        assertEquals(
            "select * from `user` where id in ()",
            SqlBuilder.newBuilder()
                .append("select * from `user` where id in ")
                .append("(", Collections.emptyList()).append(")")
                .build()
                .preparedSql()
        );
    }

    @Test
    public void testSqlExecutionWithInsert() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );

        // SQL builder
        SqlBuilder insertBuilder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");

        // Insert data
        PreparedSql preparedSql = insertBuilder.build();
        assertEquals(
            "INSERT INTO `user` (name, age, birthday) VALUES (?,?,?)",
            preparedSql.preparedSql()
        );
        assertEquals(
            ListKit.list("Alice", 25, now),
            preparedSql.parameters()
        );

        SqlInsert insert = preparedSql.insert(h2Connection);
        assertEquals(1, insert.execute());
        assertEquals(1, insert.autoGeneratedKeys().size());
        assertEquals(1, insert.autoGeneratedKeys().get(0));

        // close
        insert.close();
        assertThrows(SqlRuntimeException.class, insert::execute);
    }

    @Test
    public void testSqlExecutionWithQuery() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );

        // Insert test data
        SqlBuilder insertBuilder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");
        SqlInsert insert = insertBuilder.build().insert(h2Connection);
        insert.execute();
        insert.close();

        // Query one data
        SqlBuilder queryBuilder = SqlBuilder.newBuilder().append("SELECT * FROM `user` order by id asc");
        SqlQuery<User> query = queryBuilder
            .build()
            .query(User.class, h2Connection);

        User user = query.first();
        assertEquals(1L, user.getId());
        assertEquals("Alice", user.getName());
        assertEquals(25, user.getAge());
        assertEquals(now, user.getBirthday());

        // close
        query.close();
        assertThrows(SqlRuntimeException.class, query::list);
    }

    @Test
    public void testSqlExecutionWithBatchInsert() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );

        // SQL builder
        SqlBuilder insertBuilder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");

        // Batch add
        PreparedBatchSql preparedBatchSql = insertBuilder.buildBatch();
        SqlBatch batch = preparedBatchSql.parameters(ListKit.list("Bob", 30, now))
            .batchParameters(ListKit.list(
                ListKit.list("Charlie", 35, now),
                ListKit.list("David", 40, now)
            )).execute(h2Connection);
        assertArrayEquals(new long[]{1L, 1L, 1L}, batch.execute());

        // Query inserted rows
        SqlBuilder queryBuilder = SqlBuilder.newBuilder().append("SELECT * FROM `user` order by id asc");
        SqlQuery<User> batchQuery = queryBuilder
            .build()
            .query(User.class, h2Connection);
        List<User> users = batchQuery.list();
        assertEquals(3, users.size());

        // close
        batch.close();
        batchQuery.close();
        assertThrows(SqlRuntimeException.class, batch::execute);
        assertThrows(SqlRuntimeException.class, batchQuery::list);
    }

    @Test
    public void testSqlExecutionWithUpdate() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );

        // Insert test data
        SqlBuilder insertBuilder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");
        SqlInsert insert = insertBuilder.build().insert(h2Connection);
        insert.execute();
        insert.close();

        // Update one
        SqlBuilder updateBuilder = SqlBuilder.newBuilder()
            .append("UPDATE `user` SET name = ", "Eve")
            .append(", age = ", 28)
            .append(" WHERE id = ", 1L);
        SqlUpdate update = updateBuilder.build().update(h2Connection);
        assertEquals(1, update.execute());

        // Query updated row
        User eve = SqlBuilder.newBuilder()
            .append("SELECT * FROM `user` WHERE id = ", 1L)
            .build()
            .query(User.class, h2Connection)
            .first(JdbcKit.defaultNameMapper());
        assertEquals(1L, eve.getId());
        assertEquals("Eve", eve.getName());
        assertEquals(28, eve.getAge());
        assertEquals(now, eve.getBirthday());
    }

    @Test
    public void testSqlExecutionWithDelete() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );

        // Insert test data
        SqlBuilder insertBuilder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Alice")
            .append(",", 25)
            .append(",", now)
            .append(")");
        SqlInsert insert = insertBuilder.build().insert(h2Connection);
        insert.execute();
        insert.close();

        // Delete one
        SqlBuilder deleteBuilder = SqlBuilder.newBuilder()
            .append("DELETE FROM `user` WHERE id = ", 1L);
        SqlUpdate delete = deleteBuilder.build().update(h2Connection);
        assertEquals(1, delete.execute());

        // Query deleted row
        SqlQuery<User> deletedQuery = SqlBuilder.newBuilder()
            .append("SELECT * FROM `user` WHERE id = ", 1L)
            .build()
            .query(User.class, h2Connection);
        assertNull(deletedQuery.first(
            JdbcKit.defaultNameMapper(),
            ObjectConverter.defaultConverter()
        ));
    }

    @Test
    public void testSqlExecutionWithTypeRefQuery() throws Exception {
        ZonedDateTime now = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        );

        // Insert test data
        SqlBuilder insertBuilder = SqlBuilder.newBuilder()
            .append("INSERT INTO `user` (name, age, birthday) VALUES ")
            .append("(", "Bob")
            .append(",", 30)
            .append(",", now)
            .append(")");
        SqlInsert insert = insertBuilder.build().insert(h2Connection);
        insert.execute();
        insert.close();

        // Query id = 1L using TypeRef
        User bob = SqlBuilder.newBuilder()
            .append("SELECT * FROM `user` WHERE id = ", 1L)
            .build()
            .query(new TypeRef<User>() {}, h2Connection)
            .list(
                JdbcKit.defaultNameMapper()
            )
            .get(0);
        assertEquals(1L, bob.getId());
        assertEquals("Bob", bob.getName());
        assertEquals(30, bob.getAge());
        assertEquals(now, bob.getBirthday());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class User {
        private Long id;
        private String name;
        private int age;
        private ZonedDateTime birthday;
    }
}