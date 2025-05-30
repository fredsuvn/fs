package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AssignerTest {

    // @Test
    // public void testAssignableOther() {
    //     Type other = JieType.otherType();
    //     Class<?> classType = String.class;
    //     ParameterizedType parameterized = JieType.parameterizedType(List.class, new Type[]{String.class});
    //     WildcardType wildcard = JieType.upperWildcard(CharSequence.class);
    //     GenericArrayType arrayType = JieType.arrayType(String.class);
    //     TypeVariable<?> typeVariable = AssignTester0.class.getTypeParameters()[0];
    //
    //     assertFalse(JieType.isAssignable(other, other));
    //     assertFalse(JieType.isAssignable(other, classType));
    //     assertFalse(JieType.isAssignable(other, parameterized));
    //     assertFalse(JieType.isAssignable(other, wildcard));
    //     assertFalse(JieType.isAssignable(other, arrayType));
    //     assertFalse(JieType.isAssignable(other, typeVariable));
    //
    //     assertFalse(JieType.isAssignable(classType, other));
    //     assertTrue(JieType.isAssignable(classType, classType));
    //     assertFalse(JieType.isAssignable(classType, parameterized));
    //     assertFalse(JieType.isAssignable(classType, wildcard));
    //     assertFalse(JieType.isAssignable(classType, arrayType));
    //     assertFalse(JieType.isAssignable(classType, typeVariable));
    //
    //     assertFalse(JieType.isAssignable(parameterized, other));
    //     assertFalse(JieType.isAssignable(parameterized, classType));
    //     assertTrue(JieType.isAssignable(parameterized, parameterized));
    //     assertFalse(JieType.isAssignable(parameterized, wildcard));
    //     assertFalse(JieType.isAssignable(parameterized, arrayType));
    //     assertFalse(JieType.isAssignable(parameterized, typeVariable));
    //
    //     assertFalse(JieType.isAssignable(wildcard, other));
    //     assertFalse(JieType.isAssignable(wildcard, classType));
    //     assertFalse(JieType.isAssignable(wildcard, parameterized));
    //     assertFalse(JieType.isAssignable(wildcard, wildcard));
    //     assertFalse(JieType.isAssignable(wildcard, arrayType));
    //     assertFalse(JieType.isAssignable(wildcard, typeVariable));
    //
    //     assertFalse(JieType.isAssignable(arrayType, other));
    //     assertFalse(JieType.isAssignable(arrayType, classType));
    //     assertFalse(JieType.isAssignable(arrayType, parameterized));
    //     assertFalse(JieType.isAssignable(arrayType, wildcard));
    //     assertTrue(JieType.isAssignable(arrayType, arrayType));
    //     assertFalse(JieType.isAssignable(arrayType, typeVariable));
    //
    //     assertFalse(JieType.isAssignable(typeVariable, other));
    //     assertFalse(JieType.isAssignable(typeVariable, classType));
    //     assertFalse(JieType.isAssignable(typeVariable, parameterized));
    //     assertFalse(JieType.isAssignable(typeVariable, wildcard));
    //     assertFalse(JieType.isAssignable(typeVariable, arrayType));
    //     assertTrue(JieType.isAssignable(typeVariable, typeVariable));
    // }

    //@Test
    public void testAssignable() {
        // AssignTester0 test = new AssignTester0();
        // test.doTest();
    }

    public static class AssignTester0<
        T0,
        T1 extends Number & CharSequence,
        T2 extends T1,
        T3 extends T2,
        T4 extends String,
        T5 extends List<CharSequence>,
        T6 extends CharSequence,
        T7 extends T6
        > {

        // Class
        private Object f0 = null;
        private CharSequence f1 = null;
        private String f2 = null;
        private List f3 = null;
        // Parameterized
        private List<CharSequence> f4 = null;
        private List<String> f5 = null;
        private List<? extends CharSequence> f6 = null;
        private List<? extends String> f7 = null;
        private List<? super CharSequence> f8 = null;
        private List<? super String> f9 = null;
        private List<T1> f10 = null;
        private List<T3> f11 = null;
        private List<? extends T1> f12 = null;
        private List<? extends T3> f13 = null;
        private List<? super T1> f14 = null;
        private List<? extends T4> f15 = null;
        private List<? extends CharSequence[]> f34 = null;
        private List<? super CharSequence[]> f35 = null;
        private List<? extends List<? super CharSequence>[]> f36 = null;
        private List<? super List<? super CharSequence>[]> f37 = null;
        private List<? extends List<? extends CharSequence>[]> f38 = null;
        private List<? super List<? extends CharSequence>[]> f39 = null;
        private List<? extends List<String>> f40 = null;
        private List<? super List<String>> f41 = null;
        private ArrayList<String> f42 = null;
        private List<List<? super CharSequence>[]> f43 = null;
        private List<List<CharSequence>[]> f45 = null;
        private List<? extends List<CharSequence>[]> f46 = null;
        private List<? super List<CharSequence>[]> f47 = null;
        private List<Object> f48 = null;
        private List<List<String>> f49 = null;
        private List<? extends T6> f51 = null;
        private List<? super T6> f52 = null;
        private List<T6> f53 = null;
        private List<? extends T7> f54 = null;
        private List<? super T7> f55 = null;
        private List<T7> f56 = null;
        // TypeVariable
        private T0 f16 = null;
        private T1 f17 = null;
        private T2 f18 = null;
        private T3 f19 = null;
        private T4 f20 = null;
        private T5 f44 = null;
        private T6 f50;
        // Array
        private CharSequence[] f21 = null;
        private String[] f22 = null;
        private List<CharSequence>[] f23 = null;
        private List<String>[] f24 = null;
        private List<? extends CharSequence>[] f25 = null;
        private List<? extends String>[] f26 = null;
        private List<? super CharSequence>[] f27 = null;
        private List<? super String>[] f28 = null;
        private T1[] f29 = null;
        private T3[] f30 = null;
        private List[] f31 = null;
        private T4[] f32 = null;
        private List<T4>[] f33 = null;
        private T6[] f57;


        public void doTest() {
            // Class
            testAssign("f1", "f1", true);
            testAssign("f1", "f2", true);
            testAssign("f1", "f3", false);
            testAssign("f0", "f4", true);
            testAssign("f1", "f4", false);
            testAssign("f0", "f16", true);
            testAssign("f1", "f16", false);
            testAssign("f0", "f21", true);
            testAssign("f1", "f21", false);
            testAssign("f2", "f20", true);
            testAssign("f0", "f25", true);
            testAssign("f1", "f25", false);

            // Parameterized
            testAssign("f4", "f5", false);
            testAssign("f5", "f4", false);
            testAssign("f4", "f6", false);
            testAssign("f6", "f4", true);
            testAssign("f6", "f7", true);
            testAssign("f7", "f6", false);
            testAssign("f4", "f8", false);
            testAssign("f8", "f4", true);
            testAssign("f10", "f11", false);
            testAssign("f10", "f12", false);
            testAssign("f12", "f10", true);
            testAssign("f12", "f13", true);
            testAssign("f13", "f12", false);
            testAssign("f10", "f14", false);
            testAssign("f14", "f10", true);
            testAssign("f4", "f0", false);
            testAssign("f4", "f16", false);
            testAssign("f4", "f21", false);
            testAssign("f4", "f25", false);
            testAssign("f15", "f9", false);
            testAssign("f9", "f15", false);
            testAssign("f9", "f14", false);
            testAssign("f5", "f42", true);
            testAssign("f7", "f42", true);
            testAssign("f8", "f42", false);
            testAssign("f9", "f42", true);
            testAssign("f4", "f44", true);
            testAssign("f44", "f4", false);
            testAssign("f6", "f51", true);
            testAssign("f6", "f52", false);
            testAssign("f6", "f53", true);
            testAssign("f8", "f51", false);
            testAssign("f8", "f52", false);
            testAssign("f8", "f53", false);
            testAssign("f6", "f54", true);
            testAssign("f6", "f55", false);
            testAssign("f6", "f56", true);
            testAssign("f8", "f54", false);
            testAssign("f8", "f55", false);
            testAssign("f8", "f56", false);
            testAssign("f6", "f48", false);
            testAssign("f42", "f5", false);
            doTestAdd("f45", "f23", true);
            doTestAdd("f46", "f23", false);
            doTestAdd("f47", "f23", true);
            doTestAdd("f48", "f16", true);
            doTestAdd("f34", "f50", false);
            doTestAdd("f35", "f50", false);
            doTestParam("f48", "f6", true);
            doTestParam("f48", "f8", true);
            doTestParam("f48", "f10", true);
            doTestParam("f48", "f12", true);
            doTestParam("f48", "f14", true);
            doTestParam("f49", "f40", true);
            doTestParam("f49", "f41", false);
            doTestParam("f6", "f51", false);
            doTestParam("f6", "f52", false);
            doTestParam("f6", "f53", false);
            doTestParam("f8", "f51", true);
            doTestParam("f8", "f52", false);
            doTestParam("f8", "f53", true);
            ParameterizedType p1 = JieType.parameterizedType(List.class, new Type[]{String.class});
            ParameterizedType p2 = JieType.parameterizedType(List.class, new Type[]{String.class, String.class});
            assertFalse(JieType.isAssignable(p1, p2));

            // TypeVariable
            testAssign("f16", "f16", true);
            testAssign("f17", "f19", true);
            testAssign("f19", "f17", false);
            testAssign("f20", "f2", false);
            testAssign("f20", "f4", false);
            testAssign("f16", "f23", false);

            // Array
            testAssign("f21", "f0", false);
            testAssign("f21", "f4", false);
            testAssign("f21", "f16", false);
            testAssign("f21", "f22", true);
            testAssign("f22", "f21", false);
            testAssign("f23", "f31", true);
            testAssign("f31", "f23", true);
            testAssign("f23", "f24", false);
            testAssign("f25", "f26", true);
            testAssign("f26", "f25", false);
            testAssign("f27", "f28", false);
            testAssign("f28", "f27", true);
            testAssign("f25", "f27", false);
            testAssign("f27", "f25", false);
            testAssign("f29", "f30", true);
            testAssign("f30", "f29", false);
            testAssign("f32", "f24", false);
            testAssign("f26", "f32", false);
            testAssign("f27", "f33", false);
            testAssign("f23", "f44", false);
            testAssign("f23", "f4", false);
            testAssign("f23", "f4", false);
            testAssign("f57", "f21", false);
            testAssign("f21", "f57", true);
            GenericArrayType a1 = JieType.arrayType(String.class);
            assertTrue(JieType.isAssignable(a1, String[].class));
            assertTrue(JieType.isAssignable(String[].class, a1));

            // Wildcard
            // add
            doTestAdd("f6", "f1", false);
            doTestAdd("f6", "f2", false);
            doTestAdd("f7", "f2", false);
            doTestAdd("f8", "f1", true);
            doTestAdd("f8", "f2", true);
            doTestAdd("f9", "f1", false);
            doTestAdd("f9", "f2", true);
            doTestAdd("f6", "f17", false);
            doTestAdd("f7", "f20", false);
            doTestAdd("f10", "f17", true);
            doTestAdd("f14", "f17", true);
            doTestAdd("f12", "f17", false);
            doTestAdd("f14", "f0", false);
            doTestAdd("f6", "f21", false);
            doTestAdd("f34", "f21", false);
            doTestAdd("f35", "f21", true);
            doTestAdd("f36", "f27", false);
            doTestAdd("f37", "f27", true);
            doTestAdd("f38", "f25", false);
            doTestAdd("f39", "f25", true);
            doTestAdd("f40", "f42", false);
            doTestAdd("f41", "f42", true);
            // add(get)
            doTestParam("f4", "f6", true);
            doTestParam("f4", "f7", true);
            doTestParam("f4", "f8", false);
            doTestParam("f4", "f9", false);
            doTestParam("f5", "f6", false);
            doTestParam("f5", "f7", true);
            doTestParam("f5", "f8", false);
            doTestParam("f5", "f9", false);
            doTestParam("f10", "f12", true);
            doTestParam("f10", "f13", true);
            doTestParam("f10", "f14", false);
            doTestParam("f10", "f15", false);
            doTestParam("f11", "f12", false);
            doTestParam("f11", "f13", true);
            doTestParam("f11", "f14", false);
            doTestParam("f11", "f15", false);
            doTestParam("f43", "f36", true);
            doTestParam("f43", "f37", false);
            doTestParam("f6", "f6", false);
            doTestParam("f6", "f7", false);
            doTestParam("f6", "f8", false);
            doTestParam("f6", "f9", false);
            doTestParam("f7", "f6", false);
            doTestParam("f7", "f7", false);
            doTestParam("f7", "f8", false);
            doTestParam("f7", "f9", false);
            doTestParam("f8", "f6", true);
            doTestParam("f8", "f7", true);
            doTestParam("f8", "f8", false);
            doTestParam("f8", "f9", false);
            doTestParam("f9", "f6", false);
            doTestParam("f9", "f7", true);
            doTestParam("f9", "f8", false);
            doTestParam("f9", "f9", false);
        }

        private void testAssign(String assigned, String assignee, boolean isAssignable) {
            try {
                Field assignedField = AssignTester0.class.getDeclaredField(assigned);
                Type assignedType = assignedField.getGenericType();
                Field assigneeField = AssignTester0.class.getDeclaredField(assignee);
                Type assigneeType = assigneeField.getGenericType();
                assertEquals(JieType.isAssignable(assignedType, assigneeType), isAssignable,
                    String.format(
                        "Assign error: %s: %s = %s: %s",
                        assigned,
                        assignedType.getTypeName(),
                        assignee,
                        assigneeType.getTypeName()
                    ));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void doTestAdd(String assigned, String assignee, boolean isAssignable) {
            try {
                Field assignedField = AssignTester0.class.getDeclaredField(assigned);
                ParameterizedType assignedParamType = (ParameterizedType) assignedField.getGenericType();
                Type assignedType = assignedParamType.getActualTypeArguments()[0];
                Field assigneeField = AssignTester0.class.getDeclaredField(assignee);
                Type assigneeType = assigneeField.getGenericType();
                assertEquals(JieType.isAssignable(assignedType, assigneeType), isAssignable,
                    String.format(
                        "Assign error: (%s: %s).add(%s: %s)",
                        assigned,
                        assignedType.getTypeName(),
                        assignee,
                        assigneeType.getTypeName()
                    ));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void doTestParam(String assigned, String assignee, boolean isAssignable) {
            try {
                Field assignedField = AssignTester0.class.getDeclaredField(assigned);
                ParameterizedType assignedParamType = (ParameterizedType) assignedField.getGenericType();
                Type assignedType = assignedParamType.getActualTypeArguments()[0];
                Field assigneeField = AssignTester0.class.getDeclaredField(assignee);
                ParameterizedType assigneeParamType = (ParameterizedType) assigneeField.getGenericType();
                Type assigneeType = assigneeParamType.getActualTypeArguments()[0];
                assertEquals(JieType.isAssignable(assignedType, assigneeType), isAssignable,
                    String.format(
                        "Assign error: (%s: %s).add((%s: %s).get(0))",
                        assigned,
                        assignedType.getTypeName(),
                        assignee,
                        assigneeType.getTypeName()
                    ));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testClassAssign() {
        new ClassTester();
    }

    @Test
    public void testParamAssign() {
        new ParamTester();
    }

    @Test
    public void testWildcardAssign() {
        new WildcardTester();
    }

    public static class ClassTester<
        T0,
        T1 extends String,
        T2 extends T1,
        T3 extends Number & CharSequence,
        T4 extends T3
        > {

        {
            // Class = Class
            String f1 = null;
            String f2 = f1;
            testAssign(String.class, String.class, true);
            Object f3 = f1;
            testAssign(Object.class, String.class, true);
            // f1 = f3;
            testAssign(String.class, Object.class, false);
            Object[] f4 = null;
            String[] f5 = null;
            f4 = f5;
            testAssign(Object[].class, String[].class, true);
            // f5 = f4;
            testAssign(String[].class, Object[].class, false);
        }

        {
            // Class = Param
            Object f1 = null;
            List<?> f2 = null;
            Type t2 = new TypeRef<List<String>>() {}.type();
            f1 = f2;
            testAssign(Object.class, t2, true);
            String f3 = null;
            // f3 = f2;
            testAssign(String.class, t2, false);
            List f4 = f2;
            testAssign(List.class, t2, true);
            Collection f5 = f2;
            testAssign(Collection.class, t2, true);
            Map<?, ?> f6 = null;
            Type t6 = new TypeRef<Map<?, ?>>() {}.type();
            // f5 = f6;
            testAssign(Collection.class, t6, false);
        }

        {
            // Class = ?
            {
                // Object
                List<Object> f1 = null;
                List<?> f2 = null;
                Type t2 = new TypeRef<List<?>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f2.get(0));
                testAssign(Object.class, t2, true);
                List<? extends String> f3 = null;
                Type t3 = new TypeRef<List<? extends String>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f3.get(0));
                testAssign(Object.class, t3, true);
                List<? super String> f4 = null;
                Type t4 = new TypeRef<List<? super String>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f4.get(0));
                testAssign(Object.class, t4, true);
                List<? extends T1> f5 = null;
                Type t5 = new TypeRef<List<? extends T1>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f5.get(0));
                testAssign(Object.class, t5, true);
                List<? super T1> f6 = null;
                Type t6 = new TypeRef<List<? super T1>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f6.get(0));
                testAssign(Object.class, t6, true);
            }
            {
                // String
                List<String> f1 = null;
                List<?> f2 = null;
                Type t2 = new TypeRef<List<?>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f2.get(0));
                testAssign(String.class, t2, false);
                List<? extends String> f3 = null;
                Type t3 = new TypeRef<List<? extends String>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f3.get(0));
                testAssign(String.class, t3, true);
                List<? super String> f4 = null;
                Type t4 = new TypeRef<List<? super String>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f4.get(0));
                testAssign(String.class, t4, false);
                List<? extends T1> f5 = null;
                Type t5 = new TypeRef<List<? extends T1>>() {}.asParameterized().getActualTypeArguments()[0];
                //  f1.set(0, f5.get(0));
                testAssign(String.class, t5, true);
                List<? super T1> f6 = null;
                Type t6 = new TypeRef<List<? super T1>>() {}.asParameterized().getActualTypeArguments()[0];
                // f1.set(0, f6.get(0));
                testAssign(String.class, t6, false);
            }
        }

        {
            // Class = T
            Object f1 = null;
            String f2 = null;
            T0 f3 = null;
            T1 f4 = null;
            T2 f5 = null;
            f1 = f3;
            f1 = f4;
            f1 = f5;
            testAssign(Object.class, new TypeRef<T0>() {}.type(), true);
            testAssign(Object.class, new TypeRef<T1>() {}.type(), true);
            testAssign(Object.class, new TypeRef<T2>() {}.type(), true);
            // f2 = f3;
            f2 = f4;
            f2 = f5;
            testAssign(String.class, new TypeRef<T0>() {}.type(), false);
            testAssign(String.class, new TypeRef<T1>() {}.type(), true);
            testAssign(String.class, new TypeRef<T2>() {}.type(), true);
            CharSequence f6 = null;
            T3 f7 = null;
            T4 f8 = null;
            f6 = f7;
            f6 = f8;
            testAssign(CharSequence.class, new TypeRef<T3>() {}.type(), true);
            testAssign(CharSequence.class, new TypeRef<T4>() {}.type(), true);
        }

        {
            // Class = T[]
            Object f1 = null;
            String f2 = null;
            Object[] f3 = null;
            String[] f4 = null;
            T0[] f5 = null;
            T1[] f6 = null;
            T2[] f7 = null;
            f1 = f5;
            f1 = f6;
            f1 = f7;
            testAssign(Object.class, new TypeRef<T0[]>() {}.type(), true);
            testAssign(Object.class, new TypeRef<T1[]>() {}.type(), true);
            testAssign(Object.class, new TypeRef<T2[]>() {}.type(), true);
            // f2 = f5;
            // f2 = f6;
            // f2 = f7;
            testAssign(String.class, new TypeRef<T0[]>() {}.type(), false);
            testAssign(String.class, new TypeRef<T1[]>() {}.type(), false);
            testAssign(String.class, new TypeRef<T2[]>() {}.type(), false);
            f3 = f5;
            f3 = f6;
            f3 = f7;
            testAssign(Object[].class, new TypeRef<T0[]>() {}.type(), true);
            testAssign(Object[].class, new TypeRef<T1[]>() {}.type(), true);
            testAssign(Object[].class, new TypeRef<T2[]>() {}.type(), true);
            // f4 = f5;
            f4 = f6;
            f4 = f7;
            testAssign(String[].class, new TypeRef<T0[]>() {}.type(), false);
            testAssign(String[].class, new TypeRef<T1[]>() {}.type(), true);
            testAssign(String[].class, new TypeRef<T2[]>() {}.type(), true);
            List f8 = null;
            List[] f9 = null;
            List<?>[] f10 = null;
            List<T1>[] f11 = null;
            List<? extends T1>[] f12 = null;
            // f8 = f10;
            // f8 = f11;
            // f8 = f12;
            f9 = f10;
            f9 = f11;
            f9 = f12;
            testAssign(List.class, new TypeRef<List<?>[]>() {}.type(), false);
            testAssign(List.class, new TypeRef<List<T1>[]>() {}.type(), false);
            testAssign(List.class, new TypeRef<List<? extends T1>[]>() {}.type(), false);
            testAssign(List[].class, new TypeRef<List<?>[]>() {}.type(), true);
            testAssign(List[].class, new TypeRef<List<T1>[]>() {}.type(), true);
            testAssign(List[].class, new TypeRef<List<? extends T1>[]>() {}.type(), true);
        }

        private void testAssign(Type assigned, Type assignee, boolean expected) {
            assertEquals(JieType.isAssignable(assigned, assignee), expected,
                String.format("Assign error: %s = %s", assigned.getTypeName(), assignee.getTypeName())
            );
        }
    }

    public static class ParamTester<
        T0,
        T1 extends Number & List<? extends String>,
        T2 extends T1
        > {

        {
            // Param = Class
            List<?> f1 = null;
            List f2 = f1;
            testAssign(new TypeRef<List<?>>() {}.type(), List.class, true);
            List<? extends String> f3 = f2;
            List<? super String> f4 = f2;
            List<? extends T0> f5 = f2;
            List<? super T0> f6 = f2;
            testAssign(new TypeRef<List<? extends String>>() {}.type(), List.class, true);
            testAssign(new TypeRef<List<? super String>>() {}.type(), List.class, true);
            testAssign(new TypeRef<List<? extends T0>>() {}.type(), List.class, true);
            testAssign(new TypeRef<List<? super T0>>() {}.type(), List.class, true);
        }

        {
            // Param = Param
            {
                // !?
                List<Object> f1 = null;
                List<String> f2 = null;
                // f1 = f2;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<String>>() {}.type(), false);
                List<?> f3 = null;
                // f1 = f3;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<?>>() {}.type(), false);
                List<? super String> f4 = null;
                // f1 = f4;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), false);
                List<T0> f5 = null;
                // f1 = f5;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<T0>>() {}.type(), false);
                List<T0[]> f6 = null;
                // f1 = f6;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), false);
            }
            {
                // ?
                List<?> f1 = null;
                List<String> f2 = null;
                f1 = f2;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<String>>() {}.type(), true);
                List<?> f3 = null;
                f1 = f3;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<?>>() {}.type(), true);
                List<? super String> f4 = null;
                f1 = f4;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), true);
                List<T0> f5 = null;
                f1 = f5;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<T0>>() {}.type(), true);
                List<T0[]> f6 = null;
                f1 = f6;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), true);
            }
            {
                // ? extends String
                List<? extends String> f1 = null;
                List<String> f2 = null;
                f1 = f2;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<String>>() {}.type(), true);
                List<?> f3 = null;
                // f1 = f3;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<?>>() {}.type(), false);
                List<? super String> f4 = null;
                // f1 = f4;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), false);
                List<T0> f5 = null;
                // f1 = f5;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<T0>>() {}.type(), false);
                List<T0[]> f6 = null;
                // f1 = f6;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), false);
            }
            {
                // ? super String
                List<? super String> f1 = null;
                List<String> f2 = null;
                f1 = f2;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<String>>() {}.type(), true);
                List<?> f3 = null;
                // f1 = f3;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<?>>() {}.type(), false);
                List<? super String> f4 = null;
                f1 = f4;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), true);
                List<T0> f5 = null;
                // f1 = f5;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<T0>>() {}.type(), false);
                List<T0[]> f6 = null;
                // f1 = f6;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), false);
                List<CharSequence> f7 = null;
                f1 = f7;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<CharSequence>>() {}.type(), true);
                List<? extends String> f8 = null;
                // f1 = f8;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<? extends String>>() {}.type(), false);
            }
            {
                // complex
                List<? extends List<? super String>> f1 = null;
                ArrayList<ArrayList<CharSequence>> f2 = null;
                f1 = f2;
                testAssign(
                    new TypeRef<List<? extends List<? super String>>>() {}.type(),
                    new TypeRef<ArrayList<ArrayList<CharSequence>>>() {}.type(),
                    true
                );
                List<? super ArrayList<? extends CharSequence>> f3 = null;
                ArrayList<List<String>> f4 = null;
                // f3 = f4;
                testAssign(
                    new TypeRef<List<? super ArrayList<? extends CharSequence>>>() {}.type(),
                    new TypeRef<ArrayList<List<String>>>() {}.type(),
                    false
                );
            }
        }

        {
            // Param = ?
            List<List<?>> f1 = null;
            Type t1 = new TypeRef<List<List<?>>>() {}.asParameterized().getActualTypeArguments()[0];
            List<? extends List<?>> f2 = null;
            Type t2 = new TypeRef<List<? extends List<?>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f2.get(0));
            testAssign(t1, t2, true);
            List<? super List<?>> f3 = null;
            Type t3 = new TypeRef<List<? super List<?>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f3.get(0));
            testAssign(t1, t3, false);
            List<? extends List<? extends String>> f4 = null;
            Type t4 = new TypeRef<List<? extends List<? extends String>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f4.get(0));
            testAssign(t1, t4, true);
            List<? super List<? extends String>> f5 = null;
            Type t5 = new TypeRef<List<? super List<? extends String>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f5.get(0));
            testAssign(t1, t5, false);
            List<? super List<? super String>> f6 = null;
            Type t6 = new TypeRef<List<? super List<? super String>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f6.get(0));
            testAssign(t1, t6, false);
        }

        {
            // Param = T
            List<? extends String> f1 = null;
            T1 f2 = null;
            T2 f3 = null;
            f1 = f2;
            f1 = f3;
            testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<T1>() {}.type(), true);
            testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<T2>() {}.type(), true);
            List<? super String> f4 = null;
            // f4 = f2;
            // f4 = f3;
            testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<T1>() {}.type(), false);
            testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<T2>() {}.type(), false);
        }

        {
            // Class = T[]
            List<?> f1 = null;
            T0[] f2 = null;
            T1[] f3 = null;
            T2[] f4 = null;
            List<?>[] f5;
            // f1 = f2;
            // f1 = f3;
            // f1 = f4;
            // f1 = f5;
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<T0[]>() {}.type(), false);
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<T1[]>() {}.type(), false);
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<T2[]>() {}.type(), false);
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<?>[]>() {}.type(), false);
        }
    }

    public static class WildcardTester<
        T0,
        T1 extends Number & List<? extends String>,
        T2 extends T1
        > {

        {
            // ? = Class
            List<?> f1 = null;

        }

        {
            // Param = Param
            {
                // !?
                List<Object> f1 = null;
                List<String> f2 = null;
                // f1 = f2;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<String>>() {}.type(), false);
                List<?> f3 = null;
                // f1 = f3;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<?>>() {}.type(), false);
                List<? super String> f4 = null;
                // f1 = f4;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), false);
                List<T0> f5 = null;
                // f1 = f5;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<T0>>() {}.type(), false);
                List<T0[]> f6 = null;
                // f1 = f6;
                testAssign(new TypeRef<List<Object>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), false);
            }
            {
                // ?
                List<?> f1 = null;
                List<String> f2 = null;
                f1 = f2;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<String>>() {}.type(), true);
                List<?> f3 = null;
                f1 = f3;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<?>>() {}.type(), true);
                List<? super String> f4 = null;
                f1 = f4;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), true);
                List<T0> f5 = null;
                f1 = f5;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<T0>>() {}.type(), true);
                List<T0[]> f6 = null;
                f1 = f6;
                testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), true);
            }
            {
                // ? extends String
                List<? extends String> f1 = null;
                List<String> f2 = null;
                f1 = f2;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<String>>() {}.type(), true);
                List<?> f3 = null;
                // f1 = f3;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<?>>() {}.type(), false);
                List<? super String> f4 = null;
                // f1 = f4;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), false);
                List<T0> f5 = null;
                // f1 = f5;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<T0>>() {}.type(), false);
                List<T0[]> f6 = null;
                // f1 = f6;
                testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), false);
            }
            {
                // ? super String
                List<? super String> f1 = null;
                List<String> f2 = null;
                f1 = f2;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<String>>() {}.type(), true);
                List<?> f3 = null;
                // f1 = f3;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<?>>() {}.type(), false);
                List<? super String> f4 = null;
                f1 = f4;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<? super String>>() {}.type(), true);
                List<T0> f5 = null;
                // f1 = f5;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<T0>>() {}.type(), false);
                List<T0[]> f6 = null;
                // f1 = f6;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<T0[]>>() {}.type(), false);
                List<CharSequence> f7 = null;
                f1 = f7;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<CharSequence>>() {}.type(), true);
                List<? extends String> f8 = null;
                // f1 = f8;
                testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<List<? extends String>>() {}.type(), false);
            }
            {
                // complex
                List<? extends List<? super String>> f1 = null;
                ArrayList<ArrayList<CharSequence>> f2 = null;
                f1 = f2;
                testAssign(
                    new TypeRef<List<? extends List<? super String>>>() {}.type(),
                    new TypeRef<ArrayList<ArrayList<CharSequence>>>() {}.type(),
                    true
                );
                List<? super ArrayList<? extends CharSequence>> f3 = null;
                ArrayList<List<String>> f4 = null;
                // f3 = f4;
                testAssign(
                    new TypeRef<List<? super ArrayList<? extends CharSequence>>>() {}.type(),
                    new TypeRef<ArrayList<List<String>>>() {}.type(),
                    false
                );
            }
        }

        {
            // Param = ?
            List<List<?>> f1 = null;
            Type t1 = new TypeRef<List<List<?>>>() {}.asParameterized().getActualTypeArguments()[0];
            List<? extends List<?>> f2 = null;
            Type t2 = new TypeRef<List<? extends List<?>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f2.get(0));
            testAssign(t1, t2, true);
            List<? super List<?>> f3 = null;
            Type t3 = new TypeRef<List<? super List<?>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f3.get(0));
            testAssign(t1, t3, false);
            List<? extends List<? extends String>> f4 = null;
            Type t4 = new TypeRef<List<? extends List<? extends String>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f4.get(0));
            testAssign(t1, t4, true);
            List<? super List<? extends String>> f5 = null;
            Type t5 = new TypeRef<List<? super List<? extends String>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f5.get(0));
            testAssign(t1, t5, false);
            List<? super List<? super String>> f6 = null;
            Type t6 = new TypeRef<List<? super List<? super String>>>() {}.asParameterized().getActualTypeArguments()[0];
            // f1.set(0, f6.get(0));
            testAssign(t1, t6, false);
        }

        {
            // Param = T
            List<? extends String> f1 = null;
            T1 f2 = null;
            T2 f3 = null;
            f1 = f2;
            f1 = f3;
            testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<T1>() {}.type(), true);
            testAssign(new TypeRef<List<? extends String>>() {}.type(), new TypeRef<T2>() {}.type(), true);
            List<? super String> f4 = null;
            // f4 = f2;
            // f4 = f3;
            testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<T1>() {}.type(), false);
            testAssign(new TypeRef<List<? super String>>() {}.type(), new TypeRef<T2>() {}.type(), false);
        }

        {
            // Class = T[]
            List<?> f1 = null;
            T0[] f2 = null;
            T1[] f3 = null;
            T2[] f4 = null;
            List<?>[] f5;
            // f1 = f2;
            // f1 = f3;
            // f1 = f4;
            // f1 = f5;
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<T0[]>() {}.type(), false);
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<T1[]>() {}.type(), false);
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<T2[]>() {}.type(), false);
            testAssign(new TypeRef<List<?>>() {}.type(), new TypeRef<List<?>[]>() {}.type(), false);
        }
    }

    private static void testAssign(Type assigned, Type assignee, boolean expected) {
        assertEquals(JieType.isAssignable(assigned, assignee), expected,
            String.format("Assign error: %s = %s", assigned.getTypeName(), assignee.getTypeName())
        );
    }
}
