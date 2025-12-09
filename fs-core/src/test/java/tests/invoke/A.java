package tests.invoke;

public class A {

    public static String staticMethod(String s) {
        return s;
    }

    private static String staticPrivateMethod(String s) {
        return s;
    }

    public static String staticThrowMethod(String s) {
        throw new InvokeTestException();
    }

    public A() {
    }

    private A(int i) {
    }

    public A(long l) {
        throw new InvokeTestException();
    }

    public String instanceMethod(String s) {
        return s;
    }

    private String instancePrivateMethod(String s) {
        return s;
    }

    public String instanceThrowMethod(String s) {
        throw new InvokeTestException();
    }
}
