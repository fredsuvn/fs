package xyz.sunqian.common.invoke.asm;

import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.invoke.InvocationException;

public class InstCaller implements Invocable {

    // public static void main(String[] args) {
    //     Invocable i = new InstCaller();
    //     System.out.println(i.invoke("abc", 1));
    // }

    public Object invoke(Object inst, Object... args) throws InvocationException {
        try {
            String str = (String) inst;
            Integer i1 = (Integer) args[0];
            Integer i2 = (Integer) args[1];
            return str.substring(i1, i2);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }
}
