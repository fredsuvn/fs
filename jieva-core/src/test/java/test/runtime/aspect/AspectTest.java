package test.runtime.aspect;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.runtime.aspect.AspectHandler;
import xyz.sunqian.common.runtime.aspect.AspectMaker;
import xyz.sunqian.common.runtime.aspect.AspectSpec;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

public class AspectTest {

    @Test
    public void testAspect() {
        {
            // simple
            IntVar count = IntVar.of(0);
            AspectHandler handler = new AspectHandler() {

                @Override
                public boolean shouldApplyAspect(@Nonnull Method method) {
                    return true;
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
                    count.getAndIncrement();
                    return null;
                }
            };
            AspectSpec spec = AspectMaker.byAsm().make(SimpleCls.class, handler);
            SimpleCls sc = spec.newInstance();
            assertEquals(count.get(), 0);
            assertEquals(
                sc.s1("a", true, (byte) 1, 'c', (short) 2, 3, 4L, 5.0f, 6.0d),
                "atrue1c2s3t4i5l4.05.0d"
            );
            assertEquals(count.get(), 2);
        }
    }

    public static class SimpleCls {
        public String s1(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return a + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
        }

        public void s2(String[] a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            a[0] = "s2";
        }
    }

    public static class SomeCls {
        public String s1(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            return a + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8;
        }

        public void s2(String[] a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
            a[0] = "s2";
        }
    }

    public interface Inter1 {

        String inter1();

        default String inter2(String s, long l) {
            return s + l;
        }
    }
}
