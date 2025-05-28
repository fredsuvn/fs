package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.proxy.JieProxy;
import xyz.sunqian.common.reflect.proxy.MethodProxyHandler;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

public class BigMethodTest {

    @Test
    public void testBigMethod() throws Exception {
        MethodProxyHandler handler = new MethodProxyHandler() {
            @Override
            public boolean needsProxy(Method method) {
                return method.getName().startsWith("big");
            }

            @Override
            public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
                if (method.getName().equals("big")) {
                    return "big";
                }
                return invoker.invokeSuper(proxy, args);
            }
        };
        BigInter bigInter = JieProxy.asmProxyBuilder().interfaces(Jie.list(BigInter.class)).proxyHandler(handler).build().newInstance();
        assertEquals(bigInter.big(
            0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
        ), "big");
        assertEquals(bigInter.bigDefault(
            0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
        ), "bigDefault");
        BigClass bigClass = JieProxy.asmProxyBuilder().superClass((BigClass.class)).proxyHandler(handler).build().newInstance();
        assertEquals(bigClass.bigClass(
            0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
        ), "BigClass");
        Object big = JieProxy.asmProxyBuilder().superClass((BigClass.class)).interfaces(Jie.list(BigInter.class)).proxyHandler(handler).build().newInstance();
        assertEquals(((BigInter) big).big(
            0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
        ), "big");
        assertEquals(((BigInter) big).bigDefault(
            0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
        ), "bigDefault");
        assertEquals(((BigClass) big).bigClass(
            0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
            , 0, 0, "0", 0, 0, "0", 0, 0, "0"
        ), "BigClass");
    }

    public interface BigInter {

        String big(
            int a000, long a001, String a002, int a003, long a004, String a005, int a006, long a007, String a008
            , int a010, long a011, String a012, int a013, long a014, String a015, int a016, long a017, String a018
            , int a020, long a021, String a022, int a023, long a024, String a025, int a026, long a027, String a028
            , int a030, long a031, String a032, int a033, long a034, String a035, int a036, long a037, String a038
            , int a040, long a041, String a042, int a043, long a044, String a045, int a046, long a047, String a048
            , int a050, long a051, String a052, int a053, long a054, String a055, int a056, long a057, String a058
            , int a060, long a061, String a062, int a063, long a064, String a065, int a066, long a067, String a068
            , int a070, long a071, String a072, int a073, long a074, String a075, int a076, long a077, String a078
            , int a080, long a081, String a082, int a083, long a084, String a085, int a086, long a087, String a088
            , int a090, long a091, String a092, int a093, long a094, String a095, int a096, long a097, String a098
            , int a100, long a101, String a102, int a103, long a104, String a105, int a106, long a107, String a108
            , int a110, long a111, String a112, int a113, long a114, String a115, int a116, long a117, String a118
            , int a120, long a121, String a122, int a123, long a124, String a125, int a126, long a127, String a128
            , int a130, long a131, String a132, int a133, long a134, String a135, int a136, long a137, String a138
            , int a140, long a141, String a142, int a143, long a144, String a145, int a146, long a147, String a148
            , int a150, long a151, String a152, int a153, long a154, String a155, int a156, long a157, String a158
            , int a160, long a161, String a162, int a163, long a164, String a165, int a166, long a167, String a168
            , int a170, long a171, String a172, int a173, long a174, String a175, int a176, long a177, String a178
            , int a180, long a181, String a182, int a183, long a184, String a185, int a186, long a187, String a188
            , int a190, long a191, String a192, int a193, long a194, String a195, int a196, long a197, String a198
        );

        default String bigDefault(
            int a000, long a001, String a002, int a003, long a004, String a005, int a006, long a007, String a008
            , int a010, long a011, String a012, int a013, long a014, String a015, int a016, long a017, String a018
            , int a020, long a021, String a022, int a023, long a024, String a025, int a026, long a027, String a028
            , int a030, long a031, String a032, int a033, long a034, String a035, int a036, long a037, String a038
            , int a040, long a041, String a042, int a043, long a044, String a045, int a046, long a047, String a048
            , int a050, long a051, String a052, int a053, long a054, String a055, int a056, long a057, String a058
            , int a060, long a061, String a062, int a063, long a064, String a065, int a066, long a067, String a068
            , int a070, long a071, String a072, int a073, long a074, String a075, int a076, long a077, String a078
            , int a080, long a081, String a082, int a083, long a084, String a085, int a086, long a087, String a088
            , int a090, long a091, String a092, int a093, long a094, String a095, int a096, long a097, String a098
            , int a100, long a101, String a102, int a103, long a104, String a105, int a106, long a107, String a108
            , int a110, long a111, String a112, int a113, long a114, String a115, int a116, long a117, String a118
            , int a120, long a121, String a122, int a123, long a124, String a125, int a126, long a127, String a128
            , int a130, long a131, String a132, int a133, long a134, String a135, int a136, long a137, String a138
            , int a140, long a141, String a142, int a143, long a144, String a145, int a146, long a147, String a148
            , int a150, long a151, String a152, int a153, long a154, String a155, int a156, long a157, String a158
            , int a160, long a161, String a162, int a163, long a164, String a165, int a166, long a167, String a168
            , int a170, long a171, String a172, int a173, long a174, String a175, int a176, long a177, String a178
            , int a180, long a181, String a182, int a183, long a184, String a185, int a186, long a187, String a188
            , int a190, long a191, String a192, int a193, long a194, String a195, int a196, long a197, String a198
        ) {
            return "bigDefault";
        }
    }

    public static class BigClass {

        public String bigClass(
            int a000, long a001, String a002, int a003, long a004, String a005, int a006, long a007, String a008
            , int a010, long a011, String a012, int a013, long a014, String a015, int a016, long a017, String a018
            , int a020, long a021, String a022, int a023, long a024, String a025, int a026, long a027, String a028
            , int a030, long a031, String a032, int a033, long a034, String a035, int a036, long a037, String a038
            , int a040, long a041, String a042, int a043, long a044, String a045, int a046, long a047, String a048
            , int a050, long a051, String a052, int a053, long a054, String a055, int a056, long a057, String a058
            , int a060, long a061, String a062, int a063, long a064, String a065, int a066, long a067, String a068
            , int a070, long a071, String a072, int a073, long a074, String a075, int a076, long a077, String a078
            , int a080, long a081, String a082, int a083, long a084, String a085, int a086, long a087, String a088
            , int a090, long a091, String a092, int a093, long a094, String a095, int a096, long a097, String a098
            , int a100, long a101, String a102, int a103, long a104, String a105, int a106, long a107, String a108
            , int a110, long a111, String a112, int a113, long a114, String a115, int a116, long a117, String a118
            , int a120, long a121, String a122, int a123, long a124, String a125, int a126, long a127, String a128
            , int a130, long a131, String a132, int a133, long a134, String a135, int a136, long a137, String a138
            , int a140, long a141, String a142, int a143, long a144, String a145, int a146, long a147, String a148
            , int a150, long a151, String a152, int a153, long a154, String a155, int a156, long a157, String a158
            , int a160, long a161, String a162, int a163, long a164, String a165, int a166, long a167, String a168
            , int a170, long a171, String a172, int a173, long a174, String a175, int a176, long a177, String a178
            , int a180, long a181, String a182, int a183, long a184, String a185, int a186, long a187, String a188
            , int a190, long a191, String a192, int a193, long a194, String a195, int a196, long a197, String a198
        ) {
            return "BigClass";
        }
    }

    private void generateParameters() {
        for (int i = 0; i < 20; i++) {
            String prefix = String.format("%02d", i);
            for (int j = 0; j < 10; j++) {
                System.out.print(", int a" + prefix + j++);
                System.out.print(", long a" + prefix + j++);
                System.out.print(", String a" + prefix + j++);
                System.out.print(", int a" + prefix + j++);
                System.out.print(", long a" + prefix + j++);
                System.out.print(", String a" + prefix + j++);
                System.out.print(", int a" + prefix + j++);
                System.out.print(", long a" + prefix + j++);
                System.out.println(", String a" + prefix + j++);
            }
        }
    }

    private void generateArguments() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(", 0");
                j++;
                System.out.print(", 0");
                j++;
                System.out.print(", \"0\"");
                j++;
                System.out.print(", 0");
                j++;
                System.out.print(", 0");
                j++;
                System.out.print(", \"0\"");
                j++;
                System.out.print(", 0");
                j++;
                System.out.print(", 0");
                j++;
                System.out.println(", \"0\"");
                j++;
            }
        }
    }
}
