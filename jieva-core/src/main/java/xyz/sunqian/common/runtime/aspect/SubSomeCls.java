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
    public String s1(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
        Object[] args = new Object[]{a, p1, p2, p3, p4, p5, p6, p7, p8};
        try {
            aspectHandler.beforeInvoking(methods[0], args, this);
            String ret = super.s1((String) args[0], (Boolean) args[1], (Byte) args[2], (Character) args[3], (Short) args[4], (Integer) args[5], (Long) args[6], (Float) args[7], (Double) args[8]);
            return (String) aspectHandler.afterReturning(ret, methods[0], args, this);
        } catch (Throwable ex) {
            return (String) aspectHandler.afterThrowing(ex, methods[0], args, this);
        }
    }

    @Override
    public void s2(String a, boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
        Object[] args = new Object[]{a, p1, p2, p3, p4, p5, p6, p7, p8};
        try {
            aspectHandler.beforeInvoking(methods[0], args, this);
            super.s2((String) args[0], (Boolean) args[1], (Byte) args[2], (Character) args[3], (Short) args[4], (Integer) args[5], (Long) args[6], (Float) args[7], (Double) args[8]);
            aspectHandler.afterReturning(null, methods[0], args, this);
        } catch (Throwable ex) {
            aspectHandler.afterThrowing(ex, methods[0], args, this);
        }
    }
}
