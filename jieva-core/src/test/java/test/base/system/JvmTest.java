package test.base.system;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.testng.annotations.Test;
import test.runtime.reflect.TypeTest;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import xyz.sunqian.common.base.system.JvmException;
import xyz.sunqian.common.base.system.JvmKit;
import xyz.sunqian.common.runtime.reflect.TypeKit;
import xyz.sunqian.test.AssertTest;
import xyz.sunqian.test.PrintTest;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class JvmTest implements AssertTest, PrintTest {

    @Test
    public void testJavaVersion() {
        printFor("JVM description", JvmKit.jvmDescription());
        printFor("JDK major version", JvmKit.javaMajorVersion());
        assertEquals(JvmKit.javaMajorVersion("1.8.0_452"), 8);
        assertEquals(JvmKit.javaMajorVersion("25"), 25);
        assertEquals(JvmKit.javaMajorVersion("25.0.0-ea"), 25);
        assertEquals(JvmKit.javaMajorVersion("17-ea"), 17);
        assertEquals(JvmKit.javaMajorVersion("21-preview"), 21);
        assertEquals(JvmKit.javaMajorVersion("1.8"), 8);
        assertEquals(JvmKit.javaMajorVersion("1.8-ea"), 8);
        assertEquals(JvmKit.javaMajorVersion("1.ea"), -1);
    }

    @Test
    public void testInternalName() throws Exception {
        assertEquals(JvmKit.getInternalName(boolean.class), asmInternalName(boolean.class));
        assertEquals(JvmKit.getInternalName(byte.class), asmInternalName(byte.class));
        assertEquals(JvmKit.getInternalName(short.class), asmInternalName(short.class));
        assertEquals(JvmKit.getInternalName(char.class), asmInternalName(char.class));
        assertEquals(JvmKit.getInternalName(int.class), asmInternalName(int.class));
        assertEquals(JvmKit.getInternalName(long.class), asmInternalName(long.class));
        assertEquals(JvmKit.getInternalName(float.class), asmInternalName(float.class));
        assertEquals(JvmKit.getInternalName(double.class), asmInternalName(double.class));
        assertEquals(JvmKit.getInternalName(void.class), asmInternalName(void.class));
        assertEquals(JvmKit.getInternalName(Object.class), asmInternalName(Object.class));
        assertEquals(JvmKit.getInternalName(Object[].class), asmInternalName(Object[].class));
        assertEquals(JvmKit.getInternalName(Object[][].class), asmInternalName(Object[][].class));
        assertEquals(JvmKit.getInternalName(String.class), asmInternalName(String.class));
        assertEquals(JvmKit.getInternalName(String[].class), asmInternalName(String[].class));
        assertEquals(JvmKit.getInternalName(String[][].class), asmInternalName(String[][].class));
        assertEquals(JvmKit.getInternalName(JvmTest.class), asmInternalName(JvmTest.class));
        assertEquals(JvmKit.getInternalName(JvmTest[].class), asmInternalName(JvmTest[].class));
        assertEquals(JvmKit.getInternalName(JvmTest[][].class), asmInternalName(JvmTest[][].class));
    }

    private String asmInternalName(Class<?> cls) {
        return org.objectweb.asm.Type.getInternalName(cls);
    }

    @Test
    public void testDescriptor() throws Exception {
        // class:
        assertEquals(JvmKit.getDescriptor(boolean.class), asmDescriptor(boolean.class));
        assertEquals(JvmKit.getDescriptor(byte.class), asmDescriptor(byte.class));
        assertEquals(JvmKit.getDescriptor(short.class), asmDescriptor(short.class));
        assertEquals(JvmKit.getDescriptor(char.class), asmDescriptor(char.class));
        assertEquals(JvmKit.getDescriptor(int.class), asmDescriptor(int.class));
        assertEquals(JvmKit.getDescriptor(long.class), asmDescriptor(long.class));
        assertEquals(JvmKit.getDescriptor(float.class), asmDescriptor(float.class));
        assertEquals(JvmKit.getDescriptor(double.class), asmDescriptor(double.class));
        assertEquals(JvmKit.getDescriptor(void.class), asmDescriptor(void.class));
        assertEquals(JvmKit.getDescriptor(boolean[].class), asmDescriptor(boolean[].class));
        assertEquals(JvmKit.getDescriptor(boolean[][].class), asmDescriptor(boolean[][].class));
        assertEquals(JvmKit.getDescriptor(Object.class), asmDescriptor(Object.class));
        assertEquals(JvmKit.getDescriptor(Object[].class), asmDescriptor(Object[].class));
        assertEquals(JvmKit.getDescriptor(Object[][].class), asmDescriptor(Object[][].class));
        assertEquals(JvmKit.getDescriptor(String.class), asmDescriptor(String.class));
        assertEquals(JvmKit.getDescriptor(String[].class), asmDescriptor(String[].class));
        assertEquals(JvmKit.getDescriptor(String[][].class), asmDescriptor(String[][].class));
        assertEquals(JvmKit.getDescriptor(Integer.class), asmDescriptor(Integer.class));
        {
            // fields
            SignatureParser signatureParser = signatureParser(DS.class);
            for (int i = 1; i <= DS.fieldNum; i++) {
                String fieldName = "f" + i;
                Field field = DS.class.getDeclaredField(fieldName);
                String testDesc = JvmKit.getDescriptor(field.getGenericType());
                String asmDesc = signatureParser.fieldDescriptor(fieldName);
                assertEquals(testDesc, asmDesc);
            }
            // method:
            List<Method> methods = Jie.list(DS.class.getDeclaredMethods());
            for (int i = 1; i <= DS.methodNum; i++) {
                String methodName = "m" + i;
                Method method = methods.stream().filter(m -> m.getName().equals(methodName)).findFirst().get();
                String testDesc = JvmKit.getDescriptor(method);
                String asmDesc = signatureParser.methodDescriptor(methodName);
                assertEquals(testDesc, asmDesc);
            }
            // constructor:
            assertTrue(signatureParser.hasConstructorDescriptor(
                JvmKit.getDescriptor(DS.class.getDeclaredConstructor())
            ));
            assertTrue(signatureParser.hasConstructorDescriptor(
                JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class))
            ));
            assertTrue(signatureParser.hasConstructorDescriptor(
                JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, List.class))
            ));
            assertTrue(signatureParser.hasConstructorDescriptor(
                JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, List.class, Map.class))
            ));
            assertTrue(signatureParser.hasConstructorDescriptor(
                JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, List.class, Map.class))
            ));
            assertTrue(signatureParser.hasConstructorDescriptor(
                JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, String.class, Integer.class))
            ));
        }
        {
            // inner class
            // fields
            SignatureParser signatureParser = signatureParser(ODS.class);
            for (int i = 1; i <= ODS.fieldNum; i++) {
                String fieldName = "f" + i;
                Field field = ODS.class.getDeclaredField(fieldName);
                String testDesc = JvmKit.getDescriptor(field.getGenericType());
                String asmDesc = signatureParser.fieldDescriptor(fieldName);
                assertEquals(testDesc, asmDesc);
            }
            // method:
            List<Method> methods = Jie.list(ODS.class.getDeclaredMethods());
            for (int i = 1; i <= ODS.methodNum; i++) {
                String methodName = "m" + i;
                Method method = methods.stream().filter(m -> m.getName().equals(methodName)).findFirst().get();
                String testDesc = JvmKit.getDescriptor(method);
                String asmDesc = signatureParser.methodDescriptor(methodName);
                assertEquals(testDesc, asmDesc);
            }
        }
        // exception
        expectThrows(JvmException.class, () -> JvmKit.getDescriptor(TypeKit.upperWildcard(String.class)));
        Method getPrimitiveDescriptor = JvmKit.class.getDeclaredMethod("getPrimitiveDescriptor", Class.class);
        invokeThrows(UnknownPrimitiveTypeException.class, getPrimitiveDescriptor, null, Object.class);
    }

    private String asmDescriptor(Class<?> cls) {
        return org.objectweb.asm.Type.getDescriptor(cls);
    }

    @Test
    public void testSignature() throws Exception {
        {
            abstract class A {
            }
            abstract class B extends Number {
            }
            abstract class C implements List {
            }
            abstract class D implements List<String> {
            }
            abstract class E extends Number implements List<String> {
            }
            abstract class F extends Number implements Map<String, String>, Serializable, RandomAccess {
            }
            SignatureParser a = signatureParser(A.class);
            assertEquals(JvmKit.getSignature(A.class), a.classSignature());
            SignatureParser b = signatureParser(B.class);
            assertEquals(JvmKit.getSignature(B.class), b.classSignature());
            SignatureParser c = signatureParser(C.class);
            assertEquals(JvmKit.getSignature(C.class), c.classSignature());
            SignatureParser d = signatureParser(D.class);
            assertEquals(JvmKit.getSignature(D.class), d.classSignature());
            SignatureParser e = signatureParser(E.class);
            assertEquals(JvmKit.getSignature(E.class), e.classSignature());
            SignatureParser f = signatureParser(F.class);
            assertEquals(JvmKit.getSignature(F.class), f.classSignature());
        }
        {
            abstract class A<T> {
            }
            abstract class B<T extends String> {
            }
            abstract class C<T extends String & Serializable> {
            }
            abstract class D<T extends CharSequence & Serializable> {
            }
            abstract class E<T extends CharSequence & Serializable, U extends T> {
            }
            abstract class F<T extends CharSequence & Serializable & RandomAccess, U extends T, W> {
            }
            SignatureParser a = signatureParser(A.class);
            assertEquals(JvmKit.getSignature(A.class), a.classSignature());
            SignatureParser b = signatureParser(B.class);
            assertEquals(JvmKit.getSignature(B.class), b.classSignature());
            SignatureParser c = signatureParser(C.class);
            assertEquals(JvmKit.getSignature(C.class), c.classSignature());
            SignatureParser d = signatureParser(D.class);
            assertEquals(JvmKit.getSignature(D.class), d.classSignature());
            SignatureParser e = signatureParser(E.class);
            assertEquals(JvmKit.getSignature(E.class), e.classSignature());
            SignatureParser f = signatureParser(F.class);
            assertEquals(JvmKit.getSignature(F.class), f.classSignature());
        }
        {
            abstract class A<T extends CharSequence & Serializable & RandomAccess, U extends T, W>
                extends Number implements Map<String, String>, Serializable, RandomAccess {
            }
            abstract class B<T> {
            }
            abstract class C extends B<String> {
            }
            SignatureParser a = signatureParser(A.class);
            assertEquals(JvmKit.getSignature(A.class), a.classSignature());
            SignatureParser c = signatureParser(C.class);
            assertEquals(JvmKit.getSignature(C.class), c.classSignature());
        }
        {
            // for Inter
            SignatureParser a = signatureParser(Inter.class);
            assertEquals(JvmKit.getSignature(Inter.class), a.classSignature());
        }
        {
            // for DS:
            // fields
            SignatureParser signatureParser = signatureParser(DS.class);
            for (int i = 1; i <= DS.fieldNum; i++) {
                String fieldName = "f" + i;
                Field field = DS.class.getDeclaredField(fieldName);
                String testSig = JvmKit.getSignature(field);
                String asmSig = signatureParser.fieldSignature(fieldName);
                assertEquals(testSig, asmSig);
            }
            // method:
            List<Method> methods = Jie.list(DS.class.getDeclaredMethods());
            for (int i = 1; i <= DS.methodNum; i++) {
                String methodName = "m" + i;
                Method method = methods.stream().filter(m -> m.getName().equals(methodName)).findFirst().get();
                String testSig = JvmKit.getSignature(method);
                String asmSig = signatureParser.methodSignature(methodName);
                assertEquals(testSig, asmSig);
            }
            // constructor:
            assertEquals(
                JvmKit.getSignature(DS.class.getDeclaredConstructor()),
                signatureParser.constructorSignature(JvmKit.getDescriptor(DS.class.getDeclaredConstructor()))
            );
            assertEquals(
                JvmKit.getSignature(DS.class.getDeclaredConstructor(String.class)),
                signatureParser.constructorSignature(
                    JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class))
                )
            );
            assertEquals(
                JvmKit.getSignature(DS.class.getDeclaredConstructor(String.class, List.class)),
                signatureParser.constructorSignature(
                    JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, List.class))
                )
            );
            assertEquals(
                JvmKit.getSignature(DS.class.getDeclaredConstructor(String.class, List.class, Map.class)),
                signatureParser.constructorSignature(
                    JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, List.class, Map.class))
                )
            );
            assertEquals(
                JvmKit.getSignature(DS.class.getDeclaredConstructor(String.class, String.class, Integer.class)),
                signatureParser.constructorSignature(
                    JvmKit.getDescriptor(DS.class.getDeclaredConstructor(String.class, String.class, Integer.class))
                )
            );
        }
        {
            // inner class
            // fields
            SignatureParser signatureParser = signatureParser(ODS.class);
            for (int i = 1; i <= ODS.fieldNum; i++) {
                String fieldName = "f" + i;
                Field field = ODS.class.getDeclaredField(fieldName);
                String testSig = JvmKit.getSignature(field);
                String asmSig = signatureParser.fieldSignature(fieldName);
                assertEquals(testSig, asmSig);
            }
            // method:
            List<Method> methods = Jie.list(ODS.class.getDeclaredMethods());
            for (int i = 1; i <= ODS.methodNum; i++) {
                String methodName = "m" + i;
                Method method = methods.stream().filter(m -> m.getName().equals(methodName)).findFirst().get();
                String testSig = JvmKit.getSignature(method);
                String asmSig = signatureParser.methodSignature(methodName);
                assertEquals(testSig, asmSig);
            }
        }
        // exception
        expectThrows(JvmException.class, () -> JvmKit.getSignature(TypeKit.otherType()));
    }

    private SignatureParser signatureParser(Class<?> cls) throws Exception {
        SignatureParser signatureParser = new SignatureParser();
        ClassReader cr = new ClassReader(cls.getName());
        cr.accept(signatureParser, 0);
        return signatureParser;
    }

    @Test
    public void testRawType() throws Exception {
        Method raw1 = JvmKit.class.getDeclaredMethod("getRawClass", ParameterizedType.class);
        invokeThrows(JvmException.class, raw1, null, TypeTest.errorParameterizedType());
        Method raw2 = JvmKit.class.getDeclaredMethod("getRawClass", GenericArrayType.class);
        invokeThrows(JvmException.class, raw2, null, TypeKit.arrayType(TypeKit.otherType()));
    }

    static class SignatureParser extends ClassVisitor {

        private String classSignature;
        private final Map<String, String> fieldDesc = new HashMap<>();
        private final Map<String, String> methodDesc = new HashMap<>();
        private final Map<String, String> fieldSig = new HashMap<>();
        private final Map<String, String> methodSig = new HashMap<>();
        private final Map<String, String> constructorInfo = new HashMap<>();

        public SignatureParser() {
            super(Opcodes.ASM7);
        }

        @Override
        public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces
        ) {
            this.classSignature = signature;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            fieldDesc.put(name, descriptor);
            fieldSig.put(name, signature);
            return super.visitField(access, name, descriptor, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            if (name.equals("<init>")) {
                constructorInfo.put(descriptor, signature);
            }
            methodDesc.put(name, descriptor);
            methodSig.put(name, signature);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        public String classSignature() {
            return classSignature == null ?
                null : classSignature.replace("*", "+Ljava/lang/Object;");
        }

        public String fieldDescriptor(String name) {
            return fieldDesc.get(name);
        }

        public String fieldSignature(String name) {
            String sig = fieldSig.get(name);
            return sig == null ? null : sig.replace("*", "+Ljava/lang/Object;");
        }

        public String methodDescriptor(String name) {
            return methodDesc.get(name);
        }

        public String methodSignature(String name) {
            String sig = methodSig.get(name);
            return sig == null ? null : sig.replace("*", "+Ljava/lang/Object;");
        }

        public boolean hasConstructorDescriptor(String descriptor) {
            return constructorInfo.containsKey(descriptor);
        }

        public String constructorSignature(String name) {
            String sig = constructorInfo.get(name);
            return sig == null ? null : sig.replace("*", "+Ljava/lang/Object;");
        }
    }

    abstract static class DS<
        T extends String,
        U extends T,
        V extends Serializable & RandomAccess,
        W extends RandomAccess & Serializable,
        F extends List<? extends String>
        > {

        static final int fieldNum = 23;
        static final int methodNum = 15;

        int f1;
        String f2;
        List<String> f3;
        List<? extends List<String>> f4;
        Map<? extends List<String>, ? super List<String>> f5;
        Map<?, ?> f6;
        T f7;
        U f8;
        V f9;
        W f10;
        F f11;
        int[] f12;
        String[] f13;
        List<String>[] f14;
        List<? extends List<String>>[][] f15;
        Map<? extends List<String>, ? super List<String>>[] f16;
        Map<?, ?>[][] f17;
        T[] f18;
        U[] f19;
        V[] f20;
        W[] f21;
        F[] f22;
        F[][] f23;

        DS() {
        }

        DS(String a) {
        }

        DS(String a, List<String> b) {
        }

        DS(String a, List<String> b, Map<? super String, ? extends Integer> c) {
        }

        <O extends String, P extends O, Q extends Integer> DS(O o, P p, Q q) {
        }

        abstract void m1();

        abstract void m2(String a);

        abstract void m3(String a, List<String> b);

        abstract void m4(String a, List<String> b, Map<? super String, ? extends Integer> c);

        abstract int m5();

        abstract int m6(String a);

        abstract int m7(String a, List<String> b);

        abstract int m8(String a, List<String> b, Map<? super String, ? extends Integer> c);

        abstract String m9();

        abstract String m10(String a);

        abstract String m11(String a, List<String> b);

        abstract String m12(String a, List<String> b, Map<? super String, ? extends Integer> c);

        abstract F[][] m13(
            String a,
            List<String> b,
            Map<? super String, ? extends Integer> c,
            T t,
            U[] u,
            List<? extends V> v,
            Map<? super W, ? extends W> w
        );

        abstract <
            O extends String & List<String>,
            P extends List<? super String> & Serializable,
            Q extends P
            > F[][] m14(
            String a,
            List<String> b,
            Map<? super String, ? extends Integer> c,
            T t,
            U[] u,
            List<? extends V> v,
            Map<? super W, ? extends W> w,
            O o,
            P p,
            Q q,
            O[] oo,
            P[][] pp,
            Q[][][] qq
        );

        abstract <O extends ODS<String, String>, P extends ODS<String, String>.IDS<Integer, Integer>>
        P m15(O o, P p, P[] pp);
    }

    abstract class ODS<T, U> {

        abstract class IDS<O, P> {
        }

        static final int fieldNum = 6;
        static final int methodNum = 7;

        ODS<String, String>.IDS<Integer, Integer> f1;
        ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer> f2;
        ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>[] f3;
        List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>> f4;
        List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>>[] f5;
        List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>[]> f6;

        abstract ODS<String, String>.IDS<Integer, Integer> m1(
            ODS<String, String>.IDS<Integer, Integer> a);

        abstract ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer> m2(
            ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer> a);

        abstract ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>[] m3(
            ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>[] a);

        abstract List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>> m4(
            List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>> a);

        abstract List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>>[] m5(
            List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>>[] a);

        abstract List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>[]> m6(
            List<? extends ODS<? super String, ? extends String>.IDS<? super Integer, ? extends Integer>[]> a);

        abstract <A extends CharSequence & RandomAccess, B extends A> B m7(
            A a, B b, ODS<String, String>.IDS<Integer, Integer> c);
    }

    interface Inter extends List<String> {
    }
}
