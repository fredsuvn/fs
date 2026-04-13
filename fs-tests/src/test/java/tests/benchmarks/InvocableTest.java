package tests.benchmarks;

import internal.api.Invoker;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class InvocableTest {

    @Test
    public void testInvoker() throws Exception {
        testInvoker("byReflect");
        testInvoker("byAsm");
        testInvoker("byMethodHandle");
        testInvoker("direct");
    }

    public void testInvoker(String invokeType) throws Exception {
        Supplier<Object> forStatic = Invoker.createSupplier(invokeType, "static");
        System.out.println(forStatic.get());
        Supplier<Object> forInstance = Invoker.createSupplier(invokeType, "instance");
        System.out.println(forInstance.get());
    }
}
