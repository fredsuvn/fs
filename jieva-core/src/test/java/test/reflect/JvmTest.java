package test.reflect;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.JvmException;
import xyz.sunqian.test.JieTest;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class JvmTest {

    @Test
    public void testInternalName() throws Exception {
        assertEquals(JieJvm.getInternalName(boolean.class), asmInternalName(boolean.class));
        assertEquals(JieJvm.getInternalName(byte.class), asmInternalName(byte.class));
        assertEquals(JieJvm.getInternalName(short.class), asmInternalName(short.class));
        assertEquals(JieJvm.getInternalName(char.class), asmInternalName(char.class));
        assertEquals(JieJvm.getInternalName(int.class), asmInternalName(int.class));
        assertEquals(JieJvm.getInternalName(long.class), asmInternalName(long.class));
        assertEquals(JieJvm.getInternalName(float.class), asmInternalName(float.class));
        assertEquals(JieJvm.getInternalName(double.class), asmInternalName(double.class));
        assertEquals(JieJvm.getInternalName(void.class), asmInternalName(void.class));
        assertEquals(JieJvm.getInternalName(Object.class), asmInternalName(Object.class));
        assertEquals(JieJvm.getInternalName(Object[].class), asmInternalName(Object[].class));
        assertEquals(JieJvm.getInternalName(Object[][].class), asmInternalName(Object[][].class));
        assertEquals(JieJvm.getInternalName(String.class), asmInternalName(String.class));
        assertEquals(JieJvm.getInternalName(String[].class), asmInternalName(String[].class));
        assertEquals(JieJvm.getInternalName(String[][].class), asmInternalName(String[][].class));
        assertEquals(JieJvm.getInternalName(JvmTest.class), asmInternalName(JvmTest.class));
        assertEquals(JieJvm.getInternalName(JvmTest[].class), asmInternalName(JvmTest[].class));
        assertEquals(JieJvm.getInternalName(JvmTest[][].class), asmInternalName(JvmTest[][].class));
    }

    private String asmInternalName(Class<?> cls) {
        return org.objectweb.asm.Type.getInternalName(cls);
    }

    @Test
    public void testDescriptor() throws Exception {
        // class:
        assertEquals(JieJvm.getDescriptor(boolean.class), asmDescriptor(boolean.class));
        assertEquals(JieJvm.getDescriptor(byte.class), asmDescriptor(byte.class));
        assertEquals(JieJvm.getDescriptor(short.class), asmDescriptor(short.class));
        assertEquals(JieJvm.getDescriptor(char.class), asmDescriptor(char.class));
        assertEquals(JieJvm.getDescriptor(int.class), asmDescriptor(int.class));
        assertEquals(JieJvm.getDescriptor(long.class), asmDescriptor(long.class));
        assertEquals(JieJvm.getDescriptor(float.class), asmDescriptor(float.class));
        assertEquals(JieJvm.getDescriptor(double.class), asmDescriptor(double.class));
        assertEquals(JieJvm.getDescriptor(void.class), asmDescriptor(void.class));
        assertEquals(JieJvm.getDescriptor(boolean[].class), asmDescriptor(boolean[].class));
        assertEquals(JieJvm.getDescriptor(boolean[][].class), asmDescriptor(boolean[][].class));
        assertEquals(JieJvm.getDescriptor(Object.class), asmDescriptor(Object.class));
        assertEquals(JieJvm.getDescriptor(Object[].class), asmDescriptor(Object[].class));
        assertEquals(JieJvm.getDescriptor(Object[][].class), asmDescriptor(Object[][].class));

        // method:
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xm")),
            asmDescriptor(X.class.getDeclaredMethod("xm"))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xm", String.class)),
            asmDescriptor(X.class.getDeclaredMethod("xm", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xm", String.class, List.class)),
            asmDescriptor(X.class.getDeclaredMethod("xm", String.class, List.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xm", String.class, List.class, Map.class)),
            asmDescriptor(X.class.getDeclaredMethod("xm", String.class, List.class, Map.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmInt")),
            asmDescriptor(X.class.getDeclaredMethod("xmInt"))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmInt", String.class)),
            asmDescriptor(X.class.getDeclaredMethod("xmInt", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmInt", String.class, List.class)),
            asmDescriptor(X.class.getDeclaredMethod("xmInt", String.class, List.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmInt", String.class, List.class, Map.class)),
            asmDescriptor(X.class.getDeclaredMethod("xmInt", String.class, List.class, Map.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmString")),
            asmDescriptor(X.class.getDeclaredMethod("xmString"))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmString", String.class)),
            asmDescriptor(X.class.getDeclaredMethod("xmString", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmString", String.class, List.class)),
            asmDescriptor(X.class.getDeclaredMethod("xmString", String.class, List.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredMethod("xmString", String.class, List.class, Map.class)),
            asmDescriptor(X.class.getDeclaredMethod("xmString", String.class, List.class, Map.class))
        );

        // constructor:
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredConstructor()),
            asmDescriptor(X.class.getDeclaredConstructor())
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredConstructor(String.class)),
            asmDescriptor(X.class.getDeclaredConstructor(String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredConstructor(String.class, List.class)),
            asmDescriptor(X.class.getDeclaredConstructor(String.class, List.class))
        );
        assertEquals(
            JieJvm.getDescriptor(X.class.getDeclaredConstructor(String.class, List.class, Map.class)),
            asmDescriptor(X.class.getDeclaredConstructor(String.class, List.class, Map.class))
        );

        // exception
        Method getPrimitiveDescriptor = JieJvm.class.getDeclaredMethod("getPrimitiveDescriptor", Class.class);
        JieTest.reflectThrows(UnknownPrimitiveTypeException.class, getPrimitiveDescriptor, null, Object.class);
    }

    private String asmDescriptor(Class<?> cls) {
        return org.objectweb.asm.Type.getDescriptor(cls);
    }

    private String asmDescriptor(Method method) {
        return org.objectweb.asm.Type.getMethodDescriptor(method);
    }

    private String asmDescriptor(Constructor<?> constructor) {
        return org.objectweb.asm.Type.getConstructorDescriptor(constructor);
    }

    @Test
    public void testWildcard() throws Exception {
        class X {
            List<?> l1;
            List<? extends Object> l2;
        }
        Type t1 = ((ParameterizedType) X.class.getDeclaredField("l1").getGenericType()).getActualTypeArguments()[0];
        Type t2 = ((ParameterizedType) X.class.getDeclaredField("l2").getGenericType()).getActualTypeArguments()[0];
        System.out.println(t1);
        System.out.println(t2);
        assertEquals(t1, t2);
        ClassReader cr = new ClassReader(X.class.getName());
        cr.accept(new SignatureParser(), 0);
    }

    @Test
    public void testSignature() throws Exception {
        {
            abstract class A {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            abstract class A extends Number {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            abstract class A extends Number implements List, Serializable {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            abstract class A extends Number implements List<String>, Serializable {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            abstract class A<T extends String> extends Number implements List<T>, Serializable {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            abstract class A<T extends String, Y extends T, U, V extends U>
                extends Number implements List<V>, Serializable {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            abstract class A<T extends String, U, V extends U, W extends Map<? extends List<? super V>, ?>,
                Y extends Map<? super List<? super V>, ? extends List<? extends U>>>
                extends Number implements List<V>, Serializable {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
        {
            class A0<T> {
            }
            abstract class A<
                T extends CharSequence & Serializable,
                U extends A0<? extends String> & CharSequence,
                V extends U
                >
                extends Number implements List<V>, Serializable {
            }
            SignatureParser signatureParser = signatureParser(A.class);
            assertEquals(JieJvm.getSignature(A.class), signatureParser.classSignature());
        }
    }

    private SignatureParser signatureParser(Class<?> cls) throws Exception {
        SignatureParser signatureParser = new SignatureParser();
        ClassReader cr = new ClassReader(cls.getName());
        cr.accept(signatureParser, 0);
        return signatureParser;
    }

    @Test
    public void testLoadBytecode() {
        expectThrows(JvmException.class, () -> JieJvm.loadBytecode(ByteBuffer.wrap(new byte[0])));
    }

    interface XI1<T> {
    }

    interface XI2<T> {
    }

    static class X<T extends List<? extends String> & Serializable & XI1<? super String> & XI2<? extends T>, U>
        extends ArrayList<T>
        implements XI1<String>, XI2<U> {

        X() {
        }

        X(String a) {
        }

        X(String a, List<String> b) {
        }

        X(String a, List<String> b, Map<? super String, ? extends Integer> c) {
        }

        void xm() {
        }

        void xm(String a) {
        }

        void xm(String a, List<String> b) {
        }

        void xm(String a, List<String> b, Map<? super String, ? extends Integer> c) {
        }

        int xmInt() {
            return 0;
        }

        int xmInt(String a) {
            return 0;
        }

        int xmInt(String a, List<String> b) {
            return 0;
        }

        int xmInt(String a, List<String> b, Map<? super String, ? extends Integer> c) {
            return 0;
        }

        String xmString() {
            return null;
        }

        String xmString(String a) {
            return null;
        }

        String xmString(String a, List<String> b) {
            return null;
        }

        String xmString(String a, List<String> b, Map<? super String, ? extends Integer> c) {
            return null;
        }
    }

    static class SignatureParser extends ClassVisitor {

        private String classSignature;
        private final Map<String, String> methodMap = new HashMap<>();
        private final Map<String, String> fieldMap = new HashMap<>();

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
            fieldMap.put(name, signature);
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
            methodMap.put(name + "-" + descriptor, signature);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        public String classSignature() {
            return classSignature == null ?
                null : classSignature.replace("*", "+Ljava/lang/Object;");
        }

        public String fieldSignature(String name) {
            return fieldMap.get(name) == null ?
                null : fieldMap.get(name).replace("*", "+Ljava/lang/Object;");
        }

        public String methodSignature(String name, String descriptor) {
            return methodMap.get(name + "-" + descriptor) == null ?
                null : methodMap.get(name + "-" + descriptor).replace("*", "+Ljava/lang/Object;");
        }
    }

    public static class BaseClass {

        public BaseClass() {
        }

        public BaseClass(String a, List<String> b, List<? super String> c, List<? extends String> d) {

        }

        public void m1() {
        }

        public void m2(String a) {
        }

        public String m3(String a) {
            return null;
        }

        public String m4(String a, List<String> b, List<? super String> c, List<? extends String> d) {
            return null;
        }
    }

    public interface BaseInter {
        default String i1(String a) {
            return null;
        }

        String i2(String a);
    }

    public interface XInter<A, B extends A, C extends String> {
    }

    public static class XClass<T extends Number & CharSequence, U, V extends T, X extends List<? super Integer>,
        Y extends Serializable, W extends CharSequence & RandomAccess, P extends String, O extends Y, M extends ArrayList<String>>
        extends BaseClass implements BaseInter, XInter<V, V, String> {

        private int f1;
        private List f2;
        private List[] f3;
        private List<? extends V> f4;
        private List<? super V> f5;
        private List<? extends V>[] f6;
        private List<? super String>[] f7;

        public XClass(X a, List<? extends T> b, List[] c, List<? extends String> d) {
        }

        @Override
        public String i2(String a) {
            return "";
        }

        public List<? extends U> x1(X a, List<? extends T> b, List[] c, List<? extends String> d) {
            return null;
        }
    }
}
