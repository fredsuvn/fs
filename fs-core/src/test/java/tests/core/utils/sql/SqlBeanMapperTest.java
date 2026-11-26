package tests.core.utils.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.utils.sql.SqlBeanMapper;
import space.sunqian.fs.utils.sql.SqlBeanMapping;
import space.sunqian.fs.utils.sql.SqlColumnInfo;
import space.sunqian.fs.utils.sql.SqlPropertyMapping;
import space.sunqian.fs.utils.sql.SqlRuntimeException;
import space.sunqian.fs.utils.sql.SqlTableInfo;
import space.sunqian.fs.utils.sql.annotation.SqlColumn;
import space.sunqian.fs.utils.sql.annotation.SqlTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlBeanMapperTest {

    @Test
    public void testNameMapper() {
        {
            // to column name
            NameMapper nameMapper = SqlBeanMapper.defaultToColumnNameMapper();
            assertEquals("SOME_COLUMN", nameMapper.map("someColumn"));
            assertEquals("SOME_COLUMN", nameMapper.map("SomeColumn"));
            assertEquals("SOME_COLUMN_123", nameMapper.map("someColumn123"));
            assertEquals("SOME_COLUMN_123", nameMapper.map("SomeColumn123"));
        }
        {
            // to property name
            NameMapper nameMapper = SqlBeanMapper.defaultToPropertyNameMapper();
            assertEquals("someColumn", nameMapper.map("SOME_COLUMN"));
            assertEquals("someColumn", nameMapper.map("some_column"));
            assertEquals("someColumn", nameMapper.map("Some_Column"));
            assertEquals("someColumn123", nameMapper.map("SOME_COLUMN_123"));
            assertEquals("someColumn123", nameMapper.map("some_column_123"));
            assertEquals("someColumn123", nameMapper.map("Some_Column_123"));
        }
        {
            // to table name
            NameMapper nameMapper = SqlBeanMapper.defaultToTableNameMapper();
            assertEquals("SOME_TABLE", nameMapper.map("a.b.c.SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.b.c.Xxx$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.b.c.Xxx$Yyy$Zzz$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.Xxx$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("a.Xxx$Yyy$Zzz$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("Xxx$SomeTable"));
            assertEquals("SOME_TABLE", nameMapper.map("Xxx$Yyy$Zzz$SomeTable"));
            assertEquals("SOME_TABLE_123", nameMapper.map("a.b.c.SomeTable123"));
            assertEquals("SOME_TABLE_123", nameMapper.map("a.b.c.Xxx$SomeTable123"));
            assertEquals("SOME_TABLE_123", nameMapper.map("a.b.c.Xxx$Yyy$Zzz$SomeTable123"));
        }
    }

    @Test
    public void testDefaultMapping() {
        SqlBeanMapper sqlBeanMapper = SqlBeanMapper.defaultMapper();
        {
            // Person
            ObjectMeta personType = ObjectMeta.of(Person.class);
            SqlBeanMapping personMapping = sqlBeanMapper.toBeanMapping(personType);
            // name
            assertEquals("PERSON", personMapping.targetName());
            // test mapping
            testPersonMapping(personType, personMapping);
        }
        {
            // PersonExt
            ObjectMeta personType = ObjectMeta.of(PersonExt.class);
            SqlBeanMapping personMapping = sqlBeanMapper.toBeanMapping(personType);
            // name
            assertEquals("PERSON_EXTENSION", personMapping.targetName());
            // test mapping
            testPersonMapping(personType, personMapping);
        }
        {
            // No table annotation
            ObjectMeta beanType = ObjectMeta.of(NoTableAnnotation.class);
            SqlBeanMapping beanMapping = sqlBeanMapper.toBeanMapping(beanType);
            // name
            assertEquals("NO_TABLE_ANNOTATION", beanMapping.targetName());
            // 0 column
            assertEquals(0, beanMapping.propertyMappingCount());
        }
        {
            // Multi primary keys
            ObjectMeta beanType = ObjectMeta.of(MultiPrimaryKeys.class);
            SqlBeanMapping beanMapping = sqlBeanMapper.toBeanMapping(beanType);
            // name
            assertEquals("MULTI_PRIMARY_KEYS", beanMapping.targetName());
            assertEquals(3, beanMapping.propertyMappingCount());
            // id1
            SqlPropertyMapping id1Mapping = beanMapping.getPropertyMappingByPropertyName("id1");
            assertNotNull(id1Mapping);
            assertTrue(id1Mapping.isPrimaryKey());
            // id2
            SqlPropertyMapping id2Mapping = beanMapping.getPropertyMappingByPropertyName("id2");
            assertNotNull(id2Mapping);
            assertTrue(id2Mapping.isPrimaryKey());
            // get one primary key
            SqlPropertyMapping primaryMapping = beanMapping.primaryKey();
            assertNotNull(primaryMapping);
            if (!primaryMapping.equals(id1Mapping)) {
                assertEquals(primaryMapping, id2Mapping);
            }
        }
    }

    @Test
    public void testCustomMapping() {
        {
            // XTable
            SqlBeanMapper sqlBeanMapper = SqlBeanMapper.of(
                bt -> new SqlTableInfo() {
                    @Override
                    public @Nonnull String name() {
                        return "X_TABLE";
                    }
                },
                pt -> {
                    if (!"firstColumn".equals(pt.name())) {
                        return null;
                    }
                    return new SqlColumnInfo() {
                        @Override
                        public @Nonnull String name() {
                            return "FIRST_COLUMN";
                        }

                        @Override
                        public boolean isPrimaryKey() {
                            return false;
                        }

                        @Override
                        public boolean isAutoGenerated() {
                            return false;
                        }
                    };
                },
                null
            );
            SqlBeanMapping xMapping = sqlBeanMapper.toBeanMapping(ObjectMeta.of(XTable.class));
            assertEquals("X_TABLE", xMapping.targetName());
            assertEquals(1, xMapping.propertyMappingCount());
            // XTable has only one property
            SqlPropertyMapping firstColumnMapping = xMapping.getPropertyMappingByPropertyName("firstColumn");
            assertNotNull(firstColumnMapping);
            assertEquals("FIRST_COLUMN", firstColumnMapping.columnName());
            assertFalse(firstColumnMapping.isPrimaryKey());
            assertFalse(firstColumnMapping.isAutoGenerated());
            assertEquals(firstColumnMapping, xMapping.getPropertyMappingByColumnName("FIRST_COLUMN"));
        }
        {
            // Cannot resolve table
            SqlBeanMapper sqlBeanMapper = SqlBeanMapper.of(
                bt -> null,
                pt -> null,
                null
            );
            assertThrows(SqlRuntimeException.class, () -> sqlBeanMapper.toBeanMapping(ObjectMeta.of(XTable.class)));
        }
    }

    private void testPersonMapping(@Nonnull ObjectMeta personType, @Nonnull SqlBeanMapping personMapping) {
        // bean
        assertEquals(personType, personMapping.beanType());
        // columns
        assertEquals(5, personMapping.propertyMappingCount());
        // id
        SqlPropertyMapping idByPropertyName = personMapping.getPropertyMappingByPropertyName("id");
        SqlPropertyMapping idByColumnName = personMapping.getPropertyMappingByColumnName("ID");
        assertEquals(idByPropertyName, idByColumnName);
        assertNotNull(idByPropertyName);
        assertEquals("ID", idByPropertyName.columnName());
        assertEquals(personType.getProperty("id"), idByPropertyName.propertyType());
        assertTrue(idByPropertyName.isPrimaryKey());
        assertTrue(idByPropertyName.isAutoGenerated());
        assertSame(personMapping, idByPropertyName.owner());
        // id is primary key
        assertEquals(idByPropertyName, personMapping.primaryKey());
        // firstName
        SqlPropertyMapping firstNameByPropertyName = personMapping.getPropertyMappingByPropertyName("firstName");
        SqlPropertyMapping firstNameByColumnName = personMapping.getPropertyMappingByColumnName("FIRST_NAME");
        assertEquals(firstNameByPropertyName, firstNameByColumnName);
        assertNotNull(firstNameByPropertyName);
        assertEquals("FIRST_NAME", firstNameByPropertyName.columnName());
        assertEquals(personType.getProperty("firstName"), firstNameByPropertyName.propertyType());
        assertFalse(firstNameByPropertyName.isPrimaryKey());
        assertFalse(firstNameByPropertyName.isAutoGenerated());
        assertSame(personMapping, firstNameByPropertyName.owner());
        // lastName
        SqlPropertyMapping lastNameByPropertyName = personMapping.getPropertyMappingByPropertyName("lastName");
        SqlPropertyMapping lastNameByColumnName = personMapping.getPropertyMappingByColumnName("FAMILY_NAME");
        assertEquals(lastNameByPropertyName, lastNameByColumnName);
        assertNotNull(lastNameByPropertyName);
        assertEquals("FAMILY_NAME", lastNameByPropertyName.columnName());
        assertEquals(personType.getProperty("lastName"), lastNameByPropertyName.propertyType());
        assertFalse(lastNameByPropertyName.isPrimaryKey());
        assertFalse(lastNameByPropertyName.isAutoGenerated());
        assertSame(personMapping, lastNameByPropertyName.owner());
        // password
        SqlPropertyMapping passwordByPropertyName = personMapping.getPropertyMappingByPropertyName("password");
        SqlPropertyMapping passwordByColumnName = personMapping.getPropertyMappingByColumnName("PASSWORD");
        assertEquals(passwordByPropertyName, passwordByColumnName);
        assertNotNull(passwordByPropertyName);
        assertEquals("PASSWORD", passwordByPropertyName.columnName());
        assertEquals(personType.getProperty("password"), passwordByPropertyName.propertyType());
        assertFalse(passwordByPropertyName.isPrimaryKey());
        assertFalse(passwordByPropertyName.isAutoGenerated());
        assertSame(personMapping, passwordByPropertyName.owner());
        // gender
        SqlPropertyMapping genderByPropertyName = personMapping.getPropertyMappingByPropertyName("gender");
        SqlPropertyMapping genderByColumnName = personMapping.getPropertyMappingByColumnName("GENDER");
        assertEquals(genderByPropertyName, genderByColumnName);
        assertNotNull(genderByPropertyName);
        assertEquals("GENDER", genderByPropertyName.columnName());
        assertEquals(personType.getProperty("gender"), genderByPropertyName.propertyType());
        assertFalse(genderByPropertyName.isPrimaryKey());
        assertFalse(genderByPropertyName.isAutoGenerated());
        assertSame(personMapping, genderByPropertyName.owner());
        // no internal field
        assertNull(personMapping.getPropertyMappingByPropertyName("internalField"));
        assertNull(personMapping.getPropertyMappingByColumnName("INTERNAL_FIELD"));
    }

    @SqlTable
    @Data
    public static class Person {
        @SqlColumn(primaryKey = true, autoGenerated = true)
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
        @SqlColumn(primaryKey = true)
        private Long id1;
        @SqlColumn(primaryKey = true)
        private Long id2;
        @SqlColumn
        private String other;
    }

    @Data
    public static class XTable {
        private String firstColumn;
        private String noColumn;
    }
}