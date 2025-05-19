package xyz.sunqian.common.test;

import java.util.concurrent.Callable;

public class SomeClass {

    public static void doSomething(Callable<?> call) throws SomeException {
        try {
            call.call();
        } catch (SomeException e) {
            throw e;
        } catch (Exception e) {
            throw new SomeException(e);
        }
    }
}
