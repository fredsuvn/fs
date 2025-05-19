package test.test;

import org.testng.annotations.Test;
import xyz.sunqian.common.test.SomeClass;
import xyz.sunqian.common.test.SomeException;

import static org.testng.Assert.expectThrows;

public class SomeTest {

    @Test
    public void testDoSomething() throws Exception {
        SomeClass.doSomething(() -> null);
        expectThrows(SomeException.class, () -> SomeClass.doSomething(() -> {
            throw new SomeException();
        }));
        expectThrows(SomeException.class, () -> SomeClass.doSomething(() -> {
            throw new RuntimeException();
        }));
    }
}
