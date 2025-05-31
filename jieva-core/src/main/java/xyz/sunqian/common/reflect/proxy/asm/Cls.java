package xyz.sunqian.common.reflect.proxy.asm;

public class Cls<T> {

    public T getCls(T t) throws Throwable {
        return t;
    }

    public <X> X getClsX(X x) throws Throwable {
        return x;
    }

    public int mInt(boolean a, byte b, short c, char d, int e, long f, float g, double h, String s) throws Throwable {
        return 0;
    }
}
