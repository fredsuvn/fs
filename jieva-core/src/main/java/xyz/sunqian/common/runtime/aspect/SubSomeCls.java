package xyz.sunqian.common.runtime.aspect;

import java.lang.reflect.Method;

public class SubSomeCls extends SomeCls {

    private final AspectHandler aspectHandler;
    private final Method[] methods;

    public SubSomeCls(AspectHandler aspectHandler) {
        this.aspectHandler = aspectHandler;
        this.methods = getClass().getMethods();
    }

    @Override
    public String s1(String a) {
        Object[] args = new Object[]{a};
        try {
            aspectHandler.beforeInvoking(methods[0], args, this);
            String ret = super.s1((String) args[0]);
            return (String) aspectHandler.afterReturning(ret, methods[0], args, this);
        } catch (Throwable ex) {
            return (String) aspectHandler.afterThrowing(ex, methods[0], args, this);
        }
    }

    @Override
    public void s2(String a) {
        Object[] args = new Object[]{a};
        try {
            aspectHandler.beforeInvoking(methods[0], args, this);
            super.s2((String) args[0]);
            aspectHandler.afterReturning(null, methods[0], args, this);
        } catch (Throwable ex) {
            aspectHandler.afterThrowing(ex, methods[0], args, this);
        }
    }
}
