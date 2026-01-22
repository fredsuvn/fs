package tests.object.schema;

import internal.test.J17Only;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.Fs;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.reflect.TypeRef;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Only
public class SchemaTestJ17 {

    @Test
    public void testRecordSchema() throws Exception {
        InfoRecord<Integer> infoRecord = new InfoRecord<>("hello", 18, List.of("friend1", "friend2"), 6);
        ObjectSchema recordSchema = ObjectSchema.parse(new TypeRef<InfoRecord<Integer>>() {}.type());
        assertTrue(recordSchema.isObjectSchema());
        assertFalse(recordSchema.isMapSchema());
        assertEquals(4, recordSchema.properties().size());
        ObjectProperty recordName = recordSchema.properties().get("name");
        assertEquals("name", recordName.name());
        assertEquals(String.class, recordName.type());
        assertTrue(recordName.isReadable());
        assertFalse(recordName.isWritable());
        assertEquals(
            InfoRecord.class.getMethod("name"),
            recordName.getterMethod()
        );
        assertEquals(
            "hello",
            recordName.getter().invoke(infoRecord)
        );
        assertNull(recordName.field());
        assertNull(recordName.setter());
        assertNull(recordName.setterMethod());
        ObjectProperty recordAge = recordSchema.properties().get("age");
        assertEquals("age", recordAge.name());
        assertEquals(int.class, recordAge.type());
        assertTrue(recordAge.isReadable());
        assertFalse(recordAge.isWritable());
        assertEquals(
            InfoRecord.class.getMethod("age"),
            recordAge.getterMethod()
        );
        assertEquals(
            18,
            recordAge.getter().invoke(infoRecord)
        );
        assertNull(recordAge.field());
        assertNull(recordAge.setter());
        assertNull(recordAge.setterMethod());
        ObjectProperty recordFriends = recordSchema.properties().get("friends");
        assertEquals("friends", recordFriends.name());
        assertEquals(new TypeRef<List<String>>() {}.type(), recordFriends.type());
        assertTrue(recordFriends.isReadable());
        assertFalse(recordFriends.isWritable());
        assertEquals(
            InfoRecord.class.getMethod("friends"),
            recordFriends.getterMethod()
        );
        assertEquals(
            List.of("friend1", "friend2"),
            recordFriends.getter().invoke(infoRecord)
        );
        assertNull(recordFriends.field());
        assertNull(recordFriends.setter());
        assertNull(recordFriends.setterMethod());
        ObjectProperty recordT = recordSchema.properties().get("t");
        assertEquals("t", recordT.name());
        assertEquals(InfoRecord.class.getTypeParameters()[0], recordT.type());
        assertTrue(recordT.isReadable());
        assertFalse(recordT.isWritable());
        assertEquals(
            InfoRecord.class.getMethod("t"),
            recordT.getterMethod()
        );
        assertEquals(
            6,
            recordT.getter().invoke(infoRecord)
        );
        assertNull(recordT.field());
        assertNull(recordT.setter());
        assertNull(recordT.setterMethod());
        InfoObject<Long> infoObject = new InfoObject<>();
        Fs.copyProperties(
            infoRecord,
            new TypeRef<InfoRecord<Integer>>() {}.type(),
            infoObject,
            new TypeRef<InfoObject<Long>>() {}.type()
        );
        assertEquals(
            new InfoObject<>("hello", 18, List.of("friend1", "friend2"), 6L),
            infoObject
        );
        assertThrows(DataSchemaException.class, () -> ObjectSchema.parse(InfoRecord.class.getTypeParameters()[0]));
    }

    public record InfoRecord<T>(String name, int age, List<String> friends, T t) {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class InfoObject<T> {
        public String name;
        public int age;
        public List<String> friends;
        public T t;
    }
}
