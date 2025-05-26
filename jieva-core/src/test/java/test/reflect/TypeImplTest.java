package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class TypeImplTest {

    @Test
    public void testType() throws Exception{
        class X {
            private List<String> list;
        }
        System.out.println(X.class.getDeclaredField("list").getGenericType().getClass());
        // ParameterizedType p1 = JieType.parameterized(Map.class, Jie.list(String.class, String.class));
        // Type t1 = new TypeRef<Map<String, String>>() {
        // }.type();
        // assertTrue(p1.equals(t1));
        // assertFalse(p1.equals(String.class));
        // assertEquals(p1.hashCode(), t1.hashCode());
        // assertNotEquals(p1, new TypeRef<Map<String, Integer>>() {
        // }.type());
        // ParameterizedType p2 = JieType.parameterized(
        //     ReflectTest.Inner.SubInner.class,
        //     Jie.list(String.class, String.class),
        //     JieType.parameterized(ReflectTest.Inner.class, Jie.array(ReflectTest.NumberString1.class))
        // );
        // assertEquals(p2, new TypeRef<ReflectTest.Inner<ReflectTest.NumberString1>.SubInner<String, String>>() {
        // }.type());
        // assertTrue(p1.equals(p1));
        // assertFalse(p1.equals(null));
        // assertNotEquals(p1, p2);
        // assertNotEquals(p1, String.class);
        // assertEquals(p1.toString(), new TypeRef<Map<String, String>>() {
        // }.type().toString());
        // assertEquals(p2.toString(), new TypeRef<ReflectTest.Inner<ReflectTest.NumberString1>.SubInner<String, String>>() {
        // }.type().toString());
        // assertEquals(p1.hashCode(), new TypeRef<Map<String, String>>() {
        // }.type().hashCode());
        // assertEquals(p2.hashCode(), new TypeRef<ReflectTest.Inner<ReflectTest.NumberString1>.SubInner<String, String>>() {
        // }.type().hashCode());
        // assertEquals(JieType.parameterized(List.class, Jie.array()).toString(), List.class.getName());
        // assertFalse(
        //     JieType.parameterized(List.class, Jie.array(String.class), List.class).equals(
        //         JieType.parameterized(List.class, Jie.array(String.class)))
        // );
        // assertFalse(
        //     JieType.parameterized(List.class, Jie.array(String.class), List.class).equals(
        //         new TypeRef<List<String>>() {
        //         }.type()
        //     ));
        // assertFalse(
        //     JieType.parameterized(List.class, Jie.array(String.class), List.class).equals(
        //         new TypeRef<ArrayList<String>>() {
        //         }.type()
        //     ));
        //
        // WildcardType w1 = JieType.upperBound(String.class);
        // assertEquals(w1, new TypeRef<List<? extends String>>() {
        // }.asParameterized().getActualTypeArguments()[0]);
        // WildcardType w2 = JieType.lowerBound(String.class);
        // assertEquals(w2, new TypeRef<List<? super String>>() {
        // }.asParameterized().getActualTypeArguments()[0]);
        // assertTrue(w1.equals(w1));
        // assertFalse(w1.equals(null));
        // assertNotEquals(w1, w2);
        // assertNotEquals(w1, String.class);
        // assertEquals(w1.toString(), new TypeRef<List<? extends String>>() {
        // }.asParameterized().getActualTypeArguments()[0].toString());
        // assertEquals(w2.toString(), new TypeRef<List<? super String>>() {
        // }.asParameterized().getActualTypeArguments()[0].toString());
        // assertEquals(w1.hashCode(), new TypeRef<List<? extends String>>() {
        // }.asParameterized().getActualTypeArguments()[0].hashCode());
        // assertEquals(w2.hashCode(), new TypeRef<List<? super String>>() {
        // }.asParameterized().getActualTypeArguments()[0].hashCode());
        // WildcardType w3 = JieType.wildcard(Jie.array(), Jie.array());
        // assertEquals(w3.toString(), "?");
        // WildcardType w4 = JieType.upperBound(Object.class);
        // assertEquals(w4, new TypeRef<List<? extends Object>>() {
        // }.asParameterized().getActualTypeArguments()[0]);
        // assertEquals(w4.toString(), new TypeRef<List<? extends Object>>() {
        // }.asParameterized().getActualTypeArguments()[0].toString());
        // WildcardType w5 = JieType.wildcard(Jie.array(String.class, Integer.class), Jie.array());
        // assertEquals(w5.toString(), "? extends " + String.class.getName() + " & " + Integer.class.getName());
        // WildcardType w6 = JieType.lowerBound(null);
        // assertEquals(w6.toString(), "? super java.lang.Object");
        // assertFalse(JieType.questionMark().equals(new TypeRef<List<? extends String>>() {
        // }.asParameterized().getActualTypeArguments()[0]));
        //
        // GenericArrayType g1 = JieType.array(JieType.parameterized(List.class, Jie.array(String.class)));
        // assertEquals(g1, new TypeRef<List<String>[]>() {
        // }.type());
        // assertTrue(g1.equals(g1));
        // assertFalse(g1.equals(null));
        // assertNotEquals(g1, w2);
        // assertNotEquals(g1, String.class);
        // assertEquals(g1.toString(), new TypeRef<List<String>[]>() {
        // }.type().toString());
        // assertEquals(g1.hashCode(), new TypeRef<List<String>[]>() {
        // }.type().hashCode());
        // assertEquals(JieType.array(String.class).toString(), String.class.getName() + "[]");
        //
        // Type other = JieType.other();
        // assertEquals(other.getTypeName(), "Hello, Jieva!");
        // assertTrue(other.equals(other));
        // assertFalse(other.equals(null));
        // assertNotEquals(other, w2);
        // assertNotEquals(other, String.class);
        // assertEquals(other.hashCode(), 1);
    }
}
