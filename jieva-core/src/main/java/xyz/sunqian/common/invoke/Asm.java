package xyz.sunqian.common.invoke;

public class Asm {

    public static final class InstCaller implements Invocable {

        public Object invoke(Object inst, Object... args) throws InvocationException {
            try {
                String str = (String) inst;
                Integer i = (Integer) args[0];
                return str.substring(i);
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }
    }

    public static final class StaticCaller implements Invocable {

        public Object invoke(Object inst, Object... args) throws InvocationException {
            try {
                Integer i = (Integer) args[0];
                return String.valueOf(i);
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }
    }

    public static final class NewCaller implements Invocable {

        public Object invoke(Object inst, Object... args) throws InvocationException {
            try {
                char[] c = (char[]) args[0];
                return new String(c);
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }
    }
}
