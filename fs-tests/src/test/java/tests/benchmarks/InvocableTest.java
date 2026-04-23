package tests.benchmarks;

import internal.api.Invoker;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class InvocableTest {

    @Test
    public void testInvocableWithDifferentImplementations() throws Exception {
        testInvocableImplementation("byReflect");
        testInvocableImplementation("byAsm");
        testInvocableImplementation("byMethodHandle");
        testInvocableImplementation("direct");
    }

    private void testInvocableImplementation(String invokeType) throws Exception {
        // Test static method invocation
        Supplier<Object> staticSupplier = Invoker.createSupplier(invokeType, "static");
        System.out.println("Static invocation result: " + staticSupplier.get());

        // Test instance method invocation
        Supplier<Object> instanceSupplier = Invoker.createSupplier(invokeType, "instance");
        System.out.println("Instance invocation result: " + instanceSupplier.get());
    }
}
