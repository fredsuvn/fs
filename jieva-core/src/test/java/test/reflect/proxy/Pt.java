package test.reflect.proxy;

public class Pt<T> {

    public void mVoid() throws Throwable {
    }

    public int mInt(
        boolean a,
        byte b,
        short c,
        char d,
        int e,
        long f,
        float g,
        double h,
        Number i,
        T t
    ) throws Throwable {
        return 666;
    }

    public T mTt(T t) throws Throwable {
        return null;
    }
}
