package test.reflect.proxy;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieStream;
import xyz.sunqian.common.reflect.proxy.asm.ClsProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AsmProxyTest {

    public void tt(Super sp) {
        sp.print();
    }

    @Test
    public void testAsmProxy() {
        tt(new Super());
        tt(new Child());
        Method[] methods = ClsProxy.class.getMethods();
        System.out.println(JieStream.stream(methods)
                .filter(m->!Jie.equals(m.getDeclaringClass(), Object.class))
            .map(m -> m + ": " + m.getDeclaringClass().getName() + "; " + m.isBridge()
                + Arrays.toString(m.getGenericParameterTypes()) + System.lineSeparator())
            .collect(Collectors.joining()));
    }
}
