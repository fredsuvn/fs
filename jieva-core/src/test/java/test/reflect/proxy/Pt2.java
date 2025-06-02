package test.reflect.proxy;

public interface Pt2 {

    default String ss2(String a, int b) {
        return a + b;
    }
}
