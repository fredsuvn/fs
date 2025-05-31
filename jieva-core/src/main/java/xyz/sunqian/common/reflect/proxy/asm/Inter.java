package xyz.sunqian.common.reflect.proxy.asm;

public interface Inter<T> {

    default T getInter(T t) throws Throwable {
        return null;
    }

    default <X> X getX(X x) throws Throwable {
        return null;
    }
}
