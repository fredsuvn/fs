package xyz.sunqian.common.reflect.proxy.asm;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

import java.lang.reflect.Method;

public class ClsProxy extends Cls2 {

    private final ProxyMethodHandler handler;
    private final Method[] methods;
    private final ProxyInvoker[] invokers;

    public ClsProxy(ProxyMethodHandler handler, Method[] methods) {
        this.handler = handler;
        this.methods = methods;
        this.invokers = new ProxyInvoker[methods.length];
    }

    @Override
    public Long getInter(Long aLong, int i, long l, float f) throws Throwable {
        ProxyInvoker invoker = invokers[0];
        if (invoker == null) {
            invoker = new Invoker1(0);
            invokers[0] = invoker;
        }
        return (Long) handler.invoke(this, methods[0], invoker, aLong, i, l, f);
    }

    @Override
    public <X> X getX(X x) throws Throwable {
        return super.getX(x);
    }

    @Override
    public int mInt(boolean a, byte b, short c, char d, int e, long f, float g, double h, String s) throws Throwable {
        return super.mInt(a, b, c, d, e, f, g, h, s);
    }

    private class Invoker1 implements ProxyInvoker {

        private final int index;

        Invoker1(int index) {
            this.index = index;
        }

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            ClsProxy _this = (ClsProxy) inst;
            switch (index) {
                case 0:
                    return _this.mInt(
                        (Boolean) args[0],
                        (Byte) args[1],
                        (Short) args[2],
                        (Character) args[3],
                        (Integer) args[4],
                        (Long) args[5],
                        (Float) args[6],
                        (Double) args[7],
                        (String) args[8]
                    );
                case 1:
                    return _this.getInter((Long) args[0], (Integer) args[1], (Long) args[2], (Float) args[3]);
                case 2:
                    _this.wait();
                    return null;
                case 3:
                    return _this.mInt(
                        (Boolean) args[0],
                        (Byte) args[1],
                        (Short) args[2],
                        (Character) args[3],
                        (Integer) args[4],
                        (Long) args[5],
                        (Float) args[6],
                        (Double) args[7],
                        (String) args[8]
                    );
            }
            return null;
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            switch (index) {
                case 1:
                    return ClsProxy.super.getInter((Long) args[0], (Integer) args[1], (Long) args[2], (Float) args[3]);
                case 2:
                    return ClsProxy.super.getInter((Long) args[0], (Integer) args[1], (Long) args[2], (Float) args[3]);
            }
            return null;
        }
    }
}
