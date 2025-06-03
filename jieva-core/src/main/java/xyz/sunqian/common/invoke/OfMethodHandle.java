package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.reflect.JieClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class OfMethodHandle implements InvocableGenerator {

    static @Nonnull Invocable forHandle(@Nonnull MethodHandle methodHandle, boolean isStatic) {
        return isStatic ? new ForStatic(methodHandle) : new ForInstance(methodHandle);
    }

    @Override
    public @Nonnull Invocable generate(@Nonnull Method method) {
        return JieClass.isStatic(method) ? new ForStatic(method) : new ForInstance(method);
    }

    @Override
    public @Nonnull Invocable generate(@Nonnull Constructor<?> constructor) {
        return new ForStatic(constructor);
    }

    private static final class ForInstance implements Invocable {

        private final @Nonnull MethodHandle methodHandle;

        private ForInstance(@Nonnull Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method).asType(buildMethodType(method));
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private ForInstance(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, @Nullable Object... args) {
            if (inst == null) {
                throw new InvocationException("The instance must be nonnull.");
            }
            try {
                return JieInvoke.invokeInstance(methodHandle, inst, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }
    }

    private static final class ForStatic implements Invocable {

        private final @Nonnull MethodHandle methodHandle;

        private ForStatic(@Nonnull Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method).asType(buildMethodType(method));
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private ForStatic(@Nonnull Constructor<?> constructor) {
            try {
                this.methodHandle = MethodHandles.lookup()
                    .unreflectConstructor(constructor)
                    .asType(buildMethodType(constructor));
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private ForStatic(@Nonnull MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, @Nullable Object... args) {
            try {
                return JieInvoke.invokeStatic(methodHandle, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }
    }

    private static MethodType buildMethodType(Method method) {
        Class<?>[] parameterTypes;
        if (JieClass.isStatic(method)) {
            parameterTypes = method.getParameterTypes();
        } else {
            parameterTypes = new Class<?>[method.getParameterCount() + 1];
            parameterTypes[0] = method.getDeclaringClass();
            System.arraycopy(
                method.getParameterTypes(), 0, parameterTypes, 1, method.getParameterCount());
        }
        return MethodType.methodType(method.getReturnType(), parameterTypes);
    }

    private static MethodType buildMethodType(Constructor<?> constructor) {
        return MethodType.methodType(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }
}
