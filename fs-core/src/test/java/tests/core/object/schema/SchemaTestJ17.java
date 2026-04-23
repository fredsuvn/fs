package tests.core.object.schema;

import internal.annotations.J17Only;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.schema.DataSchemaException;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Only
public class SchemaTestJ17 {

    private InfoRecord<Integer> infoRecord;
    private ObjectSchema recordSchema;

    @BeforeEach
    public void setUp() throws Exception {
        infoRecord = new InfoRecord<>("hello", 18, List.of("friend1", "friend2"), 6);
        recordSchema = ObjectSchema.parse(new TypeRef<InfoRecord<Integer>>() {}.type());
    }

    @Test
    public void testRecordSchemaBasicProperties() {
        assertTrue(recordSchema.isObjectSchema());
        assertFalse(recordSchema.isMapSchema());
        assertEquals(4, recordSchema.properties().size());
    }

    @Test
    public void testRecordSchemaPropertyName() throws Exception {
        ObjectProperty recordName = recordSchema.properties().get("name");
        verifyRecordProperty(recordName, "name", String.class, "hello", InfoRecord.class.getMethod("name"));
    }

    @Test
    public void testRecordSchemaPropertyAge() throws Exception {
        ObjectProperty recordAge = recordSchema.properties().get("age");
        verifyRecordProperty(recordAge, "age", int.class, 18, InfoRecord.class.getMethod("age"));
    }

    @Test
    public void testRecordSchemaPropertyFriends() throws Exception {
        ObjectProperty recordFriends = recordSchema.properties().get("friends");
        List<String> expectedFriends = List.of("friend1", "friend2");
        verifyRecordProperty(recordFriends, "friends", new TypeRef<List<String>>() {}.type(), expectedFriends, InfoRecord.class.getMethod("friends"));
    }

    @Test
    public void testRecordSchemaPropertyT() throws Exception {
        ObjectProperty recordT = recordSchema.properties().get("t");
        verifyRecordProperty(recordT, "t", InfoRecord.class.getTypeParameters()[0], 6, InfoRecord.class.getMethod("t"));
    }

    @Test
    public void testRecordSchemaCopyProperties() {
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
    }

    @Test
    public void testRecordSchemaErrorType() {
        assertThrows(DataSchemaException.class, () -> ObjectSchema.parse(InfoRecord.class.getTypeParameters()[0]));
    }

    @Test
    public void testAnnotation() {
        ObjectSchema schema = ObjectSchema.parse(ForAnnotation.class);
        testAnnotationProp1(schema);
        testAnnotationProp2(schema);
        testAnnotationProp3(schema);
        testAnnotationProp4(schema);
    }

    private void testAnnotationProp1(ObjectSchema schema) {
        ObjectProperty prop1 = schema.getProperty("prop1");
        assertNotNull(prop1);
        Nonnull a1 = prop1.getAnnotation(Nonnull.class);
        assertNotNull(a1);
        assertEquals(Nonnull.class, a1.annotationType());
        assertNull(prop1.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop1.fieldAnnotations());
        assertEquals(ListKit.list(a1), prop1.getterAnnotations());
        assertEquals(Collections.emptyList(), prop1.setterAnnotations());
    }

    private void testAnnotationProp2(ObjectSchema schema) {
        ObjectProperty prop2 = schema.getProperty("prop2");
        assertNotNull(prop2);
        Nullable a2 = prop2.getAnnotation(Nullable.class);
        assertNotNull(a2);
        assertEquals(Nullable.class, a2.annotationType());
        assertNull(prop2.getAnnotation(Nonnull.class));
        assertEquals(Collections.emptyList(), prop2.fieldAnnotations());
        assertEquals(ListKit.list(a2), prop2.getterAnnotations());
        assertEquals(Collections.emptyList(), prop2.setterAnnotations());
    }

    private void testAnnotationProp3(ObjectSchema schema) {
        ObjectProperty prop3 = schema.getProperty("prop3");
        assertNotNull(prop3);
        Nonnull a3 = prop3.getAnnotation(Nonnull.class);
        assertNotNull(a3);
        assertEquals(Nonnull.class, a3.annotationType());
        assertNull(prop3.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop3.fieldAnnotations());
        assertEquals(ListKit.list(a3), prop3.getterAnnotations());
        assertEquals(Collections.emptyList(), prop3.setterAnnotations());
    }

    private void testAnnotationProp4(ObjectSchema schema) {
        ObjectProperty prop4 = schema.getProperty("prop4");
        assertNotNull(prop4);
        assertNull(prop4.getAnnotation(Nonnull.class));
        assertNull(prop4.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop4.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop4.getterAnnotations());
        assertEquals(Collections.emptyList(), prop4.setterAnnotations());
    }

    private void verifyRecordProperty(ObjectProperty property, String expectedName, Object expectedType, Object expectedValue, Method expectedGetter) throws Exception {
        assertEquals(expectedName, property.name());
        assertEquals(expectedType, property.type());
        assertTrue(property.isReadable());
        assertFalse(property.isWritable());
        assertEquals(expectedGetter, property.getterMethod());
        assertEquals(expectedValue, property.getter().invoke(infoRecord));
        assertNull(property.field());
        assertNull(property.setter());
        assertNull(property.setterMethod());
    }

    public record InfoRecord<T>(String name, int age, List<String> friends, T t) {
    }

    public record ForAnnotation(
        @Nonnull String prop1,
        @Nullable String prop2,
        String prop3,
        String prop4
    ) {

        @Nonnull
        public String prop3() {
            return prop3;
        }
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
