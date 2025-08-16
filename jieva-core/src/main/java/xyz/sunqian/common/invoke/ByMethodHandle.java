package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.ClassKit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class ByMethodHandle {

    static final int MAX_STATIC_ARGS_NUM = 16;
    static final int MAX_INST_ARGS_NUM = 15;

    static @Nonnull Invocable newInvocable(@Nonnull Method method) {
        MethodHandle handle = methodHandle(method);
        boolean isStatic = ClassKit.isStatic(method);
        return newInvocable(handle, isStatic);
    }

    private static @Nonnull MethodHandle methodHandle(@Nonnull Method method) throws InvocationException {
        try {
            return MethodHandles.lookup()
                .unreflect(method)
                // .findVirtual(
                //     method.getDeclaringClass(),
                //     method.getName(),
                //     MethodType.methodType(method.getReturnType(), method.getParameterTypes())
                // )
                //.asType(buildMethodType(method))
                ;
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    static @Nonnull Invocable newInvocable(@Nonnull Constructor<?> constructor) {
        MethodHandle handle = constructorHandle(constructor);
        return newInvocable(handle, true);
    }

    private static @Nonnull MethodHandle constructorHandle(@Nonnull Constructor<?> constructor) throws InvocationException {
        try {
            return MethodHandles.lookup()
                .unreflectConstructor(constructor)
                // .findConstructor(
                //     constructor.getDeclaringClass(),
                //     MethodType.methodType(void.class, constructor.getParameterTypes())
                // );
                //.asType(buildMethodType(constructor))
                ;
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    static @Nonnull Invocable newInvocable(@Nonnull MethodHandle handle, boolean isStatic) {
        switch (handle.type().parameterCount()) {
            case 0:
                return new StaticInvoker0(handle);
            case 1:
                return isStatic ? new StaticInvoker1(handle) : new InstanceInvoker0(handle);
            case 2:
                return isStatic ? new StaticInvoker2(handle) : new InstanceInvoker1(handle);
            case 3:
                return isStatic ? new StaticInvoker3(handle) : new InstanceInvoker2(handle);
            case 4:
                return isStatic ? new StaticInvoker4(handle) : new InstanceInvoker3(handle);
            case 5:
                return isStatic ? new StaticInvoker5(handle) : new InstanceInvoker4(handle);
            case 6:
                return isStatic ? new StaticInvoker6(handle) : new InstanceInvoker5(handle);
            case 7:
                return isStatic ? new StaticInvoker7(handle) : new InstanceInvoker6(handle);
            case 8:
                return isStatic ? new StaticInvoker8(handle) : new InstanceInvoker7(handle);
            case 9:
                return isStatic ? new StaticInvoker9(handle) : new InstanceInvoker8(handle);
            case 10:
                return isStatic ? new StaticInvoker10(handle) : new InstanceInvoker9(handle);
            case 11:
                return isStatic ? new StaticInvoker11(handle) : new InstanceInvoker10(handle);
            case 12:
                return isStatic ? new StaticInvoker12(handle) : new InstanceInvoker11(handle);
            case 13:
                return isStatic ? new StaticInvoker13(handle) : new InstanceInvoker12(handle);
            case 14:
                return isStatic ? new StaticInvoker14(handle) : new InstanceInvoker13(handle);
            case 15:
                return isStatic ? new StaticInvoker15(handle) : new InstanceInvoker14(handle);
            case 16:
                return isStatic ? new StaticInvoker16(handle) : new InstanceInvoker15(handle);
            default:
                return forHandle(handle, isStatic);
        }
    }

    private static @Nonnull Invocable forHandle(@Nonnull MethodHandle methodHandle, boolean isStatic) {
        return isStatic ? new StaticInvoker(methodHandle) : new InstanceInvoker(methodHandle);
    }

    // public static void main(String[] args) {
    //     StringBuilder sb = new StringBuilder();
    //     for (int i = 1; i < 17; i++) {
    //         sb.append("case ").append(i).append(": ").append("return isStatic ? new StaticInvoker");
    //         sb.append(i).append("(handle) : new InstanceInvoker").append(i - 1).append("(handle);");
    //         sb.append(System.lineSeparator());
    //     }
    //     System.out.println(sb);
    // }

    private static final class StaticInvoker implements Invocable {

        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invokeWithArguments(args);
        }
    }

    private static final class InstanceInvoker implements Invocable {

        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invokeWithArguments(InvokeKit.toInstanceArgs(inst, args));
        }
    }

    private static final class StaticInvoker0 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker0(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke();
        }
    }

    private static final class StaticInvoker1 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker1(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0]);
        }
    }

    private static final class StaticInvoker2 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker2(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1]);
        }
    }

    private static final class StaticInvoker3 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker3(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2]);
        }
    }

    private static final class StaticInvoker4 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker4(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3]);
        }
    }

    private static final class StaticInvoker5 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker5(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4]);
        }
    }

    private static final class StaticInvoker6 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker6(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
        }
    }

    private static final class StaticInvoker7 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker7(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        }
    }

    private static final class StaticInvoker8 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker8(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        }
    }

    private static final class StaticInvoker9 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker9(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        }
    }

    private static final class StaticInvoker10 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker10(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
        }
    }

    private static final class StaticInvoker11 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker11(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
        }
    }

    private static final class StaticInvoker12 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker12(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
        }
    }

    private static final class StaticInvoker13 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker13(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
        }
    }

    private static final class StaticInvoker14 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker14(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
        }
    }

    private static final class StaticInvoker15 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker15(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
        }
    }

    private static final class StaticInvoker16 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private StaticInvoker16(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
        }
    }

    private static final class InstanceInvoker0 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker0(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst);
        }
    }

    private static final class InstanceInvoker1 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker1(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0]);
        }
    }

    private static final class InstanceInvoker2 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker2(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1]);
        }
    }

    private static final class InstanceInvoker3 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker3(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2]);
        }
    }

    private static final class InstanceInvoker4 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker4(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3]);
        }
    }

    private static final class InstanceInvoker5 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker5(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4]);
        }
    }

    private static final class InstanceInvoker6 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker6(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5]);
        }
    }

    private static final class InstanceInvoker7 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker7(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        }
    }

    private static final class InstanceInvoker8 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker8(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        }
    }

    private static final class InstanceInvoker9 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker9(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        }
    }

    private static final class InstanceInvoker10 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker10(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
        }
    }

    private static final class InstanceInvoker11 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker11(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
        }
    }

    private static final class InstanceInvoker12 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker12(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
        }
    }

    private static final class InstanceInvoker13 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker13(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
        }
    }

    private static final class InstanceInvoker14 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker14(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
        }
    }

    private static final class InstanceInvoker15 implements Invocable {
        private final @Nonnull MethodHandle methodHandle;

        private InstanceInvoker15(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
        }
    }

    // public static void main(String[] args) {
    //     StringBuilder sb = new StringBuilder();
    //     appendImpl(sb, 17, true);
    //     appendImpl(sb, 16, false);
    //     System.out.println(sb);
    // }
    //
    // private static void appendImpl(StringBuilder sb, int count, boolean isStatic) {
    //     String className = (isStatic ? "Static" : "Instance") + "Invoker";
    //     for (int i = 0; i < count; i++) {
    //         sb.append("private static final class ").append(className).append(i).append(" implements Invocable {");
    //         sb.append(System.lineSeparator());
    //         sb.append("private final @Nonnull MethodHandle methodHandle;");
    //         sb.append(System.lineSeparator());
    //         sb.append("private ").append(className).append(i).append("(@Nonnull MethodHandle methodHandle) {this.methodHandle = methodHandle;}");
    //         sb.append(System.lineSeparator());
    //         sb.append("@Override public @Nullable Object invokeChecked(@Nullable Object inst, @Nullable Object @Nonnull ... args) throws Throwable {");
    //         sb.append(System.lineSeparator());
    //         appendCall(sb, i, isStatic);
    //         sb.append(System.lineSeparator()).append("}");
    //         sb.append(System.lineSeparator()).append("}");
    //     }
    //     sb.append(System.lineSeparator());
    // }
    //
    // private static void appendCall(StringBuilder sb, int i, boolean isStatic) {
    //     sb.append("return methodHandle.invoke(");
    //     boolean isFirst = true;
    //     if (!isStatic) {
    //         sb.append("inst");
    //         isFirst = false;
    //     }
    //     for (int j = 0; j < i; j++) {
    //         if (!isFirst) {
    //             sb.append(",");
    //         }
    //         sb.append("args[").append(j).append("]");
    //         isFirst = false;
    //     }
    //     sb.append(");");
    // }
}
