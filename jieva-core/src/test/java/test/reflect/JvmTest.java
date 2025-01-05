package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.JvmException;
import xyz.sunqian.common.reflect.NotPrimitiveException;
import xyz.sunqian.test.JieTest;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class JvmTest {

    @Test
    public void testName() throws Exception {
        assertEquals(JieJvm.getInternalName(boolean.class), org.objectweb.asm.Type.getInternalName(boolean.class));
        assertEquals(JieJvm.getInternalName(byte.class), org.objectweb.asm.Type.getInternalName(byte.class));
        assertEquals(JieJvm.getInternalName(short.class), org.objectweb.asm.Type.getInternalName(short.class));
        assertEquals(JieJvm.getInternalName(char.class), org.objectweb.asm.Type.getInternalName(char.class));
        assertEquals(JieJvm.getInternalName(int.class), org.objectweb.asm.Type.getInternalName(int.class));
        assertEquals(JieJvm.getInternalName(long.class), org.objectweb.asm.Type.getInternalName(long.class));
        assertEquals(JieJvm.getInternalName(float.class), org.objectweb.asm.Type.getInternalName(float.class));
        assertEquals(JieJvm.getInternalName(double.class), org.objectweb.asm.Type.getInternalName(double.class));
        assertEquals(JieJvm.getInternalName(void.class), org.objectweb.asm.Type.getInternalName(void.class));
        assertEquals(
            JieJvm.getInternalName(Object.class),
            org.objectweb.asm.Type.getInternalName(Object.class)
        );
    }

    @Test
    public void testDescriptor() throws Exception {
        assertEquals(JieJvm.getDescriptor(boolean.class), org.objectweb.asm.Type.getDescriptor(boolean.class));
        assertEquals(JieJvm.getDescriptor(byte.class), org.objectweb.asm.Type.getDescriptor(byte.class));
        assertEquals(JieJvm.getDescriptor(short.class), org.objectweb.asm.Type.getDescriptor(short.class));
        assertEquals(JieJvm.getDescriptor(char.class), org.objectweb.asm.Type.getDescriptor(char.class));
        assertEquals(JieJvm.getDescriptor(int.class), org.objectweb.asm.Type.getDescriptor(int.class));
        assertEquals(JieJvm.getDescriptor(long.class), org.objectweb.asm.Type.getDescriptor(long.class));
        assertEquals(JieJvm.getDescriptor(float.class), org.objectweb.asm.Type.getDescriptor(float.class));
        assertEquals(JieJvm.getDescriptor(double.class), org.objectweb.asm.Type.getDescriptor(double.class));
        assertEquals(JieJvm.getDescriptor(void.class), org.objectweb.asm.Type.getDescriptor(void.class));
        assertEquals(
            JieJvm.getDescriptor(Object.class),
            org.objectweb.asm.Type.getDescriptor(Object.class)
        );
        assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m1")),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m1"))
        );
        assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m2", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m3", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m3", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(BaseInter.class.getMethod("i1", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i1", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(BaseInter.class.getMethod("i2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i2", String.class))
        );
        assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class))
        );
        assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getConstructor()),
            org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor())
        );
        assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class)),
            org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class))
        );

        // exception
        Method getPrimitiveDescriptor = JieJvm.class.getDeclaredMethod("getPrimitiveDescriptor", Class.class);
        JieTest.reflectThrows(NotPrimitiveException.class, getPrimitiveDescriptor, null, Object.class);
    }

    @Test
    public void testSignature() throws Exception {
        expectThrows(IllegalArgumentException.class, () -> JieJvm.getSignature(JieType.other()));
        assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m1")),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m1"))
        );
        assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m2", String.class))
        );
        assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m3", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m3", String.class))
        );
        assertEquals(
            JieJvm.getSignature(BaseInter.class.getMethod("i1", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i1", String.class))
        );
        assertEquals(
            JieJvm.getSignature(BaseInter.class.getMethod("i2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i2", String.class))
        );
        assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class)),
            "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/util/List<-Ljava/lang/String;>;Ljava/util/List<+Ljava/lang/String;>;)Ljava/lang/String;"
        );
        assertEquals(
            JieJvm.getSignature(BaseClass.class.getConstructor()),
            org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor())
        );
        assertEquals(
            JieJvm.getSignature(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class)),
            "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/util/List<-Ljava/lang/String;>;Ljava/util/List<+Ljava/lang/String;>;)V"
        );

        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f1").getGenericType()),
            org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f1").getType())
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f2").getGenericType()),
            org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f2").getType())
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f3").getGenericType()),
            org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f3").getType())
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f4").getGenericType()),
            "Ljava/util/List<+TV;>;"
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f5").getGenericType()),
            "Ljava/util/List<-TV;>;"
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f6").getGenericType()),
            "[Ljava/util/List<+TV;>;"
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f7").getGenericType()),
            "[Ljava/util/List<-Ljava/lang/String;>;"
        );

        assertEquals(
            JieJvm.getSignature(XClass.class.getMethod("x1", List.class, List.class, List[].class, List.class)),
            "(TX;Ljava/util/List<+TT;>;[Ljava/util/List;Ljava/util/List<+Ljava/lang/String;>;)Ljava/util/List<+TU;>;"
        );
        assertEquals(
            JieJvm.getSignature(XClass.class.getConstructor(List.class, List.class, List[].class, List.class)),
            "(TX;Ljava/util/List<+TT;>;[Ljava/util/List;Ljava/util/List<+Ljava/lang/String;>;)V"
        );

        assertEquals(
            JieJvm.declareSignature(BaseClass.class),
            org.objectweb.asm.Type.getDescriptor(Object.class)
        );
        assertEquals(
            JieJvm.declareSignature(XClass.class),
            "<T:Ljava/lang/Number;:Ljava/lang/CharSequence;U:Ljava/lang/Object;V:TT;X::Ljava/util/List<-Ljava/lang/Integer;>;Y::Ljava/io/Serializable;W::Ljava/lang/CharSequence;:Ljava/util/RandomAccess;P:Ljava/lang/String;O:TY;M:Ljava/util/ArrayList<Ljava/lang/String;>;>Ltest/reflect/JvmTest$BaseClass;Ltest/reflect/JvmTest$BaseInter;Ltest/reflect/JvmTest$XInter<TV;TV;Ljava/lang/String;>;"
        );
    }

    @Test
    public void testLoadBytecode() {
        expectThrows(JvmException.class, () -> JieJvm.loadBytecode(ByteBuffer.wrap(new byte[0])));
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
