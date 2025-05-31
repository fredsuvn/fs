package test.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

import java.lang.reflect.Method;

public class PStringProxy extends PString {

    private final ProxyMethodHandler handler;
    private final Method[] methods;

    public PStringProxy(ProxyMethodHandler handler, Method[] methods) {
        this.handler = handler;
        this.methods = methods;
    }

    @Override
    public void mVoid() throws Throwable {
        handler.invoke(this, methods[0], new Invoker1());
    }

    @Override
    public int mInt(boolean a, byte b, short c, char d, int e, long f, float g, double h, Number i, String s) throws Throwable {
        return (Integer) handler.invoke(this, methods[0], new Invoker2(), a, b, c, e, f, g, h, i, s);
    }

    private class Invoker1 implements ProxyInvoker {

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            ((PStringProxy) inst).mVoid();
            return null;
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            PStringProxy.super.mVoid();
            return null;
        }
    }

    private class Invoker2 implements ProxyInvoker {

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return ((PStringProxy) inst).mInt(
                (Boolean) args[0],
                (Byte) args[1],
                (Short) args[2],
                (Character) args[3],
                (Integer) args[4],
                (Long) args[5],
                (Float) args[6],
                (Double) args[7],
                (Number) args[8],
                (String) args[9]
            );
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return PStringProxy.super.mInt(
                (Boolean) args[0],
                (Byte) args[1],
                (Short) args[2],
                (Character) args[3],
                (Integer) args[4],
                (Long) args[5],
                (Float) args[6],
                (Double) args[7],
                (Number) args[8],
                (String) args[9]
            );
        }
    }
}
