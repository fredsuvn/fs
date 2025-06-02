package test.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

public class PtProxy extends Pt implements Pt2 {

    private final ProxyMethodHandler handler;
    private final Invoker invoker;

    public PtProxy(ProxyMethodHandler handler, Invoker invoker) {
        this.handler = handler;
        this.invoker = invoker;
    }

    @Override
    public String ss1(String a, int b) {
        try {
            return (String) handler.invoke(this, null, invoker, a, b);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String ss2(String a, int b) {
        try {
            return (String) handler.invoke(this, null, invoker, a, b);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final class Invoker implements ProxyInvoker {

        private final int index;

        private Invoker(int index) {
            this.index = index;
        }

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            switch (index) {
                case 1:
                    return ((Pt) inst).ss1((String) args[0], (Integer) args[1]);
                case 2:
                    return ((Pt2) inst).ss2((String) args[0], (Integer) args[1]);
                case 3:
                    return ((Pt) inst).ss1((String) args[0], (Integer) args[1]);
                case 4:
                    return ((Pt2) inst).ss2((String) args[0], (Integer) args[1]);
                case 5:
                    return ((Pt) inst).ss1((String) args[0], (Integer) args[1]);
                case 6:
                    return ((Pt2) inst).ss2((String) args[0], (Integer) args[1]);
            }
            return null;
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            // switch (index) {
            //     case 1:
            //         return PtProxy.super.ss1((String) args[0], (Integer) args[1]);
            //     case 2:
            //         return Pt2.this.ss2((String) args[0], (Integer) args[1]);
            //     case 3:
            //         return ((Pt) inst).ss1((String) args[0], (Integer) args[1]);
            //     case 4:
            //         return ((Pt2) inst).ss2((String) args[0], (Integer) args[1]);
            //     case 5:
            //         return ((Pt) inst).ss1((String) args[0], (Integer) args[1]);
            //     case 6:
            //         return ((Pt2) inst).ss2((String) args[0], (Integer) args[1]);
            // }
            return null;
        }
    }
}
