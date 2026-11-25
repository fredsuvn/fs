package tests.core.utils.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.utils.sql.SqlBeanMapping;
import space.sunqian.fs.utils.sql.SqlColumnMapping;
import space.sunqian.fs.utils.sql.SqlMapper;
import space.sunqian.fs.utils.sql.annotation.SqlColumn;
import space.sunqian.fs.utils.sql.annotation.SqlTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlMapperTest {

    @Test
    public void testNameMapper() {
        {
            // to column name
            NameMapper nameMapper = SqlMapper.defaultToColumnName();
            assertEquals("SOME_COLUMN", nameMapper.map("someColumn"));
            assertEquals("SOME_COLUMN", nameMapper.map("SomeColumn"));
        }
        {
            // to property name
            NameMapper nameMapper = SqlMapper.defaultToPropertyName();
            assertEquals("someColumn", nameMapper.map("SOME_COLUMN"));
            assertEquals("someColumn", nameMapper.map("some_column"));
            assertEquals("someColumn", nameMapper.map("Some_Column"));
        }
        {
            // to table name
            NameMapper nameMapper = SqlMapper.defaultToTableName();
            assertEquals("SOME_TABLE", nameMapper.map("a.b.c.SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.b.c.Xxx$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.b.c.Xxx$Yyy$Zzz$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.Xxx$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.Xxx$Yyy$Zzz$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("Xxx$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("Xxx$Yyy$Zzz$SomeTable"));
        }
    }

    @Test
    public void testMapping() {
        SqlMapper sqlMapper = SqlMapper.defaultMapper();
        {
            // Person
            ObjectMeta personMeta = ObjectMeta.of(Person.class);
            SqlBeanMapping personMapping = sqlMapper.toBeanMapping(personMeta);
            // name
            assertEquals("PERSON", personMapping.sourceName());
            // test mapping
            testPersonMapping(personMeta, personMapping);
        }
        {
            // PersonExt
            ObjectMeta personMeta = ObjectMeta.of(PersonExt.class);
            SqlBeanMapping personMapping = sqlMapper.toBeanMapping(personMeta);
            // name
            assertEquals("PERSON_EXTENSION", personMapping.sourceName());
            // test mapping
            testPersonMapping(personMeta, personMapping);
        }
        {
            // No table annotation
            ObjectMeta beanMeta = ObjectMeta.of(NoTableAnnotation.class);
            SqlBeanMapping beanMapping = sqlMapper.toBeanMapping(beanMeta);
            // name
            assertEquals("NO_TABLE_ANNOTATION", beanMapping.sourceName());
            // 0 column
            assertEquals(0, beanMapping.columnsByPropertyName().size());
            assertEquals(0, beanMapping.columnsByColumnName().size());
        }
        {
            // Multi primary keys
            ObjectMeta beanMeta = ObjectMeta.of(MultiPrimaryKeys.class);
            SqlBeanMapping beanMapping = sqlMapper.toBeanMapping(beanMeta);
            // name
            assertEquals("MULTI_PRIMARY_KEYS", beanMapping.sourceName());
            assertEquals(3, beanMapping.columnsByPropertyName().size());
            assertEquals(3, beanMapping.columnsByColumnName().size());
            // id1
            SqlColumnMapping id1Mapping = beanMapping.getColumnByPropertyName("id1");
            assertNotNull(id1Mapping);
            assertTrue(id1Mapping.isPrimaryKey());
            // id2
            SqlColumnMapping id2Mapping = beanMapping.getColumnByPropertyName("id2");
            assertNotNull(id2Mapping);
            assertTrue(id2Mapping.isPrimaryKey());
            // get one primary key
            SqlColumnMapping primaryMapping = beanMapping.primaryKey();
            assertNotNull(primaryMapping);
            if (!primaryMapping.equals(id1Mapping)) {
                assertEquals(primaryMapping, id2Mapping);
            }
        }
    }

    private void testPersonMapping(@Nonnull ObjectMeta personMeta, @Nonnull SqlBeanMapping personMapping) {
        // bean
        assertEquals(personMeta, personMapping.beanMeta());
        // columns
        assertEquals(5, personMapping.columnsByPropertyName().size());
        assertEquals(5, personMapping.columnsByColumnName().size());
        // id
        SqlColumnMapping idByPropertyName = personMapping.getColumnByPropertyName("id");
        SqlColumnMapping idByColumnName = personMapping.getColumnByColumnName("ID");
        assertEquals(idByPropertyName, idByColumnName);
        assertNotNull(idByPropertyName);
        assertEquals("ID", idByPropertyName.columnName());
        assertNotNull(idByColumnName);
        assertEquals("id", idByColumnName.propertyName());
        assertEquals(personMeta.getProperty("id"), idByPropertyName.propertyMeta());
        assertTrue(idByPropertyName.isPrimaryKey());
        assertTrue(idByPropertyName.isAutoGenerated());
        assertSame(personMapping, idByPropertyName.owner());
        // id is primary key
        assertEquals(idByPropertyName, personMapping.primaryKey());
        // firstName
        SqlColumnMapping firstNameByPropertyName = personMapping.getColumnByPropertyName("firstName");
        SqlColumnMapping firstNameByColumnName = personMapping.getColumnByColumnName("FIRST_NAME");
        assertEquals(firstNameByPropertyName, firstNameByColumnName);
        assertNotNull(firstNameByPropertyName);
        assertEquals("FIRST_NAME", firstNameByPropertyName.columnName());
        assertNotNull(firstNameByColumnName);
        assertEquals("firstName", firstNameByColumnName.propertyName());
        assertEquals(personMeta.getProperty("firstName"), firstNameByPropertyName.propertyMeta());
        assertFalse(firstNameByPropertyName.isPrimaryKey());
        assertFalse(firstNameByPropertyName.isAutoGenerated());
        assertSame(personMapping, firstNameByPropertyName.owner());
        // lastName
        SqlColumnMapping lastNameByPropertyName = personMapping.getColumnByPropertyName("lastName");
        SqlColumnMapping lastNameByColumnName = personMapping.getColumnByColumnName("FAMILY_NAME");
        assertEquals(lastNameByPropertyName, lastNameByColumnName);
        assertNotNull(lastNameByPropertyName);
        assertEquals("FAMILY_NAME", lastNameByPropertyName.columnName());
        assertNotNull(lastNameByColumnName);
        assertEquals("lastName", lastNameByColumnName.propertyName());
        assertEquals(personMeta.getProperty("lastName"), lastNameByPropertyName.propertyMeta());
        assertFalse(lastNameByPropertyName.isPrimaryKey());
        assertFalse(lastNameByPropertyName.isAutoGenerated());
        assertSame(personMapping, lastNameByPropertyName.owner());
        // password
        SqlColumnMapping passwordByPropertyName = personMapping.getColumnByPropertyName("password");
        SqlColumnMapping passwordByColumnName = personMapping.getColumnByColumnName("PASSWORD");
        assertEquals(passwordByPropertyName, passwordByColumnName);
        assertNotNull(passwordByPropertyName);
        assertEquals("PASSWORD", passwordByPropertyName.columnName());
        assertNotNull(passwordByColumnName);
        assertEquals("password", passwordByColumnName.propertyName());
        assertEquals(personMeta.getProperty("password"), passwordByPropertyName.propertyMeta());
        assertFalse(passwordByPropertyName.isPrimaryKey());
        assertFalse(passwordByPropertyName.isAutoGenerated());
        assertSame(personMapping, passwordByPropertyName.owner());
        // gender
        SqlColumnMapping genderByPropertyName = personMapping.getColumnByPropertyName("gender");
        SqlColumnMapping genderByColumnName = personMapping.getColumnByColumnName("GENDER");
        assertEquals(genderByPropertyName, genderByColumnName);
        assertNotNull(genderByPropertyName);
        assertEquals("GENDER", genderByPropertyName.columnName());
        assertNotNull(genderByColumnName);
        assertEquals("gender", genderByColumnName.propertyName());
        assertEquals(personMeta.getProperty("gender"), genderByPropertyName.propertyMeta());
        assertFalse(genderByPropertyName.isPrimaryKey());
        assertFalse(genderByPropertyName.isAutoGenerated());
        assertSame(personMapping, genderByPropertyName.owner());
        // no internal field
        assertNull(personMapping.getColumnByPropertyName("internalField"));
        assertNull(personMapping.getColumnByColumnName("INTERNAL_FIELD"));
    }

    @SqlTable
    @Data
    public static class Person {
        @SqlColumn(primary = true, autoGenerated = true)
        private Long id;
        @SqlColumn
        private String firstName;
        @SqlColumn(value = "FAMILY_NAME")
        private String lastName;
        @SqlColumn
        private String password;
        @SqlColumn
        private String gender;

        private String internalField;
    }

    @SqlTable("PERSON_EXTENSION")
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PersonExt extends Person {
    }

    @Data
    public static class NoTableAnnotation {
    }

    @Data
    public static class MultiPrimaryKeys {
        @SqlColumn(primary = true)
        private Long id1;
        @SqlColumn(primary = true)
        private Long id2;
        @SqlColumn
        private String other;
    }
}