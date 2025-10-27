package tests.runtime.aspect;

import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.value.BooleanVar;
import space.sunqian.common.base.value.IntVar;
import space.sunqian.common.base.value.Var;
import space.sunqian.common.runtime.aspect.AspectException;
import space.sunqian.common.runtime.aspect.AspectHandler;
import space.sunqian.common.runtime.aspect.AspectMaker;
import space.sunqian.common.runtime.aspect.AspectSpec;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AspectTest {

    @Test
    public void testSimple() {
        IntVar count = IntVar.of(0);
        BooleanVar replace = BooleanVar.of(false);
        AspectHandler handler = new AspectHandler() {

            @Override
            public boolean needsAspect(@Nonnull Method method) {
                return true;
            }

            @Override
            public void beforeInvoking(@Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                if (replace.get()) {
                    args[0] = "b";
                }
                count.getAndIncrement();
            }

            @Override
            public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                if (replace.get()) {
                    assertEquals(args[0], "b");
                } else {
                    assertEquals(args[0], "a");
                }
                count.getAndIncrement();
                return result;
            }

            @Override
            public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) {
                if (replace.get()) {
                    assertEquals(args[0], "b");
                } else {
                    assertEquals(args[0], "a");
                }
                count.getAndIncrement();
                Object[] result = (Object[]) args[args.length - 1];
                return result[0];
            }
        };
        testSimple(count, replace, handler, SimpleCls.class);
        testSimple(count, replace, handler, ComplexCls.class);
        // exception
        assertThrows(AspectException.class, () -> AspectMaker.byAsm().make(SimpleCls.class, null));
    }

    private <T extends SimpleCls> void testSimple(IntVar count, BooleanVar replace, AspectHandler handler, Class<T> cls) {
        {
            // String
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getString("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                "atrue234567.08.0"
            );
            assertEquals(count.get(), 2);
            replace.set(true);
            assertEquals(
                sc.getString("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                "btrue234567.08.0"
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
            // test AspectSpec
            assertEquals(spec.advisedClass(), cls);
            assertEquals(spec.aspectClass(), sc.getClass());
            assertEquals(spec.aspectHandler(), handler);
        }
        {
            // void
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            Object[] result = new Object[1];
            sc.getVoid("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, result);
            assertEquals(
                result[0],
                "atrue234567.08.0"
            );
            assertEquals(count.get(), 2);
            replace.set(true);
            result[0] = null;
            sc.getVoid("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, result);
            assertEquals(
                result[0],
                "btrue234567.08.0"
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // boolean
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getBoolean("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                true
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getBoolean("a", false, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                false
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // byte
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getByte("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                2
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getByte("a", true, (byte) 20, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                20
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // char
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getChar("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                '3'
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getChar("a", true, (byte) 2, '6', (short) 4, 5, 6L, 7.0f, 8.0d),
                '6'
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // short
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getShort("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                4
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getShort("a", true, (byte) 2, '3', (short) 40, 5, 6L, 7.0f, 8.0d),
                40
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // int
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getInt("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                5
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getInt("a", true, (byte) 2, '3', (short) 4, 50, 6L, 7.0f, 8.0d),
                50
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // long
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getLong("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                6L
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getLong("a", true, (byte) 2, '3', (short) 4, 5, 60L, 7.0f, 8.0d),
                60L
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // float
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getFloat("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                7.0f
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getFloat("a", true, (byte) 2, '3', (short) 4, 5, 6L, 70.0f, 8.0d),
                70.0f
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // double
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.getDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d),
                8.0
            );
            assertEquals(count.get(), 2);
            assertEquals(
                sc.getDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 80.0d),
                80.0
            );
            assertEquals(count.get(), 4);
            count.clear();
            replace.clear();
        }
        {
            // throw
            AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            Object[] result = new Object[1];
            assertEquals(
                sc.throwDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, result),
                8.0
            );
            assertEquals(
                result[0],
                8.0
            );
            assertEquals(count.get(), 2);
            count.clear();
            replace.clear();
            // direct throw
            AspectSpec spec2 = AspectMaker.byAsm().make(cls, new AspectHandler() {
                @Override
                public boolean needsAspect(@Nonnull Method method) {
                    return true;
                }

                @Override
                public void beforeInvoking(
                    @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target
                ) {
                }

                @Override
                public @Nullable Object afterReturning(
                    @Nullable Object result, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target
                ) {
                    return result;
                }

                @Override
                public @Nullable Object afterThrowing(
                    @Nonnull Throwable ex, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target
                ) {
                    throw new AspectTestException(ex);
                }
            });
            SimpleCls sc2 = spec2.newInstance();
            AspectTestException ex = assertThrows(AspectTestException.class, () ->
                sc2.throwDouble("a", true, (byte) 2, '3', (short) 4, 5, 6L, 7.0f, 8.0d, null)
            );
            assertTrue(ex.getCause() instanceof NullPointerException);
            count.clear();
            replace.clear();
        }
    }

    @Test
    public void testComplex() {
        IntVar count = IntVar.of(0);
        AspectHandler handler = new AspectHandler() {

            @Override
            public boolean needsAspect(@Nonnull Method method) {
                return !method.getDeclaringClass().equals(Object.class);
            }

            @Override
            public void beforeInvoking(@Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                count.getAndIncrement();
            }

            @Override
            public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                count.getAndIncrement();
                return result;
            }

            @Override
            public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) {
                return null;
            }
        };
        {
            // inter
            AspectSpec spec = AspectMaker.byAsm().make(ComplexCls.class, handler);
            ComplexCls cc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(cc.inter1(), "inter1");
            assertEquals(count.get(), 2);
            assertEquals(cc.inter2(11L), 11L);
            assertEquals(count.get(), 4);
            assertEquals(cc.inter2(11L, 11L), "1111");
            assertEquals(count.get(), 6);
            assertEquals(cc.inter2("22L", 11L), "22L11");
            assertEquals(count.get(), 8);
            assertEquals(cc.inter3(888), 888);
            assertEquals(count.get(), 10);
            assertEquals(cc.inter3(888, 1L), "8881");
            assertEquals(count.get(), 12);
        }
    }

    @Test
    public void testGeneric() {
        Var<Object> arg = Var.of(null);
        AspectHandler handler = new AspectHandler() {

            @Override
            public boolean needsAspect(@Nonnull Method method) {
                return !method.getDeclaringClass().equals(Object.class);
            }

            @Override
            public void beforeInvoking(@Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                arg.set(args[0]);
            }

            @Override
            public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                return result;
            }

            @Override
            public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, @Nullable Object @Nonnull [] args, @Nonnull Object target) {
                return null;
            }
        };
        AspectSpec spec = AspectMaker.byAsm().make(SimpleGeneric.class, handler);
        SimpleGeneric<String> strSg = spec.newInstance();
        assertEquals(strSg.generic("sss"), "sss");
        assertEquals(arg.get(), "sss");
        SimpleGeneric<Integer> intSg = spec.newInstance();
        assertEquals(intSg.generic(888), 888);
        assertEquals(arg.get(), 888);
    }

    public static class SimpleCls {

        public String getString(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return a + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
        }

        public void getVoid(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8, Object[] result) {
            result[0] = a + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
        }

        public boolean getBoolean(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p1;
        }

        public byte getByte(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p2;
        }

        public char getChar(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p3;
        }

        public short getShort(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p4;
        }

        public int getInt(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p5;
        }

        public long getLong(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p6;
        }

        public float getFloat(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p7;
        }

        public double getDouble(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return p8;
        }

        public double throwDouble(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8, Object[] result) {
            result[0] = p8;
            throw new AspectTestException();
        }

        // test modifier:

        public static void testModifierFilter1() {
        }

        public final void testModifierFilter2() {
        }

        private void testModifierFilter3() {
        }

        private static void testModifierFilter4() {
        }
    }

    public static abstract class SimpleCls2 extends SimpleCls implements Inter1 {
    }

    public static abstract class SimpleCls3 extends SimpleCls2 implements Inter2<Long> {
    }

    public static class ComplexCls extends SimpleCls3 implements Inter3 {

        @Override
        public String inter1() {
            return "inter1";
        }

        @Override
        public Long inter2(Long aLong) {
            return aLong;
        }

        @Override
        public <T> T inter3(T t) {
            return t;
        }
    }

    public static class SimpleGeneric<T> {

        public T generic(T t) {
            return t;
        }
    }

    public interface Inter1 {

        String inter1();

        default String inter2(String s, long l) {
            return s + l;
        }
    }

    public interface Inter2<T> {

        T inter2(T t);

        default String inter2(T s, long l) {
            return s.toString() + l;
        }
    }

    public interface Inter3 {

        <T> T inter3(T t);

        default <T> String inter3(T s, long l) {
            return s.toString() + l;
        }
    }

    private static class AspectTestException extends RuntimeException {

        public AspectTestException() {
        }

        public AspectTestException(Throwable cause) {
            super(cause);
        }
    }
}
