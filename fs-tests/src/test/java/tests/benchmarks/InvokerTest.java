package tests.benchmarks;

import internal.tests.common.Invoker;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class InvokerTest {

    @Test
    public void testInvoker() throws Exception {
        testInvoker("reflect");
        testInvoker("asm");
        testInvoker("method_handle");
        testInvoker("direct");
    }

    public void testInvoker(String invokeType) throws Exception {
        Supplier<Object> forStatic = Invoker.createAction(invokeType, "static");
        System.out.println(forStatic.get());
        Supplier<Object> forInstance = Invoker.createAction(invokeType, "instance");
        System.out.println(forInstance.get());
    }
}
