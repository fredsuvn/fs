package test.reflect;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.JvmException;
import xyz.sunqian.common.reflect.UnknownPrimitiveTypeException;
import xyz.sunqian.test.JieTest;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
    public void testSignature() throws Exception {
        class N<T>{}
        abstract class M extends N<String> implements List {}

        ClassReader cr = new ClassReader(Object.class.getName());
        cr.accept(new SignatureParser(), 0);


        // expectThrows(IllegalArgumentException.class, () -> JieJvm.getSignature(JieType.other()));
        // assertEquals(
        //     JieJvm.getSignature(BaseClass.class.getMethod("m1")),
        //     org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m1"))
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseClass.class.getMethod("m2", String.class)),
        //     org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m2", String.class))
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseClass.class.getMethod("m3", String.class)),
        //     org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m3", String.class))
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseInter.class.getMethod("i1", String.class)),
        //     org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i1", String.class))
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseInter.class.getMethod("i2", String.class)),
        //     org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i2", String.class))
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class)),
        //     "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/util/List<-Ljava/lang/String;>;Ljava/util/List<+Ljava/lang/String;>;)Ljava/lang/String;"
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseClass.class.getConstructor()),
        //     org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor())
        // );
        // assertEquals(
        //     JieJvm.getSignature(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class)),
        //     "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/util/List<-Ljava/lang/String;>;Ljava/util/List<+Ljava/lang/String;>;)V"
        // );
        //
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f1").getGenericType()),
        //     org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f1").getType())
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f2").getGenericType()),
        //     org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f2").getType())
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f3").getGenericType()),
        //     org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f3").getType())
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f4").getGenericType()),
        //     "Ljava/util/List<+TV;>;"
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f5").getGenericType()),
        //     "Ljava/util/List<-TV;>;"
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f6").getGenericType()),
        //     "[Ljava/util/List<+TV;>;"
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getDeclaredField("f7").getGenericType()),
        //     "[Ljava/util/List<-Ljava/lang/String;>;"
        // );
        //
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getMethod("x1", List.class, List.class, List[].class, List.class)),
        //     "(TX;Ljava/util/List<+TT;>;[Ljava/util/List;Ljava/util/List<+Ljava/lang/String;>;)Ljava/util/List<+TU;>;"
        // );
        // assertEquals(
        //     JieJvm.getSignature(XClass.class.getConstructor(List.class, List.class, List[].class, List.class)),
        //     "(TX;Ljava/util/List<+TT;>;[Ljava/util/List;Ljava/util/List<+Ljava/lang/String;>;)V"
        // );
        //
        // assertEquals(
        //     JieJvm.declareSignature(BaseClass.class),
        //     org.objectweb.asm.Type.getDescriptor(Object.class)
        // );
        // assertEquals(
        //     JieJvm.declareSignature(XClass.class),
        //     "<T:Ljava/lang/Number;:Ljava/lang/CharSequence;U:Ljava/lang/Object;V:TT;X::Ljava/util/List<-Ljava/lang/Integer;>;Y::Ljava/io/Serializable;W::Ljava/lang/CharSequence;:Ljava/util/RandomAccess;P:Ljava/lang/String;O:TY;M:Ljava/util/ArrayList<Ljava/lang/String;>;>Ltest/reflect/JvmTest$BaseClass;Ltest/reflect/JvmTest$BaseInter;Ltest/reflect/JvmTest$XInter<TV;TV;Ljava/lang/String;>;"
        // );
    }

    @Test
    public void testLoadBytecode() {
        expectThrows(JvmException.class, () -> JieJvm.loadBytecode(ByteBuffer.wrap(new byte[0])));
    }

    static class SignatureParser extends ClassVisitor {

        private final Map<String, String> signatureMap = new HashMap<>();
        private String classSignature;

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
            System.out.println(signature);
            this.classSignature = signature;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            signatureMap.put(name + "-" + descriptor, signature);
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        public String getClassSignature() {
            return classSignature;
        }

        public String getSignature(String name, String descriptor) {
            return signatureMap.get(name + "-" + descriptor);
        }
    }

    interface XI1<T> {}

    interface XI2<T> {}

    static class X<T extends List<? extends String> & Serializable & XI1<? super String> & XI2<? extends T>, U>
        extends ArrayList<T>
        implements XI1<String>, XI2<U> {

        X() {}

        X(String a) {}

        X(String a, List<String> b) {}

        X(String a, List<String> b, Map<? super String, ? extends Integer> c) {}

        void xm() {}

        void xm(String a) {}

        void xm(String a, List<String> b) {}

        void xm(String a, List<String> b, Map<? super String, ? extends Integer> c) {}

        int xmInt() {return 0;}

        int xmInt(String a) {return 0;}

        int xmInt(String a, List<String> b) {return 0;}

        int xmInt(String a, List<String> b, Map<? super String, ? extends Integer> c) {return 0;}

        String xmString() {return null;}

        String xmString(String a) {return null;}

        String xmString(String a, List<String> b) {return null;}

        String xmString(String a, List<String> b, Map<? super String, ? extends Integer> c) {return null;}
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
