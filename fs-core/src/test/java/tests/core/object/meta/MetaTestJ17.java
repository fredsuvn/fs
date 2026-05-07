package tests.core.object.meta;

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
import space.sunqian.fs.object.meta.DataMetaException;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.PropertyMeta;
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
public class MetaTestJ17 {

    private InfoRecord<Integer> infoRecord;
    private ObjectMeta recordMeta;

    @BeforeEach
    public void setUp() throws Exception {
        infoRecord = new InfoRecord<>("hello", 18, List.of("friend1", "friend2"), 6);
        recordMeta = ObjectMeta.of(new TypeRef<InfoRecord<Integer>>() {}.type());
    }

    @Test
    public void testRecordMetaBasicProperties() {
        assertTrue(recordMeta.isObjectMeta());
        assertFalse(recordMeta.isMapMeta());
        assertEquals(4, recordMeta.properties().size());
    }

    @Test
    public void testRecordMetaPropertyName() throws Exception {
        PropertyMeta recordName = recordMeta.properties().get("name");
        verifyRecordProperty(recordName, "name", String.class, "hello", InfoRecord.class.getMethod("name"));
    }

    @Test
    public void testRecordMetaPropertyAge() throws Exception {
        PropertyMeta recordAge = recordMeta.properties().get("age");
        verifyRecordProperty(recordAge, "age", int.class, 18, InfoRecord.class.getMethod("age"));
    }

    @Test
    public void testRecordMetaPropertyFriends() throws Exception {
        PropertyMeta recordFriends = recordMeta.properties().get("friends");
        List<String> expectedFriends = List.of("friend1", "friend2");
        verifyRecordProperty(recordFriends, "friends", new TypeRef<List<String>>() {}.type(), expectedFriends, InfoRecord.class.getMethod("friends"));
    }

    @Test
    public void testRecordMetaPropertyT() throws Exception {
        PropertyMeta recordT = recordMeta.properties().get("t");
        verifyRecordProperty(recordT, "t", InfoRecord.class.getTypeParameters()[0], 6, InfoRecord.class.getMethod("t"));
    }

    @Test
    public void testRecordMetaCopyProperties() {
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
    public void testRecordMetaErrorType() {
        assertThrows(DataMetaException.class, () -> ObjectMeta.of(InfoRecord.class.getTypeParameters()[0]));
    }

    @Test
    public void testAnnotation() {
        ObjectMeta meta = ObjectMeta.of(ForAnnotation.class);
        testAnnotationProp1(meta);
        testAnnotationProp2(meta);
        testAnnotationProp3(meta);
        testAnnotationProp4(meta);
    }

    private void testAnnotationProp1(ObjectMeta meta) {
        PropertyMeta prop1 = meta.getProperty("prop1");
        assertNotNull(prop1);
        Nonnull a1 = prop1.getAnnotation(Nonnull.class);
        assertNotNull(a1);
        assertEquals(Nonnull.class, a1.annotationType());
        assertNull(prop1.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop1.fieldAnnotations());
        assertEquals(ListKit.list(a1), prop1.getterAnnotations());
        assertEquals(Collections.emptyList(), prop1.setterAnnotations());
    }

    private void testAnnotationProp2(ObjectMeta meta) {
        PropertyMeta prop2 = meta.getProperty("prop2");
        assertNotNull(prop2);
        Nullable a2 = prop2.getAnnotation(Nullable.class);
        assertNotNull(a2);
        assertEquals(Nullable.class, a2.annotationType());
        assertNull(prop2.getAnnotation(Nonnull.class));
        assertEquals(Collections.emptyList(), prop2.fieldAnnotations());
        assertEquals(ListKit.list(a2), prop2.getterAnnotations());
        assertEquals(Collections.emptyList(), prop2.setterAnnotations());
    }

    private void testAnnotationProp3(ObjectMeta meta) {
        PropertyMeta prop3 = meta.getProperty("prop3");
        assertNotNull(prop3);
        Nonnull a3 = prop3.getAnnotation(Nonnull.class);
        assertNotNull(a3);
        assertEquals(Nonnull.class, a3.annotationType());
        assertNull(prop3.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop3.fieldAnnotations());
        assertEquals(ListKit.list(a3), prop3.getterAnnotations());
        assertEquals(Collections.emptyList(), prop3.setterAnnotations());
    }

    private void testAnnotationProp4(ObjectMeta meta) {
        PropertyMeta prop4 = meta.getProperty("prop4");
        assertNotNull(prop4);
        assertNull(prop4.getAnnotation(Nonnull.class));
        assertNull(prop4.getAnnotation(Nullable.class));
        assertEquals(Collections.emptyList(), prop4.fieldAnnotations());
        assertEquals(Collections.emptyList(), prop4.getterAnnotations());
        assertEquals(Collections.emptyList(), prop4.setterAnnotations());
    }

    private void verifyRecordProperty(PropertyMeta property, String expectedName, Object expectedType, Object expectedValue, Method expectedGetter) throws Exception {
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
